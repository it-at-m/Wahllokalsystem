MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'WLS_WAHLVORSTAND'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_BUSINESSACTION_SaveStimmzettelerfassungTeamstatus')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'WLS_WAHLVORSTAND'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_BUSINESSACTION_GetStimmzettelerfassungTeamstatus')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'WLS_WAHLVORSTAND'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_BUSINESSACTION_RegisterStimmzettelerfassungStart')
    );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_BUSINESSACTION_SaveStimmzettelerfassungTeamstatus')
    );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_BUSINESSACTION_GetStimmzettelerfassungTeamstatus')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_BUSINESSACTION_RegisterStimmzettelerfassungStart')
    );
