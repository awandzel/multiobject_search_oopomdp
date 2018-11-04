pip install click

note: parsing structure of language command is restricted: must be (class, <rooms>) pairs

Coarse Mapping of ObjectSearchWorld into Python:

Key: *Directory , -File, &Goal

Project Independent Files:
-ExperimentDriver
-WorldMap (√)
*BeliefRoadMap
-LanguageCommands
-SearchDriver
-SearchDomain
-Location (-)

Hooks to SimpleRL
*Model --> tasks
-State --> oompd
-objectDistribution --> pomdp

Development of SimpleRL
-OOOPOMCP ("OOPOMCP")
-POMCP

&figure out visualization