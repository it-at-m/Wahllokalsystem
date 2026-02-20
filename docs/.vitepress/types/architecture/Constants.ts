import { Relation } from "./Relation";

const ADMIN_SERVICE = "wls-admin-service";
const AUTH_SERVICE = "wls-auth-service";
const BASISDATEN_SERVICE = "wls-basisdaten-service";
const BRIEFWAHL_SERVICE = "wls-briefwahl-service";
const BROADCST_SERVICE = "wls-broadcast-service";
const EAI_SERVICE = "wls-eai-service";
const ERGEBNISMELDUNG_SERVICE = "wls-ergebnismeldung-service";
const INFOMANAGEMENT_SERVICE = "wls-infomanagement-service";
const MONITORING_SERVICE = "wls-monitoring-service";
const VORFAELLE_UND_EREIGNISSE_SERVICE = "wls-vorfaelleundvorkommnisse-service";
const WAHLVORBEREITUNG_SERVICE = "wls-wahlvorbereitung-service";
const WAHLVORSTAND_SERVICE = "wls-wahlvorstand-service";

const ERGEBNISMELDUNG_SERVICE_RELATIONS: Relation[] = [
  {
    source: ERGEBNISMELDUNG_SERVICE,
    target: BASISDATEN_SERVICE,
    operation: "getWahlen",
  },
  {
    source: ERGEBNISMELDUNG_SERVICE,
    target: WAHLVORBEREITUNG_SERVICE,
    operation: "getUrnenwahlSchliessungsUhrzeit",
  },
  {
    source: ERGEBNISMELDUNG_SERVICE,
    target: BRIEFWAHL_SERVICE,
    operation: "getBeanstandeteWahlbriefe",
  },
  {
    source: ERGEBNISMELDUNG_SERVICE,
    target: MONITORING_SERVICE,
    operation: "postSchnellmeldungSendungsuhrzeit",
  },
  {
    source: ERGEBNISMELDUNG_SERVICE,
    target: MONITORING_SERVICE,
    operation: "postSchnellmeldungDruckuhrzeit",
  },
  {
    source: ERGEBNISMELDUNG_SERVICE,
    target: MONITORING_SERVICE,
    operation: "postNiederschriftSendungsuhrzeit",
  },
  {
    source: ERGEBNISMELDUNG_SERVICE,
    target: MONITORING_SERVICE,
    operation: "postNiederschriftDruckuhrzeit",
  },
  {
    source: ERGEBNISMELDUNG_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    operation: "getKonfigurierterWahltag",
  },
  {
    source: ERGEBNISMELDUNG_SERVICE,
    target: EAI_SERVICE,
    operation: "loadWahlberechtigte",
  },
  {
    source: ERGEBNISMELDUNG_SERVICE,
    target: EAI_SERVICE,
    operation: "saveErgebnismeldung",
  },
];

const AUTH_SERVICE_RELATIONS: Relation[] = [
  {
    source: AUTH_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    operation: "getKonfigurationUnauthorized",
  },
  {
    source: AUTH_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    operation: "isWahltagActive",
  },
];

