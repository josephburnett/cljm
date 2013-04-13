(ns cljm.sandbox
  (:use overtone.live)
  (:use cljm.notation)
  (:use cljm.notation.piano)
  (:use overtone.inst.piano)
  (:use overtone.inst.sampled-piano))

(definst kick [freq 120 dur 0.3 width 0.5]
    (let [freq-env (* freq (env-gen (perc 0 (* 0.99 dur))))
                  env (env-gen (perc 0.01 dur) 1 1 0 1 FREE)
                  sqr (* (env-gen (perc 0 0.01)) (pulse (* 2 freq) width))
                  src (sin-osc freq-env)
                  drum (+ sqr (* env src))]
              (compander drum drum 0.2 1 0.1 0.01 0.01)))

(definst c-hat [amp 0.8 t 0.04]
    (let [env (env-gen (perc 0.001 t) 1 1 0 1 FREE)
                  noise (white-noise)
                  sqr (* (env-gen (perc 0.01 0.04)) (pulse 880 0.2))
                  filt (bpf (+ sqr noise) 9000 0.5)]
              (* amp env filt)))

(def p
  (bar 4 sampled-piano
    [1 2 4]
    (notes :c4 :d4)
    (sustain 4 :4 :2 :4)))
