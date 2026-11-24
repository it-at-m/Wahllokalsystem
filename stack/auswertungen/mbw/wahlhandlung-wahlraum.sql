SELECT wbz.NUMMER                   "Wahlbezirknummer",
       uu.ANZAHL                    "Anzahl Urnen",
       CASE
           WHEN uu.URNEVERSIEGELT = 1 THEN 'ja'
           WHEN uu.URNEVERSIEGELT = 0 THEN 'nein'
           END                      "Versiegelt?",
       uwbVorb.ANZAHLWAHLKABINEN    "Anzahl Wahlkabinen",
       uwbVorb.ANZAHLWAHLTISCHE     "Anzahl Tische",
       uwbVorb.ANZAHLNEBENRAEUME    "Anzahl Nebenräume",
       CASE
           WHEN w.MITTEILUNGUNGUELTIGESCHEINE = 1 THEN 'ja'
           WHEN w.MITTEILUNGUNGUELTIGESCHEINE = 0 THEN 'nein'
           END                      "WVZ war nicht zuberichtigen?",
       CASE
           WHEN w.BERICHTIGUNGVORBEGINN = 1 THEN 'ja'
           WHEN w.BERICHTIGUNGVORBEGINN = 0 THEN 'nein'
           END                      "Berichtigung vor Beginn?",
       CASE
           WHEN w.NACHTRAEGLICHEBERICHTIGUNG = 1 THEN 'ja'
           WHEN w.NACHTRAEGLICHEBERICHTIGUNG = 0 THEN 'nein'
           END                      "Die Wahlvorsteher*innen berichtigten später?",
       CASE
           WHEN w.VERZEICHNISLAGVOR = 1 THEN 'ja'
           WHEN w.VERZEICHNISLAGVOR = 0 THEN 'nein'
           END                      "Die Wahlvorsteher*innen wurde über ungültige Wahlscheine aufgeklärt",
       uOeff.EROEFFNUNGSUHRZEIT     "Beginn der Stimmabgabe",
       uSChließ.SCHLIESSUNGSUHRZEIT "Uhrzeit Wahlschluss",
       wbz.WAHLBEZIRKID             "WahlbezirkID"
FROM WLS_BASISDATEN_SERVICE.WAHLBEZIRK wbz
         LEFT JOIN WLS_WAHLVORBEREITUNG_SERVICE.URNENWAHLVORBEREITUNG uwbVorb ON wbz.WAHLBEZIRKID = uwbVorb.WAHLBEZIRKID
         LEFT JOIN WLS_WAHLVORBEREITUNG_SERVICE.UWVORBEREITUNG_URNEN UU
                   ON wbz.WAHLBEZIRKID = UU.VORBEREITUNG_WAHLBEZIRKID
         LEFT JOIN WLS_WAHLVORBEREITUNG_SERVICE.WAEHLERVERZEICHNIS W ON wbz.WAHLBEZIRKID = W.WAHLBEZIRKID
         LEFT JOIN WLS_WAHLVORBEREITUNG_SERVICE.EROEFFNUNGSUHRZEIT uOeff ON wbz.WAHLBEZIRKID = uOeff.WAHLBEZIRKID
         LEFT JOIN WLS_WAHLVORBEREITUNG_SERVICE.URNENWAHLSCHLIESSUNGSUHRZEIT uSChließ
                   ON wbz.WAHLBEZIRKID = uSChließ.WAHLBEZIRKID
WHERE wbz.WAHLBEZIRKART = 'UWB'
ORDER BY wbz.NUMMER
;