(ns cljm.music.doremi
  (:use cljm.notation)
  (:use overtone.inst.sampled-piano))

(def p (partial bar 4 sampled-piano))

(def doremi (phrase 
  
  (p [1 2.5 3 4.5]       (notes [:c4 :d4 :e4 :c4]))
  (p [1 2 3]             (notes [:e4 :c4 :e4]))
  (p [1 2.5 3 3.5 4 4.5] (notes [:d4 :e4 :f4 :f4 :e4 :d4]))
  (p [1]                 (notes [:f4]))
 
  (p [1 2.5 3 4.5]       (notes [:e4 :f4 :g4 :e4]))
  (p [1 2 3]             (notes [:g4 :e4 :g4]))
  (p [1 2.5 3 3.5 4 4.5] (notes [:f4 :g4 :a4 :a4 :g4 :f4]))
  (p [1]                 (notes [:a4]))

  (p [1 2.5 3 3.5 4 4.5] (notes [:g4 :c4 :d4 :e4 :f4 :g4]))
  (p [1]                 (notes [:a4]))
  (p [1 2.5 3 3.5 4 4.5] (notes [:a4 :d4 :e4 :f4 :g4 :a4]))
  (p [1]                 (notes [:b4]))

  (p [1 2.5 3 3.5 4 4.5] (notes [:b4 :e4 :f4 :g4 :a4 :b4]))
  (p [1 4 4.5]           (notes [:c5 :c5 :b4]))
  (p [1 2 3 4]           (notes [:a4 :f4 :b4 :g4]))
  (p [1 2 3 4]           (notes [:c5 :g4 :e4 :d4]))

))

