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

(def test-notes
  (list {:at 1 :inst saw-wave :params []}
        {:at 2 :inst saw-wave :params []}
        {:at 3 :inst saw-wave :params []}
        {:at 4 :inst saw-wave :params []}
        {:at 5 :inst saw-wave :bpm 240 :params []}
        {:at 6 :inst saw-wave :params []}
        {:at 7 :inst saw-wave :params []}
        {:at 8 :inst saw-wave :params []}))
