UPDATE Konfiguration
SET wert = '09:00' WHERE schluessel = 'FRUEHESTE_EROEFFNUNGSZEIT_UW';
UPDATE Konfiguration
SET wert = '18:00' WHERE schluessel = 'SPAETESTE_EROEFFNUNGSZEIT_UW';
UPDATE Konfiguration
SET wert = '18:30' WHERE schluessel = 'FRUEHESTE_SCHLIESSUNGSZEIT_UW';
UPDATE Konfiguration
SET wert = '16:00' WHERE schluessel = 'FRUEHESTE_EROEFFNUNGSZEIT_BW';
UPDATE Konfiguration
SET wert = '18:00' WHERE schluessel = 'SPAETESTE_EROEFFNUNGSZEIT_BW';
UPDATE Konfiguration
SET wert = '18:30' WHERE schluessel = 'FRUEHESTE_SCHLIESSUNGSZEIT_BW';
UPDATE Konfiguration
SET wert = '18:30' WHERE schluessel = 'MELDUNGSZEIT_WAHL_SCHLIESSEN';
UPDATE Konfiguration
SET wert = '14:00' WHERE schluessel = 'MELDUNGSZEIT_ANWESENHEIT_CHECK';
