(ns cljm.player
  (:use cljm.core)
  (:use overtone.core))

(defn- play-note [note m]
  (if (and (note? note) (inst? (:inst note)))
    (let [i (:inst note)
          p (:params note)]
      ;; play instrument i with parameters p
      (let [n (apply i p)]
        ;; apply temporal parameters t
        (doall
          (for [t (:tparams note)]
            (let [a (+ (first t) (:at note))
                  q (rest t)]
              ;; control instrument note n 
              ;; with parameters q at beat a
              (apply-at (m a) ctl (cons n q)))))))))

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
                        (if (time? %) (metro-bpm m (:bpm %)))
                        (if (note? %)
                          (apply-at (m (:at %)) play-note [% m])))
                     sched-now))
         ; come back to schedule more when we play the first note
         (apply-at (m next-beat) play [sched-later m]))))))


