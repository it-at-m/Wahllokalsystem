SELECT wbz.NUMMER                                                 "Wahlbezirknummer",
       concat(stimmzettel.TEAMID, stimmzettel.STIMMZETTELKENNUNG) "Nummer des Stimmzettels",
       stimmzettel.GUELTIGKEIT                                    "Gültigkeit des Stimmzettels",
       stimmzettel.BESCHLUSS_TEXT                                 "Beschlusstext",
       wbz.WAHLBEZIRKID
FROM WLS_BASISDATEN_SERVICE.WAHLBEZIRK wbz
         INNER JOIN WLS_ERGEBNISMELDUNG_SERVICE.STIMMZETTEL stimmzettel ON wbz.WAHLBEZIRKID = stimmzettel.WAHLBEZIRKID
WHERE stimmzettel.BESCHLUSS_TEXT IS NOT NULL
ORDER BY wbz.NUMMER, stimmzettel.TEAMID, stimmzettel.STIMMZETTELKENNUNG
;