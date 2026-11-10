MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Wahlvorstand_BUSINESSACTION_GetWahlvorstand') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Wahlvorstand_READ_Wahlvorstand') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

-- Für den Fallbackwahlvorstand
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Wahlvorstand_BUSINESSACTION_UpdateWahlvorstand') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Wahlvorstand_WRITE_Wahlvorstand') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

-- Für die Ermittlung Fallbackwahlvorstand
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Infomanagement_BUSINESSACTION_GetKonfigurierterWahltag') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'aoueai_BUSINESSACTION_LoadWahlvorstand') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Wahlvorbereitung_BUSINESSACTION_GetEroeffnungsuhrzeit') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Wahlvorbereitung_READ_Eroeffnungsuhrzeit') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Wahlvorbereitung_BUSINESSACTION_GetUrnenwahlSchliessungsuhrzeit') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Wahlvorbereitung_READ_UrnenwahlSchliessungsuhrzeit') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Ergebnismeldung_BUSINESSACTION_GetStimmabgabevermerke') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Ergebnismeldung_READ_Stimmabgabevermerke') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Ergebnismeldung_BUSINESSACTION_GetWahlscheine') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Ergebnismeldung_READ_Wahlscheine') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Ergebnismeldung_BUSINESSACTION_GetStimmzettelumschlaege') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Ergebnismeldung_READ_Stimmzettelumschlaege') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Ergebnismeldung_BUSINESSACTION_GetBegruendung') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Ergebnismeldung_READ_Begruendung') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Wahlvorbereitung_BUSINESSACTION_GetUrnenwahlVorbereitung') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Wahlvorbereitung_READ_UrnenwahlVorbereitung') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Wahlvorbereitung_BUSINESSACTION_GetWaehlerverzeichnis') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Wahlvorbereitung_READ_Waehlerverzeichnis') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Ergebnismeldung_BUSINESSACTION_GetAWerte') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Ergebnismeldung_READ_AWerte') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'VorfaelleUndVorkommnisse_BUSINESSACTION_GetEreignisse') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'VorfaelleUndVorkommnisse_READ_Ereignisse') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Ergebnismeldung_BUSINESSACTION_GetErgebnisse') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Ergebnismeldung_READ_Ergebnisse') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Wahlvorbereitung_BUSINESSACTION_GetBriefwahlvorbereitung') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
MERGE INTO Secauthorities_Secpermissions t
    USING (SELECT a.ID AS authority_oid, p.ID AS permission_oid
           FROM Authority a,
                Permission p
           WHERE a.authority = 'ERFASSUNGSTEAM'
             AND p.permission = 'Wahlvorbereitung_READ_Briefwahlvorbereitung') s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);