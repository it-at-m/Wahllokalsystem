INSERT INTO Wlsuser (id, username, WAHLBEZIRKSART, userEnabled, accountNonLocked)
VALUES ('00000000-0000-0000-0000-000000000004', 'wls_mbw_bwb', 'BWB', true, true);
INSERT INTO Wlsuser (id, username, WAHLBEZIRKSART, userEnabled, accountNonLocked)
VALUES ('00000000-0000-0000-0000-000000000005', 'wls_mbw_uwb', 'UWB', true, true);
INSERT INTO Wlsuser (id, username, WAHLBEZIRKSART, userEnabled, accountNonLocked)
VALUES ('00000000-0000-0000-0000-000000000006', 'wls_komw_bwb', 'BWB', true, true);
INSERT INTO Wlsuser (id, username, WAHLBEZIRKSART, userEnabled, accountNonLocked)
VALUES ('00000000-0000-0000-0000-000000000007', 'wls_komw_uwb', 'UWB', true, true);

-- link wls_wahlvorstand for wls_komw
INSERT INTO SECUSERS_SECAUTHORITIES (USER_OID, AUTHORITY_OID)
SELECT id, '00000000-0000-0000-0001-000000000001'
FROM WLSUSER
WHERE username LIKE 'wls_komw%';

-- link wls_wahlvorstand for wls_mbw
INSERT INTO SECUSERS_SECAUTHORITIES (USER_OID, AUTHORITY_OID)
SELECT id, '00000000-0000-0000-0001-000000000001'
FROM WLSUSER
WHERE username LIKE 'wls_mbw%';
