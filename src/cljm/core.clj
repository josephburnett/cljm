(ns cljm.core
  (:use [clojure.string :only [join split]]))

(defrecord Note [at inst params tparams])
(defrecord Time [at bpm])
(defrecord Event [params inc-time inc-length])

(defn note? [o] (= "cljm.core.Note" (-> o type .getName)))
(defn time? [o] (= "cljm.core.Time" (-> o type .getName))) 

(defn is-note-coll? [c]
  (and (coll? c)
       (not (nil? (:beat-length (meta c))))
       (every? note? c)))

(defn every-note-coll? [colls]
  (if (every? is-note-coll? colls) true false))

(defn panic 
  [& msg] 
  (let [m (map #(if (nil? %1) "nil" %1) msg)]
    (throw (Exception. (join " " m)))))

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

(defn- expand 
  [notes]
  (flatten (map 
             #(let [[prim-params rest-params] (split-with (complement coll?) (:params %))]
                (if (empty? rest-params)
                  (list %) ; base case -- all primitives params
                  (expand  ; recurse on an expanded collection of notes
                    (for [p (first rest-params)]
                      (->Note 
                        (:at %) 
                        (:inst %) 
                        (concat prim-params (list p) (rest rest-params))
                        (:tparams %)))))) 
             notes)))

(defn with
  [params bars]
  (with-meta
    (if (= :at (first params))
      (map #(assoc % :tparams (concat (:tparams %) (rest params))) bars)
      (map #(assoc % :params (concat (:params %) params)) bars))
    (meta bars)))

(defn with-inst
  [inst bars]
  (with-meta
    (map #(assoc % :inst inst) bars)
    (meta bars)))

(defn- apply-params
  [notes params]
  (if (keyword? (first params))
    ;; named parameter
    (map #(assoc %1 :params (cons (first params) (cons %2 (:params %1))))
         notes (cycle (rest params)))
    ;; unnamed parameter
    (map #(assoc %1 :params (cons %2 (:params %1))) 
         notes (cycle params))))

(defn- apply-tparams
  [notes tparams]
  (map #(assoc %1 :tparams (cons %2 (:tparams %1)))
        notes (cycle tparams)))

(defn bar
  [beat-length inst beats & rest-params]
  (let [notes (for [b beats] (->Note b inst [] []))]
    ;; apply parameter seqs to build complete notes
    (with-meta
      (expand ; chords into notes
        (reduce (fn [n p]
                  ;; temporal parameters start with :at
                  (if (and (keyword? (first p)) (= :at (first p)))
                    (apply-tparams n (rest p))
                    (apply-params n p)))
                notes ; one per beat
                rest-params))
      {:beat-length beat-length})))

(defn- running-index
  ([coll] (cons 0 (running-index 0 (drop-last coll))))
  ([last-val coll]
    (if (empty? coll)
      '()
      (let [next-val (+ last-val (first coll))]
        (cons next-val (running-index next-val (rest coll)))))))

(defn phrase
  [b & bars]
  (if (and ; single non-note collection parameter?
           (empty? bars)
           (not (is-note-coll? b))
           (coll? b))
    (apply phrase b) ; treat as a list of note collections
                      ; i.e. (repeat 10 (bar..))
    (do
      (assert (every-note-coll? (cons b bars)) "invalid phrase")
      (let [beats (map #(:beat-length (meta %)) (cons b bars))]
        (with-meta
          (flatten
            (map (fn [b index]
                   (let [beat-length (:beat-length (meta b))]
                     (map #(assoc % :at (+ index (:at %))) b)))
                 (cons b bars)
                 (running-index beats)))
          {:beat-length (reduce + beats)})))))

(defn score
  [b & bars]
  (if (and ; single non-note collection parameter?
           (empty? bars) 
           (not (is-note-coll? b))
           (coll? b))
    (apply score b) ; treat as a list of note collections
                    ; i.e. (repeat 10 (bar..))
    (do
      (assert (every-note-coll? (cons b bars)) "invalid score")
      (let [beats (map #(:beat-length (meta %)) (cons b bars))]
        (with-meta
          (reduce merge-bars b bars)
          {:beat-length (reduce max beats)})))))

(defn at-bpm
  [bpm bars]
  (cons (->Time 1 bpm) bars))

(defn rest-for
  [beat-length]
  (with-meta '() {:beat-length beat-length}))

(defn tparam [p & times]
  (cons :at (map #(cons %1 p) times)))
