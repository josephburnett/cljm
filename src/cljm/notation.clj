(ns cljm.notation
  (:use cljm.core)
  (:use overtone.core))

(def sheet phrase)

(defn- sum-time [t]
  (reduce #(+ %1 (case %2
                  \- 1/2
                  \. 1/4
                  \_ 1/8
                  \* 1/16))
           0.0 (filter #(contains? #{ \- \. \_ } %1) t)))

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
    (if (empty? t)
      nil ; rest
      (+ (note (keyword (apply str t)))
         (term-accidental term)))))

(defn- beat-only? [term]
  (if (empty? (filter #(= \O %1) term)) false true))

(defn- duration-only? [term]
  (if (empty? (filter #(= \X %1) term)) false true))

(defn term-bar [term]
  (let [t (count-time-in term)]
    (if (beat-only? term)
      ;; Term is only a beat
      (bar t nil [1])
      (if (duration-only? term)
        ;; Term is beat and duration, but no tone
        (bar t nil [1] [:at [(count-time-total term) :gate 0]]) 
        (let [n (term-note term)]
          (if (nil? n)
            ;; Rest
            (bar (count-time-in term) nil []) ; rest
            ;; Tone and duration
            (bar (count-time-in term) nil [1] [:note n]
                 [:at [(count-time-total term) :gate 0]])))))))
       
(defmacro line-bars 
  "Interpret terms as string parameters to term-bar."
  ([term] `(list (term-bar (str (quote ~term)))))
  ([term & terms]
    `(cons (term-bar (str (quote ~term))) (line-bars ~@terms))))

(defmacro staff
  ([line] `(phrase (line-bars ~@line)))
  ([line & lines]
    `(score (phrase (line-bars ~@line)) (staff ~@lines))))

