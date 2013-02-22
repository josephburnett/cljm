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

(defn play [note m]
  (println note))

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
         (doall (map #(apply-at (m (:at %)) play [% m]) sched-now))
         ; come back to schedule more when we play the first note
         (apply-at (m (:at next-note)) player [sched-later m])))))) 

(def test-notes
  '({:at 4}
    {:at 5}
    {:at 6}
    {:at 7}
    {:at 7.5}
    {:at 8}))
