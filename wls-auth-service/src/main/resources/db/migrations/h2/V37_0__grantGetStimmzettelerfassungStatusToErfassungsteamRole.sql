MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_BUSINESSACTION_GetStimmzettelerfassungStatus')
    );