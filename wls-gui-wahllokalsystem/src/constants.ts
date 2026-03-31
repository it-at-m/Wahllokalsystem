export const ROUTES_HOME = "home";
export const ROUTE_NOTFOUND = "404";
export const ROUTE_LOGOUT = "logout";
export const ROUTE_WAHLVORSTAND = "wahlvorstand";
export const ROUTE_EREIGNISSE = "ereignisse";
export const ROUTE_STIMMABGABE = "stimmabgabe";
export const ROUTE_WAHLUMGEBUNG = "wahlumgebung";
export const ROUTE_BEGINN_STIMMABGABE = "beginnStimmabgabe";
export const ROUTE_ERFASSUNG_WAHLBRIEFE = "erfassungWahlbriefe";
export const ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS = "waehlerverzeichnis";
export const ROUTE_WAHLBRIEFE_ZULASSEN = "wahlbriefzulassung";
export const ROUTE_STIMMABGABEVERMERKE = "stimmabgabevermerke";
export const ROUTE_WAHLSCHEINE = "wahlscheine";
export const ROUTE_AUSZAEHLUNG_STIMMZETTEL = "auszaehlungStimmzettel";
export const ROUTE_STAPEL_A = "stapelA";
export const ROUTE_STAPEL_B = "stapelB";
export const ROUTE_STAPEL_C = "stapelC";

export const MIN_WAHLVORSTAND_ANWESEND_VOR_SCHLIESSUNG = 3;
export const MIN_WAHLVORSTAND_ANWESEND_NACH_SCHLIESSUNG = 5;

export const WAHLHOTLINE = "089 233 96233";
export const TEAMVIEWER_URL = "KioskControlHandler:teamviewer://";

export const REQUEST_HEADER_OFFLINE_STRATEGY = "X-WLS-SW-STRATEGY";
export const HTTP_HEADER_CONTENT_TYPE = "content-type";

const WLS_SERVICE_API_URL = "/api/";

export const BROADCAST_SERVICE_API_URL =
  WLS_SERVICE_API_URL + "broadcast-service";
export const WAHLVORSTAND_SERVICE_API_URL =
  WLS_SERVICE_API_URL + "wahlvorstand-service";
export const VORFAELLEUNDVORKOMMNISSE_SERVICE_API_URL =
  WLS_SERVICE_API_URL + "vorfaelleundvorkommnisse-service";
export const WAHLVORBEREITUNG_SERVICE_API_URL =
  WLS_SERVICE_API_URL + "wahlvorbereitung-service";
export const BASISDATEN_SERVICE_API_URL =
  WLS_SERVICE_API_URL + "basisdaten-service";
export const MONITORING_SERVICE_API_URL =
  WLS_SERVICE_API_URL + "monitoring-service";
export const AUTH_SERVICE_API_URL = WLS_SERVICE_API_URL + "auth-service";
export const INFOMANAGEMENT_SERVICE_API_URL =
  WLS_SERVICE_API_URL + "infomanagement-service";
export const BRIEFWAHL_SERVICE_API_URL =
  WLS_SERVICE_API_URL + "briefwahl-service";
export const ERGEBNISMELDUNG_SERVICE_API_URL =
  WLS_SERVICE_API_URL + "ergebnismeldung-service";

export const MIN_LENGTH_FOR_BEGRUENDUNG = 3;
export const MAX_LENGTH_FOR_TEXT_INPUT = 500;

export const PRIMARY_COLOR = "#546e7a";

export const DISABLED_SUBTITLE_WAHLVORSTAND_REQUIRED =
  "Erst Anwesenheit erfassen.";
export const DISABLED_SUBTITLE_WAHLVORSTAND_MISSING =
  "Nicht genügend Mitglieder anwesend.";
export const DISABLED_SUBTITLE_WAHLHANDLUNG_MISSING =
  "Wahlhandlung muss abgeschlossen sein.";
export const DISABLED_SUBTITLE_WAHLBRIEFZULASSUNG_MISSING =
  "Wahlbriefzulassung muss abgeschlossen sein.";
export const DISABLED_SUBTITLE_WAHLSCHEINE_MISSING = "Erst Wahlbriefe zählen.";
export const DISABLED_SUBTITLE_STIMMABGABEVERMERKE_MISSING =
  "Erst Stimmabgabevermerke erfassen.";
export const SUBTITLE_WAEHLERANZAHL_IN_ARBEIT = "Wähleranzahl in Arbeit";
export const SUBTITLE_WAEHLERANZAHL_ERFASST = "Wähleranzahl erfasst";
export const SUBTITLE_AUSZAEHLUNG_IN_ARBEIT = "Auszählung in Arbeit";
export const SUBTITLE_AUSZAEHLUNG_ERFASST = "Abgeschlossen";

export const SAVE_CONTINUE = "Speichern und Weiter";
