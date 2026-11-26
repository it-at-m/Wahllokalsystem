SELECT wbz.NUMMER                                                 "Wahlbezirknummer",
       stimmzettel.GUELTIGKEIT                                    "Gueltigkeit",
       concat(stimmzettel.TEAMID, stimmzettel.STIMMZETTELKENNUNG) "Nummer des Stimmzettels",
       sum(szKandidat.VOTESBYVOTER)                               "Gültige Einzelstimmenstimmen",
       sum(szKandidat.VOTESBYWAHLVORSCHLAG)                       "Reststimmen",
       sum(szKandidat.INVALIDVOTES)                               "Ungültige Stimmen",
       max(stimmzettel.INVALIDEVOTES)                             "Ungültige nicht zugeordnete Stimmen",
       sum(szKandidat.DISCARDED)                                  "Anzahl Streichungen"
FROM WLS_BASISDATEN_SERVICE.WAHLBEZIRK wbz
         INNER JOIN WLS_ERGEBNISMELDUNG_SERVICE.STIMMZETTEL stimmzettel ON wbz.WAHLBEZIRKID = stimmzettel.WAHLBEZIRKID
         LEFT JOIN WLS_ERGEBNISMELDUNG_SERVICE.WAHLVORSCHLAG szWahlvorschlag
                   ON stimmzettel.WAHLBEZIRKID = szWahlvorschlag.STIMMZETTEL_WAHLBEZIRKID and
                      stimmzettel.WAHLID = szWahlvorschlag.STIMMZETTEL_WAHLID and
                      stimmzettel.TEAMID = szWahlvorschlag.STIMMZETTEL_TEAMID and
                      stimmzettel.STIMMZETTELKENNUNG = szWahlvorschlag.STIMMZETTEL_STIMMZETTELKENNUNG
         LEFT JOIN WLS_ERGEBNISMELDUNG_SERVICE.KANDIDAT szKandidat ON szWahlvorschlag.ID = szKandidat.WAHLVORSCHLAG_ID
GROUP BY (wbz.nummer, stimmzettel.TEAMID, stimmzettel.STIMMZETTELKENNUNG, stimmzettel.GUELTIGKEIT)
ORDER BY wbz.NUMMER, stimmzettel.TEAMID, stimmzettel.STIMMZETTELKENNUNG
;