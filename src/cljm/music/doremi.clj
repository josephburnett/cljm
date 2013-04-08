(ns cljm.music.doremi
  (:use cljm.notation)
  (:use overtone.inst.sampled-piano))

(def p (partial bar 8 sampled-piano))
(def n notes)

(def doremi (phrase 

  ;    do  a   deer a   fe- male deer 
  (p [ 1   2.5 3    4.5 5   6    7   ]
  (n [ :c4 :d4 :e4  :c4 :e4 :c4  :e4 ]))
  
  ;    re  a   drop of  gol-den sun
  (p [ 1   2.5 3    3.5 4   4.5 5   ]
  (n [ :d4 :e4 :f4  :f4 :e4 :d4 :f4 ]))
 
  ;    mi  a   name i   call my- self
  (p [ 1   2.5 3    4.5 5    6   7   ]       
  (n [ :e4 :f4 :g4  :e4 :g4  :e4 :g4 ]))

  ;    fa  a   long long way to  run
  (p [ 1   2.5 3    3.5  4   4.5 5   ]
  (n [ :f4 :g4 :a4  :a4  :g4 :f4 :a4 ]))

  ;    so  a   nee- dle pull- ing thread
  (p [ 1   2.5 3    3.5 4     4.5 5   ]
  (n [ :g4 :c4 :d4  :e4 :f4   :g4 :a4 ]))

  ;    la  a   note to  fol- low so
  (p [ 1   2.5 3    3.5 4    4.5 5   ]
  (n [ :a4 :d4 :e4  :f4 :g4  :a4 :b4 ]))

  ;    ti  a   drink with jam and bread and that
  (p [ 1   2.5 3     3.5  4   4.5 5     8   8.5 ] 
  (n [ :b4 :e4 :f4   :g4  :a4 :b4 :c5   :c5 :b4 ]))

  ;    brings us  back to  do  do  do  do
  (p [ 1      2   3    4   5   6   7   8   ]
  (n [ :a4    :f4 :b4  :g4 :c5 :g4 :e4 :d4 ]))

))

