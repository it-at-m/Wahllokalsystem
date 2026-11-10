MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Wahlvorstand_BUSINESSACTION_GetWahlvorstand')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Wahlvorstand_READ_Wahlvorstand')
    );

-- Für den Fallbackwahlvorstand
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Wahlvorstand_BUSINESSACTION_UpdateWahlvorstand')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Wahlvorstand_WRITE_Wahlvorstand')
    );

-- Für die Ermittlung Fallbackwahlvorstand
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Infomanagement_BUSINESSACTION_GetKonfigurierterWahltag')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'aoueai_BUSINESSACTION_LoadWahlvorstand')
    );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Wahlvorbereitung_BUSINESSACTION_GetEroeffnungsuhrzeit')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Wahlvorbereitung_READ_Eroeffnungsuhrzeit')
    );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Wahlvorbereitung_BUSINESSACTION_GetUrnenwahlSchliessungsuhrzeit')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Wahlvorbereitung_READ_UrnenwahlSchliessungsuhrzeit')
    );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_BUSINESSACTION_GetStimmabgabevermerke')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_READ_Stimmabgabevermerke')
    );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_BUSINESSACTION_GetWahlscheine')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_READ_Wahlscheine')
    );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_BUSINESSACTION_GetStimmzettelumschlaege')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_READ_Stimmzettelumschlaege')
    );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_BUSINESSACTION_GetBegruendung')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_READ_Begruendung')
    );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Wahlvorbereitung_BUSINESSACTION_GetUrnenwahlVorbereitung')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Wahlvorbereitung_READ_UrnenwahlVorbereitung')
    );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Wahlvorbereitung_BUSINESSACTION_GetWaehlerverzeichnis')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Wahlvorbereitung_READ_Waehlerverzeichnis')
    );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_BUSINESSACTION_GetAWerte')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_READ_AWerte')
    );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'VorfaelleUndVorkommnisse_BUSINESSACTION_GetEreignisse')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'VorfaelleUndVorkommnisse_READ_Ereignisse')
    );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_BUSINESSACTION_GetErgebnisse')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Ergebnismeldung_READ_Ergebnisse')
    );

MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Wahlvorbereitung_BUSINESSACTION_GetBriefwahlvorbereitung')
    );
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
    KEY(authority_oid, permission_oid)
    VALUES (
    (SELECT ID FROM Authority WHERE authority = 'ERFASSUNGSTEAM'),
    (SELECT ID FROM Permission WHERE permission = 'Wahlvorbereitung_READ_Briefwahlvorbereitung')
    );