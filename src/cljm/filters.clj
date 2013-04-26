(ns cljm.filters
  (:use cljm.core)
  (:use cljm.player))

(defn- update-channel [update-fn channel]
  (swap! CLJM-CHANNELS
         #(let [c-params (channel %)]
            (if (nil? c-params) 
                ;; create new channel
                (assoc % channel (update-fn {:filters [] :clear false :mute false}))
                ;; existing channel
                (assoc % channel (update-fn c-params))))))

(defn create-channel [channel]
  (update-channel (fn [c] c) channel))

(defn- update-filters [update-fn channel]
  (update-channel #(assoc % :filters (update-fn (:filters %)))
                  channel))

(defn add-filter 
  ([f] (add-filter f :default))
  ([f channel]
    (update-filters #(cons f %)
                    channel)))

(defn remove-filter 
  ([f] (add-filter f :default))
  ([f channel]
    (update-filters (fn [filters] (filter #(not (= f %)) filters))
                    channel)))

(defn clear-filters
  ([] (clear-filters :default))
  ([channel]
    (update-filters (fn [f] []) channel)))

(defn clear
  ([] (clear :default))
  ([channel] (clear true channel))
  ([bool channel]
    (update-channel #(assoc % :clear bool) channel)))

(defn mute
  ([] (mute :default))
  ([channel] (mute true channel))
  ([bool channel]
    (update-channel #(assoc % :mute bool) channel)))

(defn list-channels []
  (let [channels @CLJM-CHANNELS]
    (dorun (map (fn [k]
                  (let [c (k channels)
                        f (count (:filters c))]
                    (println k
                             (if (true? (:clear c)) ":clear" "")
                             (if (true? (:mute c)) ":mute" "")
                             (if (> f 0) (str ":filters " f) ""))))
                (keys channels)))))

;;; Note filters

(defn f-inst [inst]
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

