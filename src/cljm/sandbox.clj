(ns cljm.sandbox
  (:use overtone.live))

;; We use a saw-wave that we defined in the oscillators tutorial
(definst saw-wave [freq 440 attack 0.01 sustain 0.4 release 0.1 vol 0.4] 
  (* (env-gen (lin-env attack sustain release) 1 1 0 1 FREE)
     (saw freq)
     vol))

(definst square-wave [freq 440 attack 0.01 sustain 0.4 release 0.1 vol 0.4] 
  (* (env-gen (lin-env attack sustain release) 1 1 0 1 FREE)
     (lf-pulse freq)
     vol))

(def metro (metronome 120))

(defn play [note]
  (let [f (:inst note)
        p (:params note)]
    (if (inst? f)
      (apply f p))))

(defn player
  ([notes] (player notes (metronome 120)))
  ([notes m]
   (if (empty? notes)
     nil ; nothing to do
     (let [next-note (first notes)
           pivot (inc (:at next-note))
           [sched-now sched-later] (split-with #(> pivot (:at %)) notes)]
       (do
         ; schedule notes until one beat after the first note
         (doall (map #(do
                        (if (contains? % :bpm) (metro-bpm m (:bpm %)))
                        (apply-at (m (:at %)) play [%])) 
                     sched-now))
         ; come back to schedule more when we play the first note
         (apply-at (m (:at next-note)) player [sched-later m])))))) 

(defn bar
  [inst beats & rest-params]
  (let [notes (for [b beats] {:at b :inst inst :params []})]
    ; apply parameter seqs to notes
    (reduce (fn [n p]
              (if (keyword? (first p))
                (map #(assoc %1 :params (cons (first p) (cons %2 (:params %1))))
                     n (cycle (rest p)))
                (map #(assoc %1 :params (cons %2 (:params %1))) 
                     n (cycle p))))
            notes
            rest-params)))

(defn phrase
  [bar-length bars]
  (flatten
    (map (fn [m i]
           (map #(assoc % :at (+ (* i bar-length) (:at %))) m))
         bars
         (range))))

(def test-measure
  (bar "saw-wave"
       [1 2 3 4 5 6 7 8] 
       [:sustain 0.1 0.4]
       [:freq 440 220 440]))


