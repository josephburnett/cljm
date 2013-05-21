# cljm

CLJM is an Overtone instrument playing framework.

## Usage

#### Basic Usage

    (use 'cljm.live)
    (in-ns 'cljm.live)
    (use 'overtone.inst.sampled-piano)
    
    (play (with-inst sampled-piano 
      (staff [ C4--- D4- E4--- C4- E4-- C4-- E4--- ])))

#### Composition

    (def melody
      (phrase
        (staff [ C4--- D4- | E4---       C4- | E4-- C4-- | E4--- - ])
        (staff [ D4--- E4- | F4- F4- E4- D4- | F4----    | ----    ])))
    (def baseline (phrase (repeat 2 (staff [ C2---- | C3---- ]))))
    
    (play (with-inst sampled-piano
      (score melody (phrase baseline (up 2 baseline)))))

#### Familiar formatting

    (def clementine
      
      ; Adapted from http://www.hummingbirdnotation.com/songs/Clementine%20(Final).pdf
      
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
    
    (play (with-inst sampled-piano clementine))

## License

Copyright (C) 2013 Joseph Burnett

Distributed under the Eclipse Public License, the same as Clojure.

