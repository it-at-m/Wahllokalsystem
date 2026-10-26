# KonfigurationControllerApi

All URIs are relative to *http://localhost:8201*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getKennbuchstabenListen**](#getkennbuchstabenlisten) | **GET** /businessActions/kennbuchstaben | |
|[**getKonfiguration**](#getkonfiguration) | **GET** /businessActions/konfiguration/{key} | |
|[**getKonfigurationUnauthorized**](#getkonfigurationunauthorized) | **GET** /businessActions/konfigurationUnauthorized/{key} | |
|[**getKonfigurations**](#getkonfigurations) | **GET** /businessActions/konfiguration | |
|[**postKonfiguration**](#postkonfiguration) | **POST** /businessActions/konfiguration/{key} | |

# **getKennbuchstabenListen**
> KennbuchstabenListenDTO getKennbuchstabenListen()

Gibt die Objekt mit einer Liste aller Kennbuchstaben zurück.

### Example

```typescript
import {
    KonfigurationControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new KonfigurationControllerApi(configuration);

const { status, data } = await apiInstance.getKennbuchstabenListen();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**KennbuchstabenListenDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*, application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Objekt mit Liste aller Kennbuchstaben erfolgreich geladen. |  -  |
|**400** | Validierung des Requestbody fehlgeschlagen |  -  |
|**404** | Ressource nicht gefunden |  -  |
|**500** | Unbehandelter interner Fehler, z.B. Kommunikation mit anderen Systemen oder andere Arten von unbehandelten Fehlern |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getKonfiguration**
> KonfigurationDTO getKonfiguration()

Gibt die Konfiguration { key } zurück.

### Example

```typescript
import {
    KonfigurationControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new KonfigurationControllerApi(configuration);

let key: 'WLK_TIME_OUT' | 'WILLKOMMENSTEXT' | 'ABSCHLUSSTEXT' | 'WAHLLOKALFINDER_URL' | 'WAEHLERVERZEICHNIS_URL' | 'CHATBOT_URL' | 'FRUEHESTE_LOGIN_UHRZEIT' | 'SPAETESTE_LOGIN_UHRZEIT' | 'FRUEHESTE_EROEFFNUNGSZEIT_UW' | 'SPAETESTE_EROEFFNUNGSZEIT_UW' | 'FRUEHESTE_SCHLIESSUNGSZEIT_UW' | 'FRUEHESTE_EROEFFNUNGSZEIT_BW' | 'SPAETESTE_EROEFFNUNGSZEIT_BW' | 'FRUEHESTE_SCHLIESSUNGSZEIT_BW' | 'MELDUNGSZEIT_WAHL_SCHLIESSEN' | 'MELDUNGSZEIT_ANWESENHEIT_CHECK' | 'KENNBUCHSTABEN' | 'FRUEHESTE_EROEFFNUNGSZEIT' | 'SPAETESTE_EROEFFNUNGSZEIT' | 'FRUEHESTE_SCHLIESSUNGSZEIT' | 'DSE_AKTIV'; // (default to undefined)

const { status, data } = await apiInstance.getKonfiguration(
    key
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **key** | [**&#39;WLK_TIME_OUT&#39; | &#39;WILLKOMMENSTEXT&#39; | &#39;ABSCHLUSSTEXT&#39; | &#39;WAHLLOKALFINDER_URL&#39; | &#39;WAEHLERVERZEICHNIS_URL&#39; | &#39;CHATBOT_URL&#39; | &#39;FRUEHESTE_LOGIN_UHRZEIT&#39; | &#39;SPAETESTE_LOGIN_UHRZEIT&#39; | &#39;FRUEHESTE_EROEFFNUNGSZEIT_UW&#39; | &#39;SPAETESTE_EROEFFNUNGSZEIT_UW&#39; | &#39;FRUEHESTE_SCHLIESSUNGSZEIT_UW&#39; | &#39;FRUEHESTE_EROEFFNUNGSZEIT_BW&#39; | &#39;SPAETESTE_EROEFFNUNGSZEIT_BW&#39; | &#39;FRUEHESTE_SCHLIESSUNGSZEIT_BW&#39; | &#39;MELDUNGSZEIT_WAHL_SCHLIESSEN&#39; | &#39;MELDUNGSZEIT_ANWESENHEIT_CHECK&#39; | &#39;KENNBUCHSTABEN&#39; | &#39;FRUEHESTE_EROEFFNUNGSZEIT&#39; | &#39;SPAETESTE_EROEFFNUNGSZEIT&#39; | &#39;FRUEHESTE_SCHLIESSUNGSZEIT&#39; | &#39;DSE_AKTIV&#39;**]**Array<&#39;WLK_TIME_OUT&#39; &#124; &#39;WILLKOMMENSTEXT&#39; &#124; &#39;ABSCHLUSSTEXT&#39; &#124; &#39;WAHLLOKALFINDER_URL&#39; &#124; &#39;WAEHLERVERZEICHNIS_URL&#39; &#124; &#39;CHATBOT_URL&#39; &#124; &#39;FRUEHESTE_LOGIN_UHRZEIT&#39; &#124; &#39;SPAETESTE_LOGIN_UHRZEIT&#39; &#124; &#39;FRUEHESTE_EROEFFNUNGSZEIT_UW&#39; &#124; &#39;SPAETESTE_EROEFFNUNGSZEIT_UW&#39; &#124; &#39;FRUEHESTE_SCHLIESSUNGSZEIT_UW&#39; &#124; &#39;FRUEHESTE_EROEFFNUNGSZEIT_BW&#39; &#124; &#39;SPAETESTE_EROEFFNUNGSZEIT_BW&#39; &#124; &#39;FRUEHESTE_SCHLIESSUNGSZEIT_BW&#39; &#124; &#39;MELDUNGSZEIT_WAHL_SCHLIESSEN&#39; &#124; &#39;MELDUNGSZEIT_ANWESENHEIT_CHECK&#39; &#124; &#39;KENNBUCHSTABEN&#39; &#124; &#39;FRUEHESTE_EROEFFNUNGSZEIT&#39; &#124; &#39;SPAETESTE_EROEFFNUNGSZEIT&#39; &#124; &#39;FRUEHESTE_SCHLIESSUNGSZEIT&#39; &#124; &#39;DSE_AKTIV&#39;>** |  | defaults to undefined|


### Return type

**KonfigurationDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*, application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Konfiguration erfolgreich zurückgegeben. |  -  |
|**204** | Keine Konfiguration vorhanden. |  -  |
|**400** | Validierung des Requestbody fehlgeschlagen |  -  |
|**404** | Ressource nicht gefunden |  -  |
|**500** | Unbehandelter interner Fehler, z.B. Kommunikation mit anderen Systemen oder andere Arten von unbehandelten Fehlern |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getKonfigurationUnauthorized**
> KonfigurationDTO getKonfigurationUnauthorized()

Gibt eine Konfiguration anhand der Loginzeit { key } zurück.

### Example

```typescript
import {
    KonfigurationControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new KonfigurationControllerApi(configuration);

let key: 'WLK_TIME_OUT' | 'WILLKOMMENSTEXT' | 'ABSCHLUSSTEXT' | 'WAHLLOKALFINDER_URL' | 'WAEHLERVERZEICHNIS_URL' | 'CHATBOT_URL' | 'FRUEHESTE_LOGIN_UHRZEIT' | 'SPAETESTE_LOGIN_UHRZEIT' | 'FRUEHESTE_EROEFFNUNGSZEIT_UW' | 'SPAETESTE_EROEFFNUNGSZEIT_UW' | 'FRUEHESTE_SCHLIESSUNGSZEIT_UW' | 'FRUEHESTE_EROEFFNUNGSZEIT_BW' | 'SPAETESTE_EROEFFNUNGSZEIT_BW' | 'FRUEHESTE_SCHLIESSUNGSZEIT_BW' | 'MELDUNGSZEIT_WAHL_SCHLIESSEN' | 'MELDUNGSZEIT_ANWESENHEIT_CHECK' | 'KENNBUCHSTABEN' | 'FRUEHESTE_EROEFFNUNGSZEIT' | 'SPAETESTE_EROEFFNUNGSZEIT' | 'FRUEHESTE_SCHLIESSUNGSZEIT' | 'DSE_AKTIV'; // (default to undefined)

const { status, data } = await apiInstance.getKonfigurationUnauthorized(
    key
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **key** | [**&#39;WLK_TIME_OUT&#39; | &#39;WILLKOMMENSTEXT&#39; | &#39;ABSCHLUSSTEXT&#39; | &#39;WAHLLOKALFINDER_URL&#39; | &#39;WAEHLERVERZEICHNIS_URL&#39; | &#39;CHATBOT_URL&#39; | &#39;FRUEHESTE_LOGIN_UHRZEIT&#39; | &#39;SPAETESTE_LOGIN_UHRZEIT&#39; | &#39;FRUEHESTE_EROEFFNUNGSZEIT_UW&#39; | &#39;SPAETESTE_EROEFFNUNGSZEIT_UW&#39; | &#39;FRUEHESTE_SCHLIESSUNGSZEIT_UW&#39; | &#39;FRUEHESTE_EROEFFNUNGSZEIT_BW&#39; | &#39;SPAETESTE_EROEFFNUNGSZEIT_BW&#39; | &#39;FRUEHESTE_SCHLIESSUNGSZEIT_BW&#39; | &#39;MELDUNGSZEIT_WAHL_SCHLIESSEN&#39; | &#39;MELDUNGSZEIT_ANWESENHEIT_CHECK&#39; | &#39;KENNBUCHSTABEN&#39; | &#39;FRUEHESTE_EROEFFNUNGSZEIT&#39; | &#39;SPAETESTE_EROEFFNUNGSZEIT&#39; | &#39;FRUEHESTE_SCHLIESSUNGSZEIT&#39; | &#39;DSE_AKTIV&#39;**]**Array<&#39;WLK_TIME_OUT&#39; &#124; &#39;WILLKOMMENSTEXT&#39; &#124; &#39;ABSCHLUSSTEXT&#39; &#124; &#39;WAHLLOKALFINDER_URL&#39; &#124; &#39;WAEHLERVERZEICHNIS_URL&#39; &#124; &#39;CHATBOT_URL&#39; &#124; &#39;FRUEHESTE_LOGIN_UHRZEIT&#39; &#124; &#39;SPAETESTE_LOGIN_UHRZEIT&#39; &#124; &#39;FRUEHESTE_EROEFFNUNGSZEIT_UW&#39; &#124; &#39;SPAETESTE_EROEFFNUNGSZEIT_UW&#39; &#124; &#39;FRUEHESTE_SCHLIESSUNGSZEIT_UW&#39; &#124; &#39;FRUEHESTE_EROEFFNUNGSZEIT_BW&#39; &#124; &#39;SPAETESTE_EROEFFNUNGSZEIT_BW&#39; &#124; &#39;FRUEHESTE_SCHLIESSUNGSZEIT_BW&#39; &#124; &#39;MELDUNGSZEIT_WAHL_SCHLIESSEN&#39; &#124; &#39;MELDUNGSZEIT_ANWESENHEIT_CHECK&#39; &#124; &#39;KENNBUCHSTABEN&#39; &#124; &#39;FRUEHESTE_EROEFFNUNGSZEIT&#39; &#124; &#39;SPAETESTE_EROEFFNUNGSZEIT&#39; &#124; &#39;FRUEHESTE_SCHLIESSUNGSZEIT&#39; &#124; &#39;DSE_AKTIV&#39;>** |  | defaults to undefined|


### Return type

**KonfigurationDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*, application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Keine Konfiguration für den { key } vorhanden. |  -  |
|**400** | Validierung des Requestbody fehlgeschlagen |  -  |
|**404** | Ressource nicht gefunden |  -  |
|**500** | Unbehandelter interner Fehler, z.B. Kommunikation mit anderen Systemen oder andere Arten von unbehandelten Fehlern |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getKonfigurations**
> Array<KonfigurationDTO> getKonfigurations()

Gibt alle Konfigurationen zurück.

### Example

```typescript
import {
    KonfigurationControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new KonfigurationControllerApi(configuration);

const { status, data } = await apiInstance.getKonfigurations();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<KonfigurationDTO>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*, application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Konfigurationen erfolgreich geladen. |  -  |
|**204** | Keine Konfigurationen vorhanden. |  -  |
|**400** | Validierung des Requestbody fehlgeschlagen |  -  |
|**404** | Ressource nicht gefunden |  -  |
|**500** | Unbehandelter interner Fehler, z.B. Kommunikation mit anderen Systemen oder andere Arten von unbehandelten Fehlern |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **postKonfiguration**
> postKonfiguration(konfigurationSetDTO)

Speichert die Konfiguration { key }.

### Example

```typescript
import {
    KonfigurationControllerApi,
    Configuration,
    KonfigurationSetDTO
} from './api';

const configuration = new Configuration();
const apiInstance = new KonfigurationControllerApi(configuration);

let key: 'WLK_TIME_OUT' | 'WILLKOMMENSTEXT' | 'ABSCHLUSSTEXT' | 'WAHLLOKALFINDER_URL' | 'WAEHLERVERZEICHNIS_URL' | 'CHATBOT_URL' | 'FRUEHESTE_LOGIN_UHRZEIT' | 'SPAETESTE_LOGIN_UHRZEIT' | 'FRUEHESTE_EROEFFNUNGSZEIT_UW' | 'SPAETESTE_EROEFFNUNGSZEIT_UW' | 'FRUEHESTE_SCHLIESSUNGSZEIT_UW' | 'FRUEHESTE_EROEFFNUNGSZEIT_BW' | 'SPAETESTE_EROEFFNUNGSZEIT_BW' | 'FRUEHESTE_SCHLIESSUNGSZEIT_BW' | 'MELDUNGSZEIT_WAHL_SCHLIESSEN' | 'MELDUNGSZEIT_ANWESENHEIT_CHECK' | 'KENNBUCHSTABEN' | 'FRUEHESTE_EROEFFNUNGSZEIT' | 'SPAETESTE_EROEFFNUNGSZEIT' | 'FRUEHESTE_SCHLIESSUNGSZEIT' | 'DSE_AKTIV'; // (default to undefined)
let konfigurationSetDTO: KonfigurationSetDTO; //

const { status, data } = await apiInstance.postKonfiguration(
    key,
    konfigurationSetDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **konfigurationSetDTO** | **KonfigurationSetDTO**|  | |
| **key** | [**&#39;WLK_TIME_OUT&#39; | &#39;WILLKOMMENSTEXT&#39; | &#39;ABSCHLUSSTEXT&#39; | &#39;WAHLLOKALFINDER_URL&#39; | &#39;WAEHLERVERZEICHNIS_URL&#39; | &#39;CHATBOT_URL&#39; | &#39;FRUEHESTE_LOGIN_UHRZEIT&#39; | &#39;SPAETESTE_LOGIN_UHRZEIT&#39; | &#39;FRUEHESTE_EROEFFNUNGSZEIT_UW&#39; | &#39;SPAETESTE_EROEFFNUNGSZEIT_UW&#39; | &#39;FRUEHESTE_SCHLIESSUNGSZEIT_UW&#39; | &#39;FRUEHESTE_EROEFFNUNGSZEIT_BW&#39; | &#39;SPAETESTE_EROEFFNUNGSZEIT_BW&#39; | &#39;FRUEHESTE_SCHLIESSUNGSZEIT_BW&#39; | &#39;MELDUNGSZEIT_WAHL_SCHLIESSEN&#39; | &#39;MELDUNGSZEIT_ANWESENHEIT_CHECK&#39; | &#39;KENNBUCHSTABEN&#39; | &#39;FRUEHESTE_EROEFFNUNGSZEIT&#39; | &#39;SPAETESTE_EROEFFNUNGSZEIT&#39; | &#39;FRUEHESTE_SCHLIESSUNGSZEIT&#39; | &#39;DSE_AKTIV&#39;**]**Array<&#39;WLK_TIME_OUT&#39; &#124; &#39;WILLKOMMENSTEXT&#39; &#124; &#39;ABSCHLUSSTEXT&#39; &#124; &#39;WAHLLOKALFINDER_URL&#39; &#124; &#39;WAEHLERVERZEICHNIS_URL&#39; &#124; &#39;CHATBOT_URL&#39; &#124; &#39;FRUEHESTE_LOGIN_UHRZEIT&#39; &#124; &#39;SPAETESTE_LOGIN_UHRZEIT&#39; &#124; &#39;FRUEHESTE_EROEFFNUNGSZEIT_UW&#39; &#124; &#39;SPAETESTE_EROEFFNUNGSZEIT_UW&#39; &#124; &#39;FRUEHESTE_SCHLIESSUNGSZEIT_UW&#39; &#124; &#39;FRUEHESTE_EROEFFNUNGSZEIT_BW&#39; &#124; &#39;SPAETESTE_EROEFFNUNGSZEIT_BW&#39; &#124; &#39;FRUEHESTE_SCHLIESSUNGSZEIT_BW&#39; &#124; &#39;MELDUNGSZEIT_WAHL_SCHLIESSEN&#39; &#124; &#39;MELDUNGSZEIT_ANWESENHEIT_CHECK&#39; &#124; &#39;KENNBUCHSTABEN&#39; &#124; &#39;FRUEHESTE_EROEFFNUNGSZEIT&#39; &#124; &#39;SPAETESTE_EROEFFNUNGSZEIT&#39; &#124; &#39;FRUEHESTE_SCHLIESSUNGSZEIT&#39; &#124; &#39;DSE_AKTIV&#39;>** |  | defaults to undefined|


### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Konfiguration erfolgreich gespeichert. |  -  |
|**400** | Validierung des Requestbody fehlgeschlagen |  -  |
|**404** | Ressource nicht gefunden |  -  |
|**500** | Unbehandelter interner Fehler, z.B. Kommunikation mit anderen Systemen oder andere Arten von unbehandelten Fehlern |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

