(ns cljm.notation.core
  (:require [cljm.core :refer [phrase score]]))

(declare read-phrase)
(declare read-score)

(defn read-phrase
  [string-bar-fn & notes]
  (println notes)
  (phrase (map #(if (coll? %)
                  (apply read-score string-bar-fn %)
                  (string-bar-fn %))
               notes)))

(defn read-score
  [string-bar-fn & notes]
  (println notes)
  (score (map #(if (coll? %)
                 (apply read-phrase string-bar-fn %)
                 (string-bar-fn %))
              notes)))

(defmacro defphrasenotation
  [notation-name string-bar-fn]
  `(defn ~notation-name
     [& notes#]
     (apply read-phrase (cons ~string-bar-fn notes#))))