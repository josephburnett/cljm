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
(definst fminst [note 40 divisor 2.0 depth 1.0 out-bus 0 gate 1 attack 0.1 decay 1 sustain 1 release 0.1 level 1 curve -4]
  (let [carrier (midicps note)
        modulator (/ carrier divisor)
        env   (env-gen (adsr attack decay sustain release level curve) 
                       :gate gate
                       :action FREE)]
    (pan2 (* 0.5 env
                 (sin-osc (+ carrier
                             (* env  (* carrier depth) (sin-osc modulator))))))))

;; (inst-fx! fminst fx-chorus)
;; (inst-fx! fminst fx-echo)
;; (inst-fx! fminst fx-distortion)

(definst hat-in []
  (pan2 (play-buf 1 (load-sample (freesound-path 109723)))))

(definst hat-out[]
  (pan2 (play-buf 1 (load-sample (freesound-path 109724)))))

(definst fourty-four-pedal-hi-hat []
  (pan2 (play-buf 1 (load-sample (freesound-path 22747)))))

(definst dodgy-c-wood-block []
  (pan2 (play-buf 1 (load-sample (freesound-path 53387)))))

(definst low-tone-with-ringmod []
  (pan2 (play-buf 1 (load-sample (freesound-path 38718)))))

(def baseline (with [:attack 0 :depth 3.0] (with-inst fminst 
  (staff [ A3- A2- A3-   - | - A3- A2--  | A2- A1- C2. . A1. . ]
         [   -   - C2- D2- | -   - - A1- |   - - - -   ]))))

;; (play (l (up 24 baseline)))

