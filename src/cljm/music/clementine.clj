(ns cljm.music.clementine
  (:use cljm.notation))

(def clementine

  (sheet

    (staff [ -- -- C4- C4-     | C4-- G3-- E4- E4- | E4-- C4-- C4- E4- ]
           [ -- -- --          | --   --   --      | --   --   --      ]
           [ -- -- --          | --   --   --      | --   --   --      ])
           ;       in  a         ca-  vern in  a     can- yon  ex- ca-

    (staff [ G4-- G4-- F4- E4- | D4-- -- D4- E4-   | F4-- F4-- E4- D4- ]
           [ G3----    --      | G3----  --        | G3----    --      ]
           [ E3----    --      | F3----  --        | F3----    --      ])
           ; va-  ting for a     mine    dwelt a     min- er   for-ty

    (staff [ E4-- C4-- C4- E4- | D4-- -- B3- D4-   | C4---- C4- C4-    ]
           [ G3----    --      | --   G3-- --      | G3---- --         ]
           [ E3----    --      | --   F3-- --      | E3---- --         ])
           ; nin- er   and his   daugh-ter Clem-en-  tine   oh  my

    (staff [ C4-- G3-- E4- E4- | E4-- C4-- C4- E4- | G4-- G4-- F4- E4- ]
           [ --   --   --      | --   --   --      | G3----    --      ]
           [ --   --   --      | --   --   --      | E3----    --      ])
           ; dar- ling oh  my    dar- ling oh  my    dar- ling Clem-en-

    (staff [ D4-- -- D4- E4-   | F4-- F4-- E4- D4- | E4-- C4-- C4- E4- ]
           [ G3---- --         | G3---- --         | G3---- --         ]
           [ F3---- --         | F3---- --         | E3---- --         ])
           ; tine    you are     lost and  gone for- ev-  er   dread-ful 

    (staff [ D4-- -- B3- D4-   | C4------ ]
           [ --   G3-- --      | G3------ ]
           [ --   F3-- --      | E3------ ])
           ; sor  ry Clem-en-    tine
))

(comment
(use 'overtone.live)
(use 'overtone.inst.sampled-piano) ; Warning, downloads 200M of samples!
(use 'cljm.player)
(use 'cljm.core)
(play (with-inst sampled-piano clementine))
)
