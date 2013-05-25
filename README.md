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

With functions:

    (def base (staff [ C2-- C3- . G2. +A2-- C3- - ]))
    (def blues-change
      (phrase
        (phrase (repeat 2 base))
        (up 5 base)
        base
        (up 7 base)
        (up 5 base)
        (phrase (repeat 2 base))))
    
    (play (with-inst sampled-piano blues-change))

Or staff-like notation:

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

## Fundamentals

#### CLJM Data

1. CLJM is just a list of Notes
2. A Note is a beat, an instrument and some parameters

Some functions generate a list of Notes:

* staff 
* bar

Other functions mutate lists of Notes:

* phrase -- put two lists of Notes end-to-end
* score -- interleave two lists of Notes
* with -- append/update Note parameters
* with-inst -- provide Notes with an instrument

#### Staff Notation

Staff notation provides a compact way to represent tone and rythm.  An initial :note parameter is given to the generated notes and the :gate parameter is applied to each note ater it's duration.  It's modeled after Hummingbird Notation (www.hummingbirdnotation.com).

    ; Tone is a MIDI formatted string
    (staff [ C4 D4 ])
    
    ; Sharps and flats
    (staff [ +C4 oD4 ])
    
    ; Time is given as a sum of 1/8 (-), 1/16 (.), 1/32 (_), and 1/64 (*)
    (staff [ ---- ]) ; 1/2 beat rest
    
    ; Time attached to a note becomes it's duration.
    (staff [ C4-- D4. ] ; C4 1/4 note and D4 1/16 note
    
    ; Multiple staff lines are scored together
    (staff [ C4-- D4-- ]
           [ E4-- F4-- ])
    
    ; Stop-time (|) separates the time which drives the rhythm forward from that which does not.
    (staff [ C4--|-- D4-- ]) ; C4 is held for 1/2 beat but displaces D4 by only 1/4 beat
    
    ; Stop-time allows you to:
    ; 1. score notes on the same staff line 
    (staff [ C4|-- D4-- ]) ; C4 and D4 are played together for a 1/4 beat
    ; 2. tie notes into the next bar
    (phrase
      (staff [ C4-- D4--|- ]) 
      (staff [ - E4-  --   ]) 
    ; 3. visually separate bars (by itself, it's a noop)
    (staff [ C4-- D4-- | E4-- F4-- ])

Staff notation uses bar notation to actually generate a list of notes.

#### Bar Notation

Bar is the core function which produces a list of notes from a beat-length, instrument, note beats, and parameters.

    ; Produces a list of two notes with no instrument and no parameters
    (bar 4 nil [1 2])
    ; (#cljm.core.Note{:at 1, :inst nil, :params [], :tparams []} 
    ;  #cljm.core.Note{:at 2, :inst nil, :params [], :tparams []})
    
    ; Two notes with tone
    (bar 4 nil [1 2] [:note (note :C4) (note :D4)])
    ; (#cljm.core.Note{:at 1, :inst nil, :params (:note 60), :tparams []} 
    ;  #cljm.core.Note{:at 2, :inst nil, :params (:note 62), :tparams []})
    
    ; Two notes with tone and duration - same as (staff [ C4-- D4-- ])
    (bar 4 nil [1 2] [:note (note :C4) (note :D4)] [:at [1 :gate 0]])
    ; (#cljm.core.Note{:at 1, :inst nil, :params (:note 60), :tparams ([1 :gate 0])} 
    ;  #cljm.core.Note{:at 2, :inst nil, :params (:note 62), :tparams ([1 :gate 0])})

Bar will produces as many notes as beats and cycle the other parameters.  It will also expand non-beat lists of parameters like a list comprehension.  (I made this so I could produce chords with bar notation, but I don't it's really useful.)

## CLJM Player

#### Play

The play function will play a list of notes according to the common metronome CLJM-METRO.  

    ; If given only a list of notes, play will start with the next beat.
    (play blues-change)
    
    ; If given a beat to start on and a measure length, it will synchrnoize the notes to that beat
    (play 1 8 blues-change) ; Play on the next 1 of an 8 beat measure
    
    ; You can create an infinite list of notes with lazy-loop
    (play (lazy-loop blues-change))

#### Channels

Every note is played in a channel.  If not specified, notes will play in the :default channel.

    ; You can temporarily mute a channel
    (mute) ; Mutes the :default channel
    (mute :piano) ; Mutes the :piano channel
    
    ; You can clear all notes playing in a channel
    (clear) ; Clears the :default channel
    
    ; You can play into a channel with a MIDI instrument
    (play-in) ; Global MIDI events generate notes in the :default channel
    (play-in :piano)

#### Filters

Filters can be applied to notes playing a channel.

    ; Add an instrument to any notes that don't have one 
    (add-filter (f-inst sampled-piano))
    
    ; Append parameters
    (add-filter (f-param :vol 0.5) :piano)

## References

Other projects:

Leipzip (github.com/ctford/leipzig) -- This one is way more mature and has a lot of music theory built in.

Hummingbird Notation (www.hummingbirdnotation.com) -- This is a brillant new take on musical notation that easier to read and write.

## License

Copyright (C) 2013 Joseph Burnett

Distributed under the Eclipse Public License, the same as Clojure.

