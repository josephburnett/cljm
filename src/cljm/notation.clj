(ns cljm.notation)

(defn- measure-context [m]
  (second m))

(defn- notes [m context]
  (println (str "NOTES "
                " m: " m
                " context: " context))
  (merge context (measure-context m)))

(defn readcljm [{inst         :instruments
                 root-context :context
                 measures     :measures}]
  (println (str " inst: " inst 
                " root: " root-context 
                " measures: " measures))
  (map notes measures (repeat root-context)))

