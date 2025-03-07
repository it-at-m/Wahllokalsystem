# API-Client Generierung

Alle Services stellen ihre API in Form einer OpenAPI Spezifikation zur Verfügung. Diese Dateien liegen den Assets
der jeweiligen Releases bei.

![Übersicht über Release von wls-eai-service Version 0.0.1-RC1](/screenshot-wls-eai-service-release-0.0.1-RC1.png)  
_Übersicht über [Release](https://github.com/it-at-m/Wahllokalsystem/releases/tag/wls-eai-service%2F0.0.1-RC1) von
wls-eai-service Version 0.0.1-RC1_

---

Mithilfe dieses `openapi.json`-Releasefiles erfolgt die Generation des Datenmodells und der Client-API im
[Backend](/technik/guides/api-client-generation/how-to-create-client-from-open-api-json.md) über ein Maven-Plugin,
während im [Frontend](/technik/guides/api-client-generation/generate-client-from-openapi-json-frontend.md) CLI-Befehle
zum Einsatz kommen.
