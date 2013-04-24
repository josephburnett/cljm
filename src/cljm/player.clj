(ns cljm.player
  (:use cljm.core)
  (:use overtone.core))

(defn- apply-note-filters [note]
  (let [h (if (nil? (:handle (meta note))) :default (:handle (meta note)))
        n (reduce #(if (note? %1) (%2 %1)) note (h @CLJM-NOTE-FILTERS))]
    (if (note? n) n note)))

(defn- play-note [note m]
  (if (note? note)
    (let [n (apply-note-filters note)
          i (:inst n)
          p (:params n)]
      (if (inst? i)
        ;; play instrument i with parameters p
        (let [node (apply i p)]
          ;; apply temporal parameters t
          (doall
            (for [t (:tparams n)]
              (let [a (+ (first t) (:at n))
                    q (rest t)]
                ;; control instrument note n 
                ;; with parameters q at beat a
                (apply-at (m a) ctl (cons node q))))))))))

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
           [sched-now sched-later] (split-with #(> pivot (:at %)) curr-notes)
           ; control handle for scheduled notes
           handle (:handle (meta notes))]
       (do
         ; schedule notes until one beat after the first note
         (doall (map #(do
                        ; adjust tempo and schedule the note
                        (if (time? %) (metro-bpm m (:bpm %)))
                        (if (note? %)
                          (apply-at (m (:at %)) play-note [(with-meta % {:handle handle}) m])))
                     sched-now))
         ; come back to schedule more when we play the first note
         (apply-at (m next-beat) play [(with-meta sched-later {:handle handle}) m]))))))

(defn play-with
  [inst notes]
  (play (with-inst inst notes)))
