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


;;; Note filters

(defn f-update-instrument [inst]
  (fn [note]
    (assoc note :inst inst)))

(defn- update-coll [coll param value]
  (let [[before after] (split-with #(not (= param %)) coll)]
    (if (empty? after) ; param is not present
      ;; append
      (concat coll (list param value))
      ;; replace
      (concat before (list param value) (drop 2 after)))))

(defn f-param [param value]
  (fn [note]
    (assoc note :params
           (update-coll (:params note) param value))))

(defn f-tparam [param value]
  (fn [note]
    (assoc note :tparams
           (map #(update-coll % param value) (:tparams note)))))

