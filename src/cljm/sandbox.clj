(ns cljm.sandbox
  (:use cljm.core)
  (:use cljm.notation)
  (:use cljm.filters)
  (:use overtone.core)
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

(definst subby []
  (pan2 (play-buf 1 (load-sample (freesound-path 25649)))))
(inst-fx! subby fx-distortion)
(add-filter (f-inst subby) :subby)






