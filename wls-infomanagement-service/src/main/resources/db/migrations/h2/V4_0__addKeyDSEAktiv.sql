INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
SELECT 'DSE_AKTIV', 'true', 'Soll die digitale Stimmzettelerfassung verwendet werden', 'true'
WHERE NOT EXISTS (SELECT * FROM Konfiguration WHERE schluessel = 'DSE_AKTIV');
