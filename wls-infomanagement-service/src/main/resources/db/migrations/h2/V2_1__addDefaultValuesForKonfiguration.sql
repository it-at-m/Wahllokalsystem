INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('WLK_TIME_OUT', '10800000', 'Timeout bei Inaktivität', '10800000');
INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('WILLKOMMENSTEXT', 'Herzlich Willkommen zur Wahl', 'Begrüßungstext auf der Anmeldemaske',
        'Herzlich Willkommen zur Wahl');
INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('ABSCHLUSSTEXT', 'Vielen Dank für Ihre Unterstützung als Wahlvorstand.',
        'Schlusstext nach Beenden der letzten Wahlhandlung am Wahltag',
        'Vielen Dank für Ihre Unterstützung als Wahlvorstand.');
INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('WAHLLOKALFINDER_URL', 'http://wahllokalsuche.muenchen.de',
        'URL zum Wahllokalfinder der LHM. Leer lassen, um Verknüpfung im WLS auszublenden.', '');
INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('FRUEHESTE_LOGIN_UHRZEIT', '', '', '');
INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('SPAETESTE_LOGIN_UHRZEIT', '', '', '');
INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('FRUEHESTE_EROEFFNUNGSZEIT_UW', '08:00:00', 'Wahllokal öffnen (UW): Früheste Uhrzeit', '08:00:00');
INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('SPAETESTE_EROEFFNUNGSZEIT_UW', '17:59:00', 'Wahllokal öffnen (UW): Späteste Uhrzeit', '17:59:00');
INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('FRUEHESTE_SCHLIESSUNGSZEIT_UW', '18:00:00', 'Wahllokal schließen (UW): Früheste Uhrzeit', '18:00:00');
INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('FRUEHESTE_EROEFFNUNGSZEIT_BW', '15:00:00', 'Wahllokal öffnen (BW): Früheste Uhrzeit', '15:00:00');
INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('SPAETESTE_EROEFFNUNGSZEIT_BW', '17:59:00', 'Wahllokal öffnen (BW): Späteste Uhrzeit', '17:59:00');
INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('FRUEHESTE_SCHLIESSUNGSZEIT_BW', '18:00:00', 'Wahllokal schließen (BW): Früheste Uhrzeit', '18:00:00');
INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('MELDUNGSZEIT_WAHL_SCHLIESSEN', '18:00:00', 'Automatische Meldung (UW + BW): Wahlhandlung schließen',
        '18:00:00');
INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('MELDUNGSZEIT_ANWESENHEIT_CHECK', '13:00:00', 'Automatische Meldung (UW): Anwesenheiten aktualisieren',
        '13:00:00');
INSERT INTO Konfiguration (schluessel, wert, beschreibung, standardwert)
VALUES ('KENNBUCHSTABEN',
        'K1, K2, K3 (K1 + K2), K4, K (K3 + K4); L1,L2, L3 (L1 + L2), L4, L (L3 + L4); M1, M2, M3 (M1 + M2), M4, M (M3 + M4); N1, N2, N3 (N1 + N2), N4, N (N3 + N4); O1, O2, O3 (O1 + O2), O4, O OK3 + O4)',
        '',
        'K1, K2, K3 (K1 + K2), K4, K (K3 + K4); L1,L2, L3 (L1 + L2), L4, L (L3 + L4); M1, M2, M3 (M1 + M2), M4, M (M3 + M4); N1, N2, N3 (N1 + N2), N4, N (N3 + N4); O1, O2, O3 (O1 + O2), O4, O OK3 + O4)');