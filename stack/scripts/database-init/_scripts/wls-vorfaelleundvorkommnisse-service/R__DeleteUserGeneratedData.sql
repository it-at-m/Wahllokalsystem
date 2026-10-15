-- ${flyway:timestamp}
/*
 Wenn wir das System in dem Zustand bringen wollen, dass die Benutzer keine Daten angelegt haben, aber
die Wahleinrichtung via Admintool nicht erneut stattfinden soll, müssen wir alle Tabellen entleeren:

 */

TRUNCATE TABLE EREIGNIS;
TRUNCATE TABLE EREIGNISSE;
