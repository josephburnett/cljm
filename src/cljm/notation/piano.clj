(ns cljm.notation.piano
  (:use overtone.core))

(defn notes [n] (map note n))

(def times
  {:_1 1.0
   :_2 2.0
   :_3 3.0
   :_4 4.0
   :2 0.5
   :4 0.25
   :8 0.125})
 
(defn sustain [beat-length & t]
  (cons :at
    (map #(if (and (keyword? %1) (%1 times))
            ;; t is a fraction of the bar's beat-length
            (list (* (%1 times) beat-length) :gate 0)
            ;; default to one beat
            (list 1 :gate 0))
          t)))