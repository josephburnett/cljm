(ns cljm.sandbox
  (:use overtone.live)
  (:use overtone.synth.sampled-piano))

;; We use a saw-wave that we defined in the oscillators tutorial
(definst saw-wave [freq 440 attack 0.01 sustain 0.4 release 0.1 vol 0.4] 
  (* (env-gen (lin-env attack sustain release) 1 1 0 1 FREE)
     (saw freq)
     vol))

(definst square-wave [freq 440 attack 0.01 sustain 0.4 release 0.1 vol 0.4] 
  (* (env-gen (lin-env attack sustain release) 1 1 0 1 FREE)
     (lf-pulse freq)
     vol))

(defn mouseinst []
  (demo 60 (bpf 
             (saw (mouse-y 40 440 EXP)) 
             (mouse-x 40 5000 EXP) 
             1)))

(defn mouseinst2 [note]
  (demo 8 (bpf
            (saw (midi->hz note))
            (mouse-x 50 5000 EXP)
            (mouse-y 0 1 LIN))))

(on-event [:midi :note-on]
  (fn [e]
    (let [note (:note e) 
          vel (/ (:velocity e) 127.0)]
    ;;(println e)    
    ;;(saw-wave (midi->hz note)))); 0.01 0.4 0.1 vel)))
    ;;(sampled-piano note vel)))
    (mouseinst2 note)))
  ::keyboard-handler)





