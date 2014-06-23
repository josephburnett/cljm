(ns cljm.music.badass
  (:use cljm.core)
  (:use cljm.notation.staff)
  (:use overtone.core))
  
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

(def mainhook (with [:attack 0 :depth 3.0] (with-inst fminst 
  (staff [ "A5- A4- A5-   - | - A5- A4--  | A4- A3- C4. . A3. ."
           "  -   - C4- D4- | -   - - A3- |   - - - - "  ]))))
