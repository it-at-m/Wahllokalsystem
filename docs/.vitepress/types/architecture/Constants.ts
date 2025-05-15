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
    target: WAHLVORBEREITUNG_SERVICE,
    titel: " ",
  },
  {
    source: ERGEBNISMELDUNG_SERVICE,
    target: BRIEFWAHL_SERVICE,
    titel: " ",
  },
  {
    source: ERGEBNISMELDUNG_SERVICE,
    target: MONITORING_SERVICE,
    titel: " ",
  },
  {
    source: ERGEBNISMELDUNG_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    titel: " ",
  },
  {
    source: ERGEBNISMELDUNG_SERVICE,
    target: EAI_SERVICE,
    titel: " ",
  },
];

const AUTH_SERVICE_RELATIONS: Relation[] = [
  {
    source: AUTH_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    titel: " ",
  },
];

const ADMIN_SERVICE_RELATIONS: Relation[] = [
  {
    source: ADMIN_SERVICE,
    target: AUTH_SERVICE,
    titel: "createAndExportWahllokalBenutzer",
  },
  {
    source: ADMIN_SERVICE,
    target: AUTH_SERVICE,
    titel: "deleteWahllokalBenutzer",
  },
  {
    source: ADMIN_SERVICE,
    target: AUTH_SERVICE,
    titel: "exportWahllokalBenutzer",
  },
  {
    source: ADMIN_SERVICE,
    target: BASISDATEN_SERVICE,
    titel: "getWahlbezirke",
  },
  {
    source: ADMIN_SERVICE,
    target: BASISDATEN_SERVICE,
    titel: "getWahltage",
  },
  {
    source: ADMIN_SERVICE,
    target: BASISDATEN_SERVICE,
    titel: "resetWahlen",
  },
  {
    source: ADMIN_SERVICE,
    target: BASISDATEN_SERVICE,
    titel: "getWahlen",
  },
  {
    source: ADMIN_SERVICE,
    target: BASISDATEN_SERVICE,
    titel: "postWahlen",
  },
  {
    source: ADMIN_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    titel: "setKonfigurierterWahltag",
  },
  {
    source: ADMIN_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    titel: "getKonfigurierteWahltage",
  },
  {
    source: ADMIN_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    titel: "deleteKonfigurierterWahltag",
  },
  {
    source: ADMIN_SERVICE,
    target: ERGEBNISMELDUNG_SERVICE,
    titel: "initialiseAWerte",
  },
];

const BASISDATEN_SERVICE_RELATIONS: Relation[] = [
  {
    source: BASISDATEN_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    titel: " ",
  },
  {
    source: BASISDATEN_SERVICE,
    target: EAI_SERVICE,
    titel: " ",
  },
];

const WAHLVORSTAND_SERVICE_RELATION: Relation[] = [
  {
    source: WAHLVORSTAND_SERVICE,
    target: INFOMANAGEMENT_SERVICE,
    titel: " ",
  },
  {
    source: WAHLVORSTAND_SERVICE,
    target: BASISDATEN_SERVICE,
    titel: " ",
  },
  {
    source: WAHLVORSTAND_SERVICE,
    target: EAI_SERVICE,
    titel: " ",
  },
];

const MONITORING_SERVICE_RELATIONS: Relation[] = [
  {
    source: MONITORING_SERVICE,
    target: EAI_SERVICE,
    titel: " ",
  },
];

export const BACKENDSERVICE_RELATIONS: Relation[] = [
  ...ERGEBNISMELDUNG_SERVICE_RELATIONS,
  ...AUTH_SERVICE_RELATIONS,
  ...ADMIN_SERVICE_RELATIONS,
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
