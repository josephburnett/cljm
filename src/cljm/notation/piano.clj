(ns cljm.notation.piano
  (:use cljm.core)
  (:use overtone.core))

(defn notes [& n] (map note n))

(def sustain (partial tparam :gate))
