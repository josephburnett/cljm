(ns cljm.notation
  (:use cljm.core)
  (:use overtone.core))

(defn notes [n] (map note n))

(defn- merge-bars
  ([a b] (reverse (merge-bars a b '())))
  ([a b c]
   (cond
     ; done
     (and (empty? a) (empty? b)) c
     (empty? a) (concat (reverse b) c)
     (empty? b) (concat (reverse a) c)
     ; smallest goes first
     (< (:at (first a)) (:at (first b)))
       (merge-bars (rest a) b (cons (first a) c))
     :else
       (merge-bars a (rest b) (cons (first b) c)))))

(defn bar
  [beat-length bpm inst beats & rest-params]
  (let [notes (for [b beats] (->Note b inst []))]
    ; apply parameter seqs to build complete notes
    (with-meta
      (merge-bars
        (reduce (fn [n p]
                  (if (keyword? (first p))
                    (map #(assoc %1 :params (cons (first p) (cons %2 (:params %1))))
                         n (cycle (rest p)))
                    (map #(assoc %1 :params (cons %2 (:params %1))) 
                         n (cycle p))))
                notes
                rest-params)
        ; mix in the beats
        (map #(->Beat % bpm) (range 1 (inc beat-length))))
      {:beat-length beat-length})))

(defn- running-index
  ([coll] (cons 0 (running-index 0 (drop-last coll))))
  ([last-val coll]
    (if (empty? coll)
      '()
      (let [next-val (+ last-val (first coll))]
        (cons next-val (running-index next-val (rest coll)))))))

(defn phrase
  ([b & bars] (phrase (cons b bars)))
  ([bars]
    (let [beats (map #(:beat-length (meta %)) bars)]
      (with-meta
        (flatten
          (map (fn [b i]
                 (let [beat-length (:beat-length (meta b))]
                   (map #(assoc % :at (+ i (:at %))) b)))
               bars
               (running-index beats)))
        {:beat-length (reduce + beats)}))))

(defn score
  ([b & bars] (score (cons b bars)))
  ([bars]
    (with-meta
      (reduce merge-bars '() bars)
      {:beat-length (reduce max (map #(:beat-length (meta %)) bars))})))
  
