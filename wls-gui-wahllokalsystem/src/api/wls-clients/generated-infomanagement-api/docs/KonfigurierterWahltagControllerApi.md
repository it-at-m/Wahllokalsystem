# KonfigurierterWahltagControllerApi

All URIs are relative to *http://localhost:8201*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**deleteKonfigurierterWahltag**](#deletekonfigurierterwahltag) | **DELETE** /businessActions/konfigurierterWahltag/{wahltagID} | |
|[**getKonfigurierteWahltage**](#getkonfiguriertewahltage) | **GET** /businessActions/konfigurierteWahltage | |
|[**getKonfigurierterWahltag**](#getkonfigurierterwahltag) | **GET** /businessActions/konfigurierterWahltag | |
|[**isWahltagActive**](#iswahltagactive) | **GET** /businessActions/loginCheck/{wahltagID} | |
|[**setKonfigurierterWahltag**](#setkonfigurierterwahltag) | **POST** /businessActions/konfigurierterWahltag | |

# **deleteKonfigurierterWahltag**
> deleteKonfigurierterWahltag()

Löscht den Konfigurierten Wahltag anhand der { wahltagID }.

### Example

```typescript
import {
    KonfigurierterWahltagControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new KonfigurierterWahltagControllerApi(configuration);

let wahltagID: string; // (default to undefined)

const { status, data } = await apiInstance.deleteKonfigurierterWahltag(
    wahltagID
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **wahltagID** | [**string**] |  | defaults to undefined|


### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Konfigurierter Wahltag erfolgreich gelöscht. |  -  |
|**400** | Validierung des Requestbody fehlgeschlagen |  -  |
|**404** | Ressource nicht gefunden |  -  |
|**500** | Unbehandelter interner Fehler, z.B. Kommunikation mit anderen Systemen oder andere Arten von unbehandelten Fehlern |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getKonfigurierteWahltage**
> Array<KonfigurierterWahltagDTO> getKonfigurierteWahltage()

Sucht alle Konfigurierten Wahltage.

### Example

```typescript
import {
    KonfigurierterWahltagControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new KonfigurierterWahltagControllerApi(configuration);

const { status, data } = await apiInstance.getKonfigurierteWahltage();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<KonfigurierterWahltagDTO>**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*, application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Liste aller Konfigurierter Wahltage erfolgreich zurückgegeben. |  -  |
|**204** | Kein Konfigurierter Wahltag vorhanden. |  -  |
|**400** | Validierung des Requestbody fehlgeschlagen |  -  |
|**404** | Ressource nicht gefunden |  -  |
|**500** | Unbehandelter interner Fehler, z.B. Kommunikation mit anderen Systemen oder andere Arten von unbehandelten Fehlern |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getKonfigurierterWahltag**
> KonfigurierterWahltagDTO getKonfigurierterWahltag()

Sucht den Konfigurierten Wahltag.

### Example

```typescript
import {
    KonfigurierterWahltagControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new KonfigurierterWahltagControllerApi(configuration);

const { status, data } = await apiInstance.getKonfigurierterWahltag();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**KonfigurierterWahltagDTO**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*, application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Konfigurierter Wahltag erfolgreich zurückgegeben. |  -  |
|**204** | Kein Konfigurierter Wahltag vorhanden. |  -  |
|**400** | Validierung des Requestbody fehlgeschlagen |  -  |
|**404** | Ressource nicht gefunden |  -  |
|**500** | Unbehandelter interner Fehler, z.B. Kommunikation mit anderen Systemen oder andere Arten von unbehandelten Fehlern |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **isWahltagActive**
> boolean isWahltagActive()

Überprüft ob ein Wahltag { wahltagID } aktiv ist.

### Example

```typescript
import {
    KonfigurierterWahltagControllerApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new KonfigurierterWahltagControllerApi(configuration);

let wahltagID: string; // (default to undefined)

const { status, data } = await apiInstance.isWahltagActive(
    wahltagID
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **wahltagID** | [**string**] |  | defaults to undefined|


### Return type

**boolean**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*, application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Erfolgreiche Rückmeldung ob Wahltag aktiv ist . |  -  |
|**204** | Wahltag konnte nicht gefunden werden. |  -  |
|**400** | Validierung des Requestbody fehlgeschlagen |  -  |
|**404** | Ressource nicht gefunden |  -  |
|**500** | Unbehandelter interner Fehler, z.B. Kommunikation mit anderen Systemen oder andere Arten von unbehandelten Fehlern |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **setKonfigurierterWahltag**
> setKonfigurierterWahltag(konfigurierterWahltagDTO)

Speichert den Konfigurierten Wahltag.

### Example

```typescript
import {
    KonfigurierterWahltagControllerApi,
    Configuration,
    KonfigurierterWahltagDTO
} from './api';

const configuration = new Configuration();
const apiInstance = new KonfigurierterWahltagControllerApi(configuration);

let konfigurierterWahltagDTO: KonfigurierterWahltagDTO; //

const { status, data } = await apiInstance.setKonfigurierterWahltag(
    konfigurierterWahltagDTO
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **konfigurierterWahltagDTO** | **KonfigurierterWahltagDTO**|  | |


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
|**200** | Konfigurierter Wahltag erfolgreich gespeichert. |  -  |
|**400** | Validierung des Requestbody fehlgeschlagen |  -  |
|**404** | Ressource nicht gefunden |  -  |
|**500** | Unbehandelter interner Fehler, z.B. Kommunikation mit anderen Systemen oder andere Arten von unbehandelten Fehlern |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

