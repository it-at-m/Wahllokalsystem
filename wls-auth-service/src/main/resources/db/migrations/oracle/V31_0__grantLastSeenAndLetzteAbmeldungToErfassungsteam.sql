-- Grant Monitoring lastSeen and letzteAbmeldung permissions to ERFASSUNGSTEAM (idempotent)

MERGE INTO Secauthorities_Secpermissions t
USING (
    SELECT a.ID AS authority_oid, p.ID AS permission_oid
    FROM Authority a,
         Permission p
    WHERE a.authority = 'ERFASSUNGSTEAM'
      AND p.permission= 'Monitoring_BUSINESSACTION_PostLastSeen'
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
      AND p.permission= 'Monitoring_BUSINESSACTION_PostLetzteAbmeldung'
) s
ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
WHEN NOT MATCHED THEN
  INSERT (authority_oid, permission_oid)
  VALUES (s.authority_oid, s.permission_oid);

-- benoetigt im EAI-Service zum Speichern des Zustandes
MERGE INTO Secauthorities_Secpermissions t
USING (
    SELECT a.ID AS authority_oid, p.ID AS permission_oid
    FROM Authority a,
         Permission p
    WHERE a.authority = 'ERFASSUNGSTEAM'
      AND p.permission= 'aoueai_BUSINESSACTION_SaveWahllokalZustand'
) s
ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
WHEN NOT MATCHED THEN
    INSERT (authority_oid, permission_oid)
    VALUES (s.authority_oid, s.permission_oid);
