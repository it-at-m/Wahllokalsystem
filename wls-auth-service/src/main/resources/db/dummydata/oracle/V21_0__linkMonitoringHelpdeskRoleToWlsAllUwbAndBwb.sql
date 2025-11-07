-- link MONITORING_HELPDESK
INSERT INTO SECUSERS_SECAUTHORITIES (USER_OID, AUTHORITY_OID)
SELECT id, '00000000-0000-0000-0003-000000000001'
FROM WLSUSER
WHERE username like 'wls_all%';

-- reset wahltagID und wahltag, um ein Löschen der Nutzer bei der Initialisierung zu verhindern
UPDATE Wlsuser
SET wahltagID        = null,
    wahltag          = null
WHERE username like 'wls_all%';