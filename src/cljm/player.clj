(ns cljm.player
  (:use cljm.core)
  (:use overtone.core))

(def CLJM-CHANNELS (atom {:default {:filters [] :clear false :mute false}}))

(def CLJM-LIVE-METRO (metronome 120))

(defn play-note [note m c]
  (let [channel (c @CLJM-CHANNELS)]
    (if (nil? channel) nil ; do nothing
      (let [;; apply note filters
            n (reduce #(if (note? %1) (%2 %1)) note (:filters channel))]
        (if (and (note? n) (inst? (:inst n)) ; we have a playable result
                 (false? (:mute channel))    ; and our channel is not muted
                 (false? (:clear channel)))  ; or cleared
          ;; play instrument i with parameters p
          (let [i (:inst n)
                p (:params n)
                node (apply i p)]
            ;; apply temporal parameters t
            (doall
               (for [t (:tparams n)]
                 (let [a (+ (first t) (:at n))
                       q (rest t)]
                   ;; control instrument note n 
                   ;; with parameters q at beat a
                   (apply-at (m a) ctl (cons node q)))))
            node)))))) ; return the synth node

(defn play
  ([notes] (play notes (metronome 120)))
  ([notes m] (play notes m :default))
  ([notes m c]
    (let [channel (c @CLJM-CHANNELS)]
      (if (or (nil? channel) (= true (:clear channel)) (empty? notes))
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
                             (apply-at (m (:at %)) play-note [% m c])))
                        sched-now))
            ; come back to schedule more when we play the first note
            (apply-at (m next-beat) play [sched-later m c])))))))

(defn lazy-loop
  "Returns an infinite sequence of bars."
  ([bars] (lazy-loop bars 0))
  ([bars index]
    (lazy-cat
      (map #(assoc %1 :at (+ index (:at %))) bars)
      (lazy-loop bars (+ index (:beat-length (meta bars)))))))

(defn align
  ([bars] (align bars 1 1)) ; on 1 of 1 is the next beat
  ([bars on of]
    (let [curr-beat (CLJM-LIVE-METRO)
          last-zero (- curr-beat (mod curr-beat of))
          first-at (+ on (- of 1) last-zero)]
      (with-meta
        (map #(assoc % :at (+ first-at (:at %))) bars)
        (meta bars)))))

(defn play-on
  ;; play now
  ([bars] (play-on 1 1 bars))
  ([bars handle] (play-on 1 1 bars handle))
  ;; play on a specific beat
  ([on of bars]
    ;; let play use the :default handle
    (play (align bars on of) CLJM-LIVE-METRO))
  ([on of bars handle]
    (play (align bars on of) CLJM-LIVE-METRO handle)))

(def ll lazy-loop)
(def po play-on)

(defn play-in
  ([] (play-in :default))
  ([channel]
    (midi-poly-player 
      (fn [_ note _ amp _ vel]
        (play-note (->Note (CLJM-LIVE-METRO) nil (list :note note :level amp) [])
                   CLJM-LIVE-METRO
                   channel)))))


