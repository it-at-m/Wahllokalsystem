-- give all wls_all*-Users the same wahlbezirkID
UPDATE Wlsuser
SET wahlbezirkID = 'wahlbezirkID'
WHERE id in ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002',
             '00000000-0000-0000-0000-000000000003');