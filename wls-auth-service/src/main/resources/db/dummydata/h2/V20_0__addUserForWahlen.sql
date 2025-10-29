INSERT INTO Wlsuser (id, username, WAHLBEZIRKSART, userEnabled, accountNonLocked)
VALUES ('00000000-0000-0000-0000-000000000004', 'wls_mbw_bwb', 'BWB', 1, 1);
INSERT INTO Wlsuser (id, username, WAHLBEZIRKSART, userEnabled, accountNonLocked)
VALUES ('00000000-0000-0000-0000-000000000005', 'wls_mbw_uwb', 'UWB', 1, 1);
INSERT INTO Wlsuser (id, username, WAHLBEZIRKSART, userEnabled, accountNonLocked)
VALUES ('00000000-0000-0000-0000-000000000006', 'wls_kommw_bwb', 'BWB', 1, 1);
INSERT INTO Wlsuser (id, username, WAHLBEZIRKSART, userEnabled, accountNonLocked)
VALUES ('00000000-0000-0000-0000-000000000007', 'wls_kommw_uwb', 'UWB', 1, 1);

-- link wls_wahlvorstand for wls_kommw
INSERT INTO SECUSERS_SECAUTHORITIES (USER_OID, AUTHORITY_OID)
SELECT id, '00000000-0000-0000-0001-000000000001'
FROM WLSUSER
WHERE username like 'wls_kommw%';

-- link wls_wahlvorstand for wls_mbw
INSERT INTO SECUSERS_SECAUTHORITIES (USER_OID, AUTHORITY_OID)
SELECT id, '00000000-0000-0000-0001-000000000001'
FROM WLSUSER
WHERE username like 'wls_mbw%';