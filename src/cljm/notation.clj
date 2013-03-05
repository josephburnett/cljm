(ns cljm.notation
  (:use overtone.core))

(defn notes [n] (map note n))

(defrecord Note [at inst params])
(defrecord Beat [at bpm])

(defn note? [o] (= cljm.notation.Note (type o)))
(defn beat? [o] (= cljm.notation.Beat (type o))) 

(defn play-note [note]
  (let [f (:inst note)
        p (:params note)]
    (if (inst? f)
      (apply f p))))

(defn play
  ([notes] (play notes (metronome 120)))
  ([notes m]
   (if (empty? notes)
     nil ; nothing to do
     (let [curr-beat (m)
           ; fast-forward to the current beat
           curr-notes (drop-while #(> curr-beat (:at %)) notes)
           next-beat (:at (first curr-notes))
           ; separate notes to be scheduled now and later
           pivot (inc next-beat)
           [sched-now sched-later] (split-with #(> pivot (:at %)) curr-notes)]
       (do
         ; schedule notes until one beat after the first note
         (doall (map #(do
                        ; adjust tempo and schedule the note
                        (if (beat? %) (metro-bpm m (:bpm %)))
                        (if (note? %)
                          (apply-at (m (:at %)) play-note [%])))
                     sched-now))
         ; come back to schedule more when we play the first note
         (apply-at (m next-beat) play [sched-later m]))))))

(defn bar
  [beat-length bpm inst beats & rest-params]
  (let [notes (for [b beats] (->Note b inst []))]
    ; apply parameter seqs to build complete notes
    (with-meta
      (merge-bars
        (reduce (fn [n p]
                  (if (keyword? (first p))
                    (map #(assoc %1 :params (cons (first p) (cons %2 (:params %1))))
                         n (cycle (rest p)))
                    (map #(assoc %1 :params (cons %2 (:params %1))) 
                         n (cycle p))))
                notes
                rest-params)
        ; mix in the beats
        (map #(->Beat % bpm) (range 1 (inc beat-length))))
      {:beat-length beat-length})))

(defn- running-index
  ([coll] (cons 0 (running-index 0 (drop-last coll))))
  ([last-val coll]
    (if (empty? coll)
      '()
      (let [next-val (+ last-val (first coll))]
        (cons next-val (running-index next-val (rest coll)))))))

(defn phrase
  ([b & bars] (phrase (cons b bars)))
  ([bars]
    (let [beats (map #(:beat-length (meta %)) bars)]
      (with-meta
        (flatten
          (map (fn [b i]
                 (let [beat-length (:beat-length (meta b))]
                   (map #(assoc % :at (+ i (:at %))) b)))
               bars
               (running-index beats)))
        {:beat-length (reduce + beats)}))))

(defn- merge-bars
  ([a b] (reverse (merge-bars a b '())))
  ([a b c]
   (cond
     ; done
     (and (empty? a) (empty? b)) c
     (empty? a) (concat (reverse b) c)
     (empty? b) (concat (reverse a) c)
     ; smallest goes first
     (< (:at (first a)) (:at (first b)))
       (merge-bars (rest a) b (cons (first a) c))
     :else
       (merge-bars a (rest b) (cons (first b) c)))))

(defn score
  ([b & bars] (score (cons b bars)))
  ([bars]
    (with-meta
      (reduce merge-bars '() bars)
      {:beat-length (reduce max (map #(:beat-length (meta %)) bars))})))
  
