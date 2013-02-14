(ns cljm.sample
  (:use cljm.notation)
  (:use overtone.inst.piano))

(def sample-1 {
  :instruments {
    :p (fn [{note :note}] (piano note)) }
  :context {
    :bpm 110
    :volume 0.8
    :bp-measure 8
    :layer :default }
  :measures '(
    '(:p {
          :on   '(1   2   [3 6] 7   8  )
          :note '(:C4 :D4 :G5   :C4 :C4) }))
})