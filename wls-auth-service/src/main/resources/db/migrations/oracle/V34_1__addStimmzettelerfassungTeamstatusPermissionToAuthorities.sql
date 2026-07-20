MERGE INTO Secauthorities_Secpermissions t
    USING (
        SELECT a.ID AS authority_oid, p.ID AS permission_oid
        FROM Authority a,
             Permission p
        WHERE a.authority = 'ERFASSUNGSTEAM'
          AND p.permission= 'Ergebnismeldung_BUSINESSACTION_SaveStimmzettelerfassungTeamstatus'
    ) s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (
        SELECT a.ID AS authority_oid, p.ID AS permission_oid
        FROM Authority a,
             Permission p
        WHERE a.authority = 'ERFASSUNGSTEAM'
          AND p.permission= 'Ergebnismeldung_BUSINESSACTION_SaveStimmzettelerfassungTeamstatus'
    ) s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (
        SELECT a.ID AS authority_oid, p.ID AS permission_oid
        FROM Authority a,
             Permission p
        WHERE a.authority = 'WLS_WAHLVORSTAND'
          AND p.permission= 'Ergebnismeldung_BUSINESSACTION_SaveStimmzettelerfassungTeamstatus'
    ) s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);

MERGE INTO Secauthorities_Secpermissions t
    USING (
        SELECT a.ID AS authority_oid, p.ID AS permission_oid
        FROM Authority a,
             Permission p
        WHERE a.authority = 'WLS_WAHLVORSTAND'
          AND p.permission= 'Ergebnismeldung_BUSINESSACTION_SaveStimmzettelerfassungTeamstatus'
    ) s
    ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
    WHEN NOT MATCHED THEN
        INSERT (authority_oid, permission_oid)
            VALUES (s.authority_oid, s.permission_oid);
