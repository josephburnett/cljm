(ns cljm.player
  (:use cljm.core)
  (:use overtone.core))

(def CLJM-NOTE-FILTERS (atom {:default []}))

(defn- play-note [note m h]
  (let [;; apply note filters
        n (reduce #(if (note? %1) (%2 %1)) note (h @CLJM-NOTE-FILTERS))]
    (if (and (note? n) (inst? (:inst n))))
      (let [i (:inst n)
            p (:params n)]
        ;; play instrument i with parameters p
        (let [node (apply i p)]
          ;; apply temporal parameters t
          (doall
            (for [t (:tparams n)]
              (let [a (+ (first t) (:at n))
                    q (rest t)]
                ;; control instrument note n 
                ;; with parameters q at beat a
                (apply-at (m a) ctl (cons node q)))))))))

(defn play
  ([notes] (play notes (metronome 120)))
  ([notes m] (play notes m :default))
  ([notes m h]
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
                          (apply-at (m (:at %)) play-note [% m h])))
                     sched-now))
         ; come back to schedule more when we play the first note
         (apply-at (m next-beat) play [(with-meta sched-later {:handle handle}) m]))))))

(defn play-with
  [inst notes]
  (play (with-inst inst notes)))