const ADMIN_SERVICE_RELATIONS: Relation[] = [
  {
    source: ADMIN_SERVICE,
    target: AUTH_SERVICE,
    operation: "createAndExportWahllokalBenutzer",
  },
  {
    source: ADMIN_SERVICE,
    target: AUTH_SERVICE,
    operation: "deleteWahllokalBenutzer",
  },
  {
    source: ADMIN_SERVICE,
    target: AUTH_SERVICE,
    operation: "exportWahllokalBenutzer",
  },
  {
    source: ADMIN_SERVICE,
    target: BASISDATEN_SERVICE,
    operation: "getWahlbezirke",
  },
  {
    source: ADMIN_SERVICE,
    target: BASISDATEN_SERVICE,
    operation: "getWahltage",
  },
  {
    source: ADMIN_SERVICE,
    target: BASISDATEN_SERVICE,
    operation: "resetWahlen",
  },
  {
    source: ADMIN_SERVICE,
    target: BASISDATEN_SERVICE,
    operation: "getWahlen",
  },
  {
    source: ADMIN_SERVICE,
    target: BASISDATEN_SERVICE,
    operation: "postWahlen",
  },
  {
    source: ADMIN_SERVICE,
    target: BASISDATEN_SERVICE,
    operation: "putWahltermindaten",
  },
  {
    source: ADMIN_SERVICE,
    target: BASISDATEN_SERVICE,
    operation: "deleteWahltermindaten",
  },
  {
    source: ADMIN_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    operation: "setKonfigurierterWahltag",
  },
  {
    source: ADMIN_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    operation: "getKonfigurierteWahltage",
  },
  {
    source: ADMIN_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    operation: "deleteKonfigurierterWahltag",
  },
  {
    source: ADMIN_SERVICE,
    target: ERGEBNISMELDUNG_SERVICE,
    operation: "initialiseAWerte",
  },
];

const BASISDATEN_SERVICE_RELATIONS: Relation[] = [
  {
    source: BASISDATEN_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    operation: "getKonfigurierterWahltag",
  },
  {
    source: BASISDATEN_SERVICE,
    target: EAI_SERVICE,
    operation: "loadBasisdaten",
  },
  {
    source: BASISDATEN_SERVICE,
    target: EAI_SERVICE,
    operation: "loadWahlen",
  },
  {
    source: BASISDATEN_SERVICE,
    target: EAI_SERVICE,
    operation: "loadWahlbezirke",
  },
  {
    source: BASISDATEN_SERVICE,
    target: EAI_SERVICE,
    operation: "loadWahltageSinceIncluding",
  },
  {
    source: BASISDATEN_SERVICE,
    target: EAI_SERVICE,
    operation: "loadWahlvorschlaege",
  },
  {
    source: BASISDATEN_SERVICE,
    target: EAI_SERVICE,
    operation: "loadReferendumvorlagen",
  },
];

const WAHLVORSTAND_SERVICE_RELATION: Relation[] = [
  {
    source: WAHLVORSTAND_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    operation: "getKonfigurierterWahltag",
  },
  {
    source: WAHLVORSTAND_SERVICE,
    target: BASISDATEN_SERVICE,
    operation: "loadWahlen",
  },
  {
    source: WAHLVORSTAND_SERVICE,
    target: EAI_SERVICE,
    operation: "loadWahlvorstand",
  },
  {
    source: WAHLVORSTAND_SERVICE,
    target: EAI_SERVICE,
    operation: "saveAnwesenheit",
  },
];

const MONITORING_SERVICE_RELATIONS: Relation[] = [
  {
    source: MONITORING_SERVICE,
    target: EAI_SERVICE,
    operation: "saveWahlbeteiligung",
  },
  {
    source: MONITORING_SERVICE,
    target: EAI_SERVICE,
    operation: "saveWahllokalZustand",
  },
];

export const BACKENDSERVICE_RELATIONS: Relation[] = [
  ...ADMIN_SERVICE_RELATIONS,
  ...ERGEBNISMELDUNG_SERVICE_RELATIONS,
  ...AUTH_SERVICE_RELATIONS,
  ...BASISDATEN_SERVICE_RELATIONS,
  ...WAHLVORSTAND_SERVICE_RELATION,
  ...MONITORING_SERVICE_RELATIONS,
];

export const BACKENDSERVICES = [
  ADMIN_SERVICE,
  AUTH_SERVICE,
  BASISDATEN_SERVICE,
  BRIEFWAHL_SERVICE,
  BROADCST_SERVICE,
  EAI_SERVICE,
  ERGEBNISMELDUNG_SERVICE,
  INFOMANAGEMENT_SERVICE,
  MONITORING_SERVICE,
  VORFAELLE_UND_EREIGNISSE_SERVICE,
  WAHLVORBEREITUNG_SERVICE,
  WAHLVORSTAND_SERVICE,
];
