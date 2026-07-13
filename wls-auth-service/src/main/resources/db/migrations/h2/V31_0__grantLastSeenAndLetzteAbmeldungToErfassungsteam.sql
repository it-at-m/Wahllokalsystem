-- Grant Monitoring lastSeen and letzteAbmeldung permissions to ERFASSUNGSTEAM

-- Monitoring_BUSINESSACTION_PostLastSeen
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
KEY(authority_oid, permission_oid)
VALUES ('00000000-0000-0000-0004-000000000001', '00000000-0000-0000-0007-000000000006');
-- Monitoring_BUSINESSACTION_PostLetzteAbmeldung
MERGE INTO Secauthorities_Secpermissions (authority_oid, permission_oid)
KEY(authority_oid, permission_oid)
VALUES ('00000000-0000-0000-0004-000000000001', '00000000-0000-0000-0007-000000000007');
