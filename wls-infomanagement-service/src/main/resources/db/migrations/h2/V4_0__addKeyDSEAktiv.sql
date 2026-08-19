INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
SELECT 'DSE_AKTIV', 'true', 'Soll die digitalen Stimmzettelerfassung verwendet werden', 'true'
WHERE NOT EXISTS (SELECT * FROM Konfiguration WHERE schluessel = 'DSE_AKTIV');
