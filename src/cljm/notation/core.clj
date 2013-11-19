(ns cljm.notation.core
  (:require [cljm.core :refer [phrase score]]))

(declare read-phrase)
(declare read-score)

(defn read-phrase
  [string-bar-fn & notes]
  (phrase (map #(if (coll? %)
                  (apply read-score string-bar-fn %)
                  (string-bar-fn %))
               notes)))

(defn read-score
  [string-bar-fn & notes]
  (score (map #(if (coll? %)
                 (apply read-phrase string-bar-fn %)
                 (string-bar-fn %))
              notes)))
