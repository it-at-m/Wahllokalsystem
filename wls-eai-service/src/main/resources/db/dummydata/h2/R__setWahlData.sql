-- update wahl for user wls_all_uwb to match wahl from user wls_all_bwb
UPDATE wahl
SET name      = 'Oberbürgermeisterwahl',
    wahlart   = 'OBW',
    wahltagID = '827e9e6a-ebe9-483f-a9cd-162b26f348e8'
WHERE id in ('d8d2dd22-cbf6-488e-b9bc-b8c2b0ab31a1');