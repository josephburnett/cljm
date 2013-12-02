(ns cljm.notation.staff
  (:require [cljm.core :refer [bar phrase score]]
            [cljm.notation.core :refer [def-phrase-notation]]
            [clearley.core :refer [build-parser execute]]
            [clearley.match :refer [defmatch]]
            [clearley.lib :refer :all]
            [overtone.core]))

(defmatch staffnote
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
  ([(n staffnote) (t-in '(:star time-inc))]
    (one-note-seq n (reduce + t-in) 0))
  ([(n staffnote) (t-in '(:star time-inc)) \| (t-out '(:star time-inc))]
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

(defn parse [term-seq-string]
  (try
    (execute staff-parser term-seq-string)
    (catch Exception e 
      ; Clearley's exception messages are very unhelpful!
      (throw (Exception. (str "Unable to parse: " term-seq-string " "
                              "Reason: " (.getMessage e)))))))

(def-phrase-notation staff parse)
