MERGE INTO Konfiguration target
    USING (SELECT 'CHATBOT_URL'                                               AS schluessel,
                  'https://bot-test.muenchen.ubitec.bot/widget/widget/'       AS wert,
                  'URL zum Script des Muckl-Chatbots'   AS beschreibung,
                  ''                                                          AS standardwert
           FROM dual) source
    ON (target.schluessel = source.schluessel)
    WHEN NOT MATCHED THEN
        INSERT (schluessel, wert, beschreibung, standardwert)
            VALUES (source.schluessel, source.wert, source.beschreibung, source.standardwert);