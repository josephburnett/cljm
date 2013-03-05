(ns cljm.core)

(defrecord Note [at inst params])
(defrecord Beat [at bpm])

(defn note? [o] (= cljm.core.Note (type o)))
(defn beat? [o] (= cljm.core.Beat (type o))) 
