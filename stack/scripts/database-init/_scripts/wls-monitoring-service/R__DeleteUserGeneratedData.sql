-- ${flyway:timestamp}
/*
Wenn wir das System in dem Zustand bringen wollen, dass die Benutzer keine Daten angelegt haben, aber
die Wahleinrichtung via Admintool nicht erneut stattfinden soll, müssen wir alles behalten außer:
 */

TRUNCATE TABLE WAEHLERANZAHL;
