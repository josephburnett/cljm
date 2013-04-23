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
  ([bars] (align bars 1 1)) ; play now
  ([bars on of]
    (let [curr-beat (CLJM-LIVE-METRO)
          last-zero (- curr-beat (mod curr-beat of))
          first-at (+ on (- of 1) last-zero)]
      (with-meta
        (map #(assoc % :at (+ first-at (:at %))) bars)
        (meta bars)))))

(defn play-on
  [on of bars]
  (play (align bars on of) CLJM-LIVE-METRO))

(defn play-now [bars]
  (play (align bars) CLJM-LIVE-METRO))

(def ll lazy-loop)
(def po play-on)
(def pn play-now)
