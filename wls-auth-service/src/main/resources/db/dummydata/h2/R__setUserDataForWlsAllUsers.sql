-- give all wls_all*-Users the same wahlbezirkID, wahltagID and wbid_wahlnummer
UPDATE Wlsuser
SET wahlbezirkID    = 'wahlbezirkID',
    wahltagID       = 'wahltagID1',
    wbid_wahlnummer = '{"wbid_wahlnummer": [{"wahlbezirkID": "wahlbezirkID", "wahlnummer": "2", "wahlID": "00000000-0000-0000-0000-000000000001"},{"wahlbezirkID": "wahlbezirkID", "wahlnummer": "1", "wahlID": "wahltagID"}]}'
WHERE id in ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002',
             '00000000-0000-0000-0000-000000000003');