# Infomanagement-Service

Dieser Service dient zum Abruf von Konfigurationsinformationen, die für den Betrieb
der Software notwendig sind.

Des Weiteren können bestimmte Konfigurations-Operationen durchgeführt werden.

## Abhängigkeiten

Der Service hat keine Abhängigkeiten zu anderen Services.

## Daten und Funktionen

### Konfigurierter Wahltag

Der Service verwaltet den konfigurierten Wahltag für eine oder mehrere konkrete Wahlen.

### Konfigurationen

Eine Vielzahl an Konfigurationen wird am Wahltag über das Admin-Tool für die Wahlen festgelegt:

| Schlüssel                      | Beschreibung                                                                      | Standardwert                                                                                                                                                                                      |
|--------------------------------|-----------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| WLK_TIME_OUT                   | Timeout bei Inaktivität                                                           | 10800000                                                                                                                                                                                          |
| WILLKOMMENSTEXT                | Begrüßungstext auf der Anmeldemaske                                               | Herzlich Willkommen zur Wahl                                                                                                                                                                      |
| ABSCHLUSSTEXT                  | Schlusstext nach Beenden der letzten Wahlhandlung am Wahltag                      | Vielen Dank für Ihre Unterstützung als Wahlvorstand.                                                                                                                                              |
| WAHLLOKALFINDER_URL            | URL zum Wahllokalfinder der LHM. Leer lassen, um Verknüpfung im WLS auszublenden. |                                                                                                                                                                                                   |
| FRUEHESTE_LOGIN_UHRZEIT        |                                                                                   |                                                                                                                                                                                                   |
| SPAETESTE_LOGIN_UHRZEIT        |                                                                                   |                                                                                                                                                                                                   |
| FRUEHESTE_EROEFFNUNGSZEIT_UW   | Wahllokal öffnen (UW): Früheste Uhrzeit                                           | 08:00:00                                                                                                                                                                                          |
| SPAETESTE_EROEFFNUNGSZEIT_UW   | Wahllokal öffnen (UW): Späteste Uhrzeit                                           | 17:59:00                                                                                                                                                                                          |
| FRUEHESTE_SCHLIESSUNGSZEIT_UW  | Wahllokal schließen (UW): Früheste Uhrzeit                                        | 18:00:00                                                                                                                                                                                          |
| FRUEHESTE_EROEFFNUNGSZEIT_BW   | Wahllokal öffnen (BW): Früheste Uhrzeit                                           | 15:00:00                                                                                                                                                                                          |
| SPAETESTE_EROEFFNUNGSZEIT_BW   | Wahllokal öffnen (BW): Späteste Uhrzeit                                           | 17:59:00                                                                                                                                                                                          |
| FRUEHESTE_SCHLIESSUNGSZEIT_BW  | Wahllokal schließen (BW): Früheste Uhrzeit                                        | 18:00:00                                                                                                                                                                                          |
| MELDUNGSZEIT_WAHL_SCHLIESSEN   | Automatische Meldung (UW + BW): Wahlhandlung schließen                            | 18:00:00                                                                                                                                                                                          |
| MELDUNGSZEIT_ANWESENHEIT_CHECK | Automatische Meldung (UW): Anwesenheiten aktualisieren                            | 13:00:00                                                                                                                                                                                          |
| KENNBUCHSTABEN                 |                                                                                   | K1, K2, K3 (K1 + K2), K4, K (K3 + K4); L1,L2, L3 (L1 + L2), L4, L (L3 + L4); M1, M2, M3 (M1 + M2), M4, M (M3 + M4); N1, N2, N3 (N1 + N2), N4, N (N3 + N4); O1, O2, O3 (O1 + O2), O4, O OK3 + O4)  |

::: info Hinweise zu den Öffnungs- und Schliessungsuhrzeiten

- FRUEHESTE_EROEFFNUNGSZEIT bezeichnet den frühesten Wert, zu dem die Wahlhandlung eröffnet werden kann.
- SPAETESTE_EROEFFNUNGSZEIT bezeichnet den spätesten Wert, zu dem die Wahlhandlung eröffnet werden kann, ohne dass
  die verspätete Eröffnung als Ereignis dokumentiert werden muss.
- FRUEHESTE_SCHLIESSUNGSZEIT bezeichnet den spätesten Wert, zu dem die Wahlhandlung eröffnet werden kann und den
  frühesten Wert, zu dem die Wahlhandlung geschlossen werden kann.
  :::
