(ns cljm.notation.piano
  (:use cljm.core)
  (:use cljm.notation)
  (:use overtone.core))

(defn notes [& n] (map note n))

(defn- sum-time [t]
  (reduce #(+ %1 (case %2
                  \- 1/4
                  \. 1/16
                  \_ 1/64))
           0 (filter #(contains? #{ \- \. \_ } %1) t)))

(defn- count-time-in [term]
  (let [t (take-while #(not (= \| %1)) term)]
    (sum-time t)))

(defn- count-time-total [term]
  (sum-time term))

(defn- term-accidental [term]
  (reduce #(+ %1 (case %2
                   \o -1
                   \+ 1))
           0 (filter #(contains? #{ \o \+ } %1) term)))

(defn- term-note [term]
  (let [n #{ \A \B \C \D \E \F \G
             \1 \2 \3 \4 \5 \6 \7 \8 \9 \0}
        t (filter #(contains? n %1) term)]
    (+ (note (keyword (apply str t)))
       (term-accidental term))))

(defn term-bar [term]
  (bar (count-time-in term) nil [1]
       [:note (term-note term)]
       [:at [(count-time-total term) :gate 0]]))
       


