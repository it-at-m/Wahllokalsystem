export const ROUTES_HOME = "home";
export const ROUTES_GETSTARTED = "getstarted";
export const ROUTES_INIT_WAHLTAG = "initWahltag";
export const ROUTES_WAHLEN = "wahlen";
export const ROUTES_KONFIGURATION = "konfiguration";

export const APPSWITCHER_URL = import.meta.env.VITE_APPSWITCHER_URL;

export const DEFAULT_TEXT_CONFIRMATION_TEXT = "Löschen";

export const enum STATUS_INDICATORS {
  SUCCESS = "success",
  INFO = "info",
  WARNING = "warning",
  ERROR = "error",
}

const WLS_SERVICE_API_URL = "/api/";

export const ADMIN_SERVICE_API_URL = `${WLS_SERVICE_API_URL}admin-service`;
export const BASISDATEN_SERVICE_API_URL = `${WLS_SERVICE_API_URL}basisdaten-service`;
export const ERGEBNISMELDUNG_SERVICE_API_URL = `${WLS_SERVICE_API_URL}ergebnismeldung-service`;
export const INFOMANAGEMENT_SERVICE_API_URL = `${WLS_SERVICE_API_URL}infomanagement-service`;
