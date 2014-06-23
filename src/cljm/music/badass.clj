(ns cljm.music.badass
  (:use cljm.core)
  (:use cljm.notation.staff)
  (:use overtone.core)
  (:use overtone.inst.synth))
  
(defmacro deffminst [name]
  `(definst ~name [~'note 40 ~'divisor 2.0 ~'depth 1.0 ~'out-bus 0 ~'gate 1 ~'attack 0.1 ~'decay 1 ~'sustain 1 ~'release 0.1 ~'level 1 ~'curve -4]
    (let [~'carrier (midicps ~'note)
          ~'modulator (~'/ ~'carrier ~'divisor)
          ~'env   (env-gen (adsr ~'attack ~'decay ~'sustain ~'release ~'level ~'curve) 
                         :gate ~'gate
                         :action FREE)]
      (pan2 (~'* 0.5 ~'env
                   (sin-osc (~'+ ~'carrier
                               (~'* ~'env  (~'* ~'carrier ~'depth) (sin-osc ~'modulator)))))))))

(deffminst i1)
(inst-fx! i1 fx-chorus)
(inst-fx! i1 fx-distortion)

(deffminst i2)

(def mainhook (with [:attack 0 :depth 3.0 :level 2] (with-inst i1 
  (staff [ "A5- A4- A5-   - | - A5- A4--  | A4- A3- C4. . A3. ."
           "  -   - C4- D4- | -   - - A3- |   - - - - "  ]))))

(def ambient (with-inst i2
  (with [:attack 5 :release 5 :level 0.5]
    (staff [ "A2------------------------------------------------" 
           [ "A1------------------------------------------------" ]]))))

(def build-phrase
  (with [:amp 0.1]
    (staff ["A2-- A1-- A2-- C2-- A2-- D2-- A2-- E2--"]
           ["A2-- G2-- A2-- A3-- A2-- C3-- A2-- D3--"])))

(def build (with-inst daf-bass
  (phrase 
    build-phrase 
    (with [:amp 0.15] build-phrase)
    (with [:amp 0.2] (staff "A2-- E2------")))))

(def badass
  (score
    (phrase (repeat 3 ambient))                           ; 72
    (phrase (rest-for 16) build)                          ; 52
    (phrase (rest-for 54) (phrase (repeat 8 mainhook))))) ; 102
