MERGE INTO Konfiguration AS target
    USING (SELECT 'DSE_AKTIV'                                               AS schluessel,
                  'true'                                                    AS wert,
                  'Soll die digitale Stimmzettelerfassung verwendet werden' AS beschreibung,
                  'true'                                                    AS standardwert) AS source
    ON target.schluessel = source.schluessel
    WHEN NOT MATCHED THEN
        INSERT (schluessel, wert, beschreibung, standardwert)
            VALUES (source.schluessel, source.wert, source.beschreibung, source.standardwert);
