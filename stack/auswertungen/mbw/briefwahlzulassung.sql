SELECT wbz.NUMMER                                "Wahlbezirknummer",
       oeffnung.EROEFFNUNGSUHRZEIT               "Zeitpunkt des Zusammentritts",
       bwVorbUrnen.ANZAHL                        "Anzahl der Wahlurnen",
       CASE
           WHEN bwVorbUrnen.URNEVERSIEGELT = 1 THEN 'ja'
           WHEN bwVorbUrnen.URNEVERSIEGELT = 0 THEN 'nein'
           END                                   "Wahlurnen waren leer?",
       wbriefdaten.WAHLBRIEFE                    "Anzahl Wahlbriefe",
       wbriefdaten.VERZEICHNISSEUNGUELTIGE       "Anzahl der Verzeichnisse",
       wbriefdaten.NACHTRAEGE                    "Anzahl der Nachträge",
       wbriefdaten.NACHTRAEGLICHUEBERBRACHTE     "Anzahl nachgelieferter Wahlscheine",
       wbriefdaten.ZEITNACHTRAEGLICHUEBERBRACHTE "Uhrzeit der Nachlieferung",
       beanstanded."Anzahl Wahlbriefe mit Bedenken",
       S.URNENEROEFFNUNGSUHRZEIT                 "Uhrzeit Öffnung der Wahlurne",
       wbz.WAHLBEZIRKID
FROM WLS_BASISDATEN_SERVICE.WAHLBEZIRK wbz
         LEFT JOIN WLS_WAHLVORBEREITUNG_SERVICE.BWVORBEREITUNG_URNEN bwVorbUrnen
                   ON wbz.WAHLBEZIRKID = bwVorbUrnen.VORBEREITUNG_WAHLBEZIRKID
         LEFT JOIN WLS_WAHLVORBEREITUNG_SERVICE.EROEFFNUNGSUHRZEIT oeffnung ON wbz.WAHLBEZIRKID = oeffnung.WAHLBEZIRKID
         LEFT JOIN WLS_BRIEFWAHL_SERVICE.WAHLBRIEFDATEN wbriefdaten ON wbz.WAHLBEZIRKID = wbriefdaten.WAHLBEZIRKID
         LEFT JOIN WLS_ERGEBNISMELDUNG_SERVICE.STIMMZETTELUMSCHLAEGE S on wbz.WAHLBEZIRKID = S.WAHLBEZIRKID
         LEFT JOIN (SELECT WAHLBEZIRKID, count(*) "Anzahl Wahlbriefe mit Bedenken"
                    FROM WLS_BRIEFWAHL_SERVICE.BEANSTANDETEWAHLBRIEFE
                    GROUP BY WAHLBEZIRKID) beanstanded ON wbz.WAHLBEZIRKID = beanstanded.WAHLBEZIRKID
WHERE wbz.WAHLBEZIRKART = 'BWB'
ORDER BY wbz.NUMMER
;