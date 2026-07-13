-- Grant Monitoring lastSeen and letzteAbmeldung permissions to ERFASSUNGSTEAM (idempotent)

-- Monitoring_BUSINESSACTION_PostLastSeen
MERGE INTO Secauthorities_Secpermissions t
USING (
  SELECT '00000000-0000-0000-0004-000000000001' AS authority_oid,
         '00000000-0000-0000-0007-000000000006' AS permission_oid
  FROM dual
) s
ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
WHEN NOT MATCHED THEN
  INSERT (authority_oid, permission_oid)
  VALUES (s.authority_oid, s.permission_oid);

-- Monitoring_BUSINESSACTION_PostLetzteAbmeldung
MERGE INTO Secauthorities_Secpermissions t
USING (
  SELECT '00000000-0000-0000-0004-000000000001' AS authority_oid,
         '00000000-0000-0000-0007-000000000007' AS permission_oid
  FROM dual
) s
ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
WHEN NOT MATCHED THEN
  INSERT (authority_oid, permission_oid)
  VALUES (s.authority_oid, s.permission_oid);

-- aoueai_BUSINESSACTION_SaveWahllokalZustand (benoetigt im EAI-Service zum Speichern des Zustandes)
MERGE INTO Secauthorities_Secpermissions t
USING (
    SELECT '00000000-0000-0000-0004-000000000001' AS authority_oid,
           '00000000-0000-0000-0004-000000000013' AS permission_oid
    FROM dual
) s
ON (t.authority_oid = s.authority_oid AND t.permission_oid = s.permission_oid)
WHEN NOT MATCHED THEN
    INSERT (authority_oid, permission_oid)
    VALUES (s.authority_oid, s.permission_oid);
