-- update for user wls_all_bwb
UPDATE Wlsuser
SET wahlbezirkID     = '9081145c-dc16-4a9b-8255-5c49ddb98c14',
    wahltagID        = '827e9e6a-ebe9-483f-a9cd-162b26f348e8',
    wahltag          = '2026-01-01',
    wahlbezirkNummer = '5678',
    wbid_wahlnummer  = '{"wbid_wahlnummer": [{"wahlbezirkID": "9081145c-dc16-4a9b-8255-5c49ddb98c14", "wahlnummer": "0", "wahlID": "28039463-3d3d-4ce5-acc1-0bef931a4409"}]}'
WHERE id in ('00000000-0000-0000-0000-000000000002');

-- update for user wls_all_uwb
UPDATE Wlsuser
SET wahlbezirkID     = '9899cae4-df9a-4ffc-a940-f20cf2280171',
    wahltagID        = '4526485e-2a3f-43d6-bd57-d0a58fc330cb',
    wahltag          = '2026-01-01',
    wahlbezirkNummer = '9012',
    wbid_wahlnummer  = '{"wbid_wahlnummer": [{"wahlbezirkID": "9899cae4-df9a-4ffc-a940-f20cf2280171", "wahlnummer": "0", "wahlID": "d8d2dd22-cbf6-488e-b9bc-b8c2b0ab31a1"}]}'
WHERE id in ('00000000-0000-0000-0000-000000000003');