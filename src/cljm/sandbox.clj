(ns cljm.sandbox
  (:use cljm.core)
  (:use cljm.notation)
  (:use cljm.filters)
  (:use overtone.core)
  (:use overtone.inst.sampled-piano)
  (:use overtone.examples.synthesis.fm))

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

;; Adapted from overtone.examples.synthesis.fm
(definst fminst [note 40 divisor 2.0 depth 1.0 out-bus 0 gate 1 attack 1 decay 1 sustain 1 release 1 level 1 curve -4]
  (let [carrier (midicps note)
        modulator (/ carrier divisor)
        mod-env   (env-gen (lin-env attack sustain release))
        amp-env   (env-gen (adsr attack decay sustain release level curve) 
                           :gate gate
                           :action FREE)]
    (pan2 (* 0.5 amp-env
                 (sin-osc (+ carrier
                             (* mod-env  (* carrier depth) (sin-osc modulator))))))))

