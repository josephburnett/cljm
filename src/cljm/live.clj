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

(defn on-of
  [on of bars]
  (let [curr-beat (CLJM-LIVE-METRO)
        last-zero (- curr-beat (mod curr-beat of))
        first-at (+ on of last-zero)]
    (with-meta
      (map #(assoc %1 :at (+ first-at (:at %))) bars)
      (meta bars))))

(defn update
  [bars]
  (let [curr-beat (CLJM-LIVE-METRO)]
    (with-meta
      (map #(assoc % :at (+ curr-beat (:at %))) bars)
      (meta bars))))

(defn live-play [bars]
  (play (update bars) CLJM-LIVE-METRO))

(def ll lazy-loop)
(def oo on-of)
(def lp live-play)
