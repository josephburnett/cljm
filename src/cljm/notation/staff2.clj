(ns cljm.notation.staff2
  (:require [cljm.core :refer [bar phrase score]]
            [clearley.core :refer [build-parser execute]]
            [clearley.match :refer [defmatch]]
            [clearley.lib :refer :all]
            [overtone.core]))

(defmatch note
  ([(k note-key) (o note-octave)]
    (overtone.core/note (str k o)))
  ([(k note-key) (o note-octave) (m note-modifier)]
    (+ m (overtone.core/note (str k o)))))
  
(defmatch note-key
  [(k '(:or \A \B \C \D \E \F \G))] 
  k)

(defmatch note-octave
  [(o '(:or \1 \2 \3 \4 \5 \6 \7 \8))]
  o)

(defmatch note-modifier
  ([\+] 1)
  ([\o] -1))

(defmatch time-stop [\|])

(defmatch time-inc
  ([\-] 1/2)
  ([\.] 1/4)
  ([\_] 1/8)
  ([\*] 1/16))

(defmatch whitespace
  (['(:or \space \tab \newline \return)])
  ([whitespace '(:star whitespace)]))

(defn one-note-seq
  [n t-in t-out]
  (bar t-in nil [1] [:note n] [:at [(+ t-in t-out) :gate 0]]))

(defn zero-note-seq
  [t] 
  (bar t nil []))

(defmatch term
  ([(n note) (t-in '(:star time-inc))]
    (one-note-seq n (reduce + t-in) 0))
  ([(n note) (t-in '(:star time-inc)) \| (t-out '(:star time-inc))]
    (one-note-seq n (reduce + t-in) (reduce + t-out)))
  ([time-stop]
    (zero-note-seq 0))
  ([(r '(:star time-inc))]
    (zero-note-seq (reduce + r))))

(defmatch term-seq
  ([(t term)] t)
  ([(t1 term) whitespace (t2 term-seq)]
    (phrase t1 t2)))

(def staff-parser (build-parser term-seq))

(defmacro line-bars 
  ([term] `(list (execute staff-parser (str (quote ~term)))))
  ([term & terms]
    `(cons (execute staff-parser (str (quote ~term))) (line-bars ~@terms))))

(defmacro staff
  ([line] `(phrase (line-bars ~@line)))
  ([line & lines]
    `(score (phrase (line-bars ~@line)) (staff ~@lines))))
