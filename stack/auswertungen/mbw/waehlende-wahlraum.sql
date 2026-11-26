SELECT wbz.NUMMER                "Wahlbezirknummer",
       SAV."Anzahl Stimmabgabevermerke",
       SAV."Anzahl Blätter",
       wahlscheine.ANZAHL        "Anzahl Wahlscheine",
       Begr.GRUND1               "Begründung Abweichung",
       sUmschlaege.ANZAHLWAEHLER "Anzahl Stimmzettel aus Urne",
       wbz.WAHLBEZIRKID
FROM WLS_BASISDATEN_SERVICE.WAHLBEZIRK wbz
         LEFT JOIN (SELECT v.FK_WAHLBEZIRKID as     wbzID,
                           sum(VStimmzettel.ANZAHL) "Anzahl Stimmabgabevermerke",
                           count(V.BLATTNUMMER)     "Anzahl Blätter"
                    FROM WLS_ERGEBNISMELDUNG_SERVICE.VERMERK V
                             INNER JOIN WLS_ERGEBNISMELDUNG_SERVICE.STIMMABGABEVERMERKESTIMMZETTEL VStimmzettel
                                        On V.ID = VStimmzettel.VERMERKID
                    GROUP BY v.FK_WAHLBEZIRKID) SAV On wbz.WAHLBEZIRKID = SAV.wbzID
         LEFT JOIN WLS_ERGEBNISMELDUNG_SERVICE.EINGENOMMENEWAHLSCHEINE wahlscheine
                   ON wbz.WAHLBEZIRKID = wahlscheine.FK_WAHLBEZIRKID
         LEFT JOIN (SELECT WAHLBEZIRKID, Grund1
                    FROM WLS_ERGEBNISMELDUNG_SERVICE.BEGRUENDUNG
                    WHERE STAPELART = 'STIMMZETTEL_UMSCHLAEGE') Begr ON wbz.WAHLBEZIRKID = Begr.WAHLBEZIRKID
         LEFT JOIN WLS_ERGEBNISMELDUNG_SERVICE.STIMMZETTELUMSCHLAEGE sUmschlaege
                   ON wbz.WAHLBEZIRKID = sUmschlaege.WAHLBEZIRKID
WHERE wbz.WAHLBEZIRKART = 'UWB'
ORDER BY wbz.NUMMER
;