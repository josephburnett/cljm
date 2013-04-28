(ns cljm.sandbox
  (:use cljm.core)
  (:use cljm.notation)
  (:use cljm.filters)
  (:use overtone.inst.sampled-piano))

(def base (staff [ C2-- C3- . G2. +A2-- C3- - ]))

(def blues-change
  (with-inst sampled-piano
    (phrase
      (phrase (repeat 2 base))
      (up 5 base)
       base
      (up 7 base)
      (up 5 base)
      (phrase (repeat 2 base)))))
 
