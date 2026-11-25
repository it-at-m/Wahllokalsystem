SELECT wbz.NUMMER                      "Wahlbezirknummer",
       wahlscheine.STIMMABGABEVERMERKE "Anzahl Wahlscheine",
       s.ANZAHLWAEHLER                 "Anzahl Stimmzettelumschläge",
       b.GRUND1                        "Begründung Abweichung",
       b.STAPELART                     "Begründung gehört zu",
       wbz.WAHLBEZIRKID
FROM WLS_BASISDATEN_SERVICE.WAHLBEZIRK wbz
         LEFT JOIN WLS_ERGEBNISMELDUNG_SERVICE.WAHLSCHEINE wahlscheine ON wbz.WAHLBEZIRKID = wahlscheine.WAHLBEZIRKID
         LEFT JOIN WLS_ERGEBNISMELDUNG_SERVICE.STIMMZETTELUMSCHLAEGE S on wbz.WAHLBEZIRKID = S.WAHLBEZIRKID
         LEFT JOIN WLS_ERGEBNISMELDUNG_SERVICE.BEGRUENDUNG B on wbz.WAHLBEZIRKID = B.WAHLBEZIRKID
WHERE wbz.WAHLBEZIRKART = 'BWB'
ORDER BY wbz.NUMMER
;