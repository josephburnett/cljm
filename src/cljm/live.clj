(ns cljm.live
  (:use cljm.core)
  (:use cljm.player)
  (:use overtone.core))

(def CLJM-LIVE-METRO (metronome 120))

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
(def pp play-on)

