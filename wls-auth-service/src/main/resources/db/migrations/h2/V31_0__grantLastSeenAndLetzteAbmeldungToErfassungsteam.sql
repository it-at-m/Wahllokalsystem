-- Grant Monitoring lastSeen and letzteAbmeldung permissions to ERFASSUNGSTEAM (idempotent)

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
VALUES (
           (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
           (SELECT ID FROM Permission WHERE permission = 'Monitoring_BUSINESSACTION_PostLastSeen')
       );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
VALUES (
           (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
           (SELECT ID FROM Permission WHERE permission = 'Monitoring_BUSINESSACTION_PostLetzteAbmeldung')
       );

-- benoetigt im EAI-Service zum Speichern des Zustandes
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
VALUES (
           (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
           (SELECT ID FROM Permission WHERE permission = 'aoueai_BUSINESSACTION_SaveWahllokalZustand')
       );