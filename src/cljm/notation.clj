(ns cljm.notation
  (:use overtone.core))

(defn notes [n] (map note n))

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
                        (if (contains? % :bpm) (metro-bpm m (:bpm %)))
                        (apply-at (m (:at %)) play-note [%])) 
                     sched-now))
         ; come back to schedule more when we play the first note
         (apply-at (m next-beat) play [sched-later m]))))))

(defn bar
  [beat-length inst beats & rest-params]
  (let [notes (for [b beats] {:at b :inst inst :params []})]
    ; apply parameter seqs to build complete notes
    (with-meta 
      (reduce (fn [n p]
                (if (keyword? (first p))
                  (map #(assoc %1 :params (cons (first p) (cons %2 (:params %1))))
                       n (cycle (rest p)))
                  (map #(assoc %1 :params (cons %2 (:params %1))) 
                       n (cycle p))))
              notes
              rest-params)
      {:beat-length beat-length})))

(defn- running-index
  ([coll] (cons 0 (running-index 0 (drop-last coll))))
  ([index coll]
    (if (empty? coll)
      '()
      (cons (first coll) (add (first coll) (rest coll))))))

(defn phrase
  [bars]
  (let [beats (map #(:beat-length (meta %)) bars)]
    (with-meta
      (flatten
        (map (fn [b i]
               (let [beat-length (:beat-length (meta b))]
                 (map #(assoc % :at (+ i (:at %))) b)))
             bars
             (running-index beats)))
      {:beat-length (reduce + beats)})))


