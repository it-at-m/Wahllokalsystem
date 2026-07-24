-- 1. teamID-Spalte hinzufügen
ALTER TABLE WahllokalZustand ADD IF NOT EXISTS teamID VARCHAR(36);

