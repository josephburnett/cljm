(ns cljm.core
  (:use [clojure.string :only [join split]]))

(defrecord Note [at inst params tparams])
(defrecord Time [at bpm])

(defn note? [o] (= cljm.core.Note (type o)))
(defn time? [o] (= cljm.core.Time (type o))) 

(defn panic 
  [& msg] 
  (let [m (map #(if (nil? %1) "nil" %1) msg)]
    (throw (Exception. (join " " m)))))

(def durations
  {:1 1.0
   :2 0.5
   :4 0.25
   :8 0.125
   :16 0.0625
   :32 0.03125})

(defn duration [k]
  (let [keywords (map keyword (split (name k) #"\+"))]
    (if (not-every? #(contains? durations %1) keywords)
        (panic "Invalid time:" k)
        (reduce + (map #(%1 durations) keywords)))))

(defn tparam [k beat-length & times]
  (cons :at
    (map #(if (keyword? %1)
            ;; t is a fraction of the bar's beat-length
            (list (* beat-length (duration %1)) k 0)
            (panic "Not a keyword:" %1))
          times)))
