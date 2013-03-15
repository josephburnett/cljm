(ns cljm.core)

(defrecord Note [at inst params])
(defrecord Time [at bpm])

(defn note? [o] (= cljm.core.Note (type o)))
(defn time? [o] (= cljm.core.Time (type o))) 