(ns cljm.filters
  (:use cljm.core)
  (:use cljm.player))

(defn update-filters [handle update-fn]
  (swap! CLJM-CHANNELS
         #(let [h-params (handle %)]
            (if (nil? h-params) @CLJM-CHANNELS ; do nothing
                (let [f-coll (update-fn (:filters h-params))
                      new-h-params (assoc h-params :filters f-coll)]
                  (assoc % handle new-h-params))))))

(defn add-filter [handle f]
  (update-filters handle 
                  #(cons f %)))

(defn remove-filter [handle f]
  (update-filters handle 
                  (fn [filters] 
                    (filter #(not (= f %)) filters))))

(defn clear-filters [handle]
  (update-filters handle (fn [f] [])))


