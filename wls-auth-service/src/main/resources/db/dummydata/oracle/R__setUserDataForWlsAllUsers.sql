-- update for user wls_all_bwb
UPDATE Wlsuser
SET wahlbezirkID     = '9081145c-dc16-4a9b-8255-5c49ddb98c14',
    email            = 'dummy@email.de',
    wahltagID        = '827e9e6a-ebe9-483f-a9cd-162b26f348e8',
    wahltag          = TO_TIMESTAMP('2026-01-01', 'yyyy-MM-dd'),
    wahlbezirkNummer = '5678',
    pin              = 'dummyPin',
    wbid_wahlnummer  = '{"wbid_wahlnummer": [{"wahlbezirkID": "e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b", "wahlnummer": "0", "wahlID": "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e"}, {"wahlbezirkID": "9081145c-dc16-4a9b-8255-5c49ddb98c14", "wahlnummer": "2", "wahlID": "28039463-3d3d-4ce5-acc1-0bef931a4409"}, {"wahlbezirkID": "9081145c-dc16-4a9b-8255-5c49ddb98c15", "wahlnummer": "1", "wahlID": "d8d2dd22-cbf6-488e-b9bc-b8c2b0ab31a1"}]}'
WHERE id in ('00000000-0000-0000-0000-000000000002');

-- update for user wls_all_uwb
UPDATE Wlsuser
SET wahlbezirkID     = '9899cae4-df9a-4ffc-a940-f20cf2280171',
    email            = 'dummy@email.de',
    wahltagID        = '827e9e6a-ebe9-483f-a9cd-162b26f348e8',
    wahltag          = TO_TIMESTAMP('2026-01-01', 'yyyy-MM-dd'),
    wahlbezirkNummer = '9012',
    pin              = 'dummyPin',
    wbid_wahlnummer  = '{"wbid_wahlnummer": [{"wahlbezirkID": "d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a", "wahlnummer": "0", "wahlID": "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e"}, {"wahlbezirkID": "9899cae4-df9a-4ffc-a940-f20cf2280171", "wahlnummer": "2", "wahlID": "28039463-3d3d-4ce5-acc1-0bef931a4409"}, {"wahlbezirkID": "9899cae4-df9a-4ffc-a940-f20cf2280172", "wahlnummer": "1", "wahlID": "d8d2dd22-cbf6-488e-b9bc-b8c2b0ab31a1"}]}'
WHERE id in ('00000000-0000-0000-0000-000000000003');
