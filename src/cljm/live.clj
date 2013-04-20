(ns cljm.live
  (:use cljm.core))

(defn lazy-loop
  "Returns an infinite sequence of bars."
  ([bars] (lazy-loop bars 0))
  ([bars index]
    (lazy-cat
      (map #(assoc %1 :at (+ index (:at %))) bars)
      (lazy-loop bars (+ index (:beat-length (meta bars)))))))

(defn on-of
  [on of metro bars]
  (let [curr-beat (metro)
        last-zero (- curr-beat (mod curr-beat of))
        first-at (+ on of last-zero)]
    (with-meta
      (map #(assoc %1 :at (+ first-at (:at %))) bars)
      (meta bars))))
