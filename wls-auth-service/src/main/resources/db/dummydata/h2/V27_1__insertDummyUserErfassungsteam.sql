INSERT INTO Wlsuser (id, username, WAHLBEZIRKSART, userEnabled, accountNonLocked)
VALUES ('00000000-0000-0000-0000-000000000008', 'wls_mbw_erf_bwb', 'UWB', 1, 1);

-- link authorities to user wls_mbw_erf_bwb
INSERT INTO SECUSERS_SECAUTHORITIES (USER_OID, AUTHORITY_OID)
SELECT id, '00000000-0000-0000-0004-000000000001'
FROM WLSUSER
WHERE username like 'wls_mbw_erf_bwb%';

INSERT INTO Wlsuser (id, username, WAHLBEZIRKSART, userEnabled, accountNonLocked)
VALUES ('00000000-0000-0000-0000-000000000009', 'wls_mbw_erf_uwb', 'BWB', 1, 1);

-- link authorities to user wls_mbw_erf_uwb
INSERT INTO SECUSERS_SECAUTHORITIES (USER_OID, AUTHORITY_OID)
SELECT id, '00000000-0000-0000-0004-000000000001'
FROM WLSUSER
WHERE username like 'wls_mbw_erf_uwb%';