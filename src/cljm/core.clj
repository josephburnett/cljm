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


