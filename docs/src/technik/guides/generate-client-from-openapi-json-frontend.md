# API-Client aus einem openapi.json File im Frontend

## Einleitung

Durch einen CLI-Befehl lässt sich aus der `openapi.json`-Datei das Datenmodell und die API des Services erstellen.

## Installation

Anders als im Backend gibt es für das Frontend kein Plugin, um den Openapi Generator zu integrieren, sondern muss
manuell über das Terminal ausgeführt werden.
Mit diesem befehl kann der openapi-generator global auf dem Rechner installiert werden:

```shell
npm install @openapitools/openapi-generator-cli -g typescript-fetch
```

Sollte dabei diese Fehlermeldung auftauchen:
![img_1.png](img_1.png)
können die Schritte
aus [diesem Stack-Beitrag](https://stackoverflow.com/questions/18088372/how-to-npm-install-global-not-as-root/59227497#59227497)
befolgt werden.

Anschließend kann im Terminal mit dem Befehl `openapi-generator-cli version` geprüft werden, ob die Installation
erfolgreich war. Gegebenenfalls muss vorher noch der Befehl

```shell
export HTTPS_PROXY="http://px-internetweb.muenchen.de:80"
``` 

im Terminal ausgeführt werden.

## Individualisieren des Generators

Damit der generierte Code zum Projekt passt, wurden die Templates des openapi-generators angepasst.

> [!IMPORTANT]
> Die Anpassung der Templates ist einmalig erfolgt und muss nicht für jeden Service wiederholt werden. Die geänderten
> Template Files wurden mit auf GitHub gepushed und liegen unter
> [wls-gui-wahllokalsystem/src/api/wls-clients/custom-openapi-template-files](https://github.com/it-at-m/Wahllokalsystem/tree/dev/wls-gui-wahllokalsystem/src/api/wls-clients/custom-openapi-template-files).
> Dieser Abschnitt dient nur zur Information und muss nicht für die Generierung des Codes ausgeführt werden.

Mit dem Befehl

```shell
openapi-generator-cli author template -g typescript-fetch -o ./my-custom-template
```

können die Template Files heruntergeladen werden. Hierbei steht `-o` für Output und es kann der Ort angegeben werden, an
dem die heruntergeladenen Files gespeichert werden sollen. Für das WLS wurden die Files `runtime.mustache` und
`apis.mustache` angepasst, indem folgende Code-Zeilen hinzugefügt wurden:

::: code-group

``` [runtime.mustache]
protected async request(context: RequestOpts, initOverrides?: RequestInit | InitOverrideFunction): Promise<Response> {
    const { url, init } = await this.createFetchParams(context, initOverrides);
    const response = await this.fetchApi(url, init);
    if (response && (response.status >= 200 && response.status < 300)) {
        return response;
    } else if (response && response.status == 400) {    // [!code ++]
      const raw = await response.text();    // [!code ++]
      const content = JSON.parse(raw);  // [!code ++]
      const wlsError = isWLSException(content)  // [!code ++]
        ? generateWlsExceptionFromJson(content) // [!code ++]
        : createDefaultWlsError({   // [!code ++]
            message: "Ungültige Anfrage",   // [!code ++]
            code: response.status.toString(),   // [!code ++]
          });   // [!code ++]
      throw new WLSError(   // [!code ++]
        response,   // [!code ++]
        wlsError.message,   // [!code ++]
        wlsError.category,  // [!code ++]
        wlsError.code,  // [!code ++]
        wlsError.service    // [!code ++]
      );    // [!code ++]
    }
    throw new ResponseError(response, 'Response returned an error code');
}

// (...)

export class RequiredError extends Error {
    override name: "RequiredError" = "RequiredError";
    constructor(public field: string, msg?: string) {
        super(msg);
    }
}

export class WLSError extends Error {   // [!code ++]
  override name: "WLSError" = "WLSError";   // [!code ++]
  category?: string;   // [!code ++]
  code?: string;   // [!code ++]
  service?: string;   // [!code ++]

  constructor(   // [!code ++]
    public response: Response,   // [!code ++]
    msg?: string,   // [!code ++]
    category?: string,   // [!code ++]
    code?: string,   // [!code ++]
    service?: string   // [!code ++]
  ) {   // [!code ++]
    super(msg);   // [!code ++]
    this.category = category;   // [!code ++]
    this.code = code;   // [!code ++]
    this.service = service;   // [!code ++]
  }   // [!code ++]
}   // [!code ++]
   // [!code ++]
export function createDefaultWlsError({   // [!code ++]
  message = "Ein unbekannter Fehler ist aufgetreten",   // [!code ++]
  category = "T",   // [!code ++]
  code = "undefined",   // [!code ++]
  service = "undefined",   // [!code ++]
}: {   // [!code ++]
  message?: string;   // [!code ++]
  category?: string;   // [!code ++]
  code?: string;   // [!code ++]
  service?: string;   // [!code ++]
}): WLSError {   // [!code ++]
  const error = new Error(message) as WLSError;   // [!code ++]
  error.name = "WLSError";   // [!code ++]
  error.category = category;   // [!code ++]
  error.code = code;   // [!code ++]
  error.service = service;   // [!code ++]
  return error;   // [!code ++]
}   // [!code ++]
   // [!code ++]
export default interface WLSException {   // [!code ++]
  category: string;   // [!code ++]
  code: string;   // [!code ++]
  message: string;   // [!code ++]
  service: string;   // [!code ++]
}   // [!code ++]
   // [!code ++]
/**   // [!code ++]
 * Type guard to check if an object is a WLSException   // [!code ++]
 * @param obj - The object to check   // [!code ++]
 * @returns True if the object has all required WLSException properties with correct types   // [!code ++]
 */   // [!code ++]
// eslint-disable-next-line @typescript-eslint/no-explicit-any   // [!code ++]
export function isWLSException(obj: any): obj is WLSException {   // [!code ++]
  return (   // [!code ++]
    obj &&   // [!code ++]
    typeof obj.category === "string" &&   // [!code ++]
    typeof obj.code === "string" &&   // [!code ++]
    typeof obj.message === "string" &&   // [!code ++]
    typeof obj.service === "string"   // [!code ++]
  );   // [!code ++]
}   // [!code ++]
   // [!code ++]
/**   // [!code ++]
 * Generates a WLSError instance from a JSON object.   // [!code ++]
 * @param content - The JSON-object with all relevant information   // [!code ++]
 * @returns the generated WLSError object from JSON data   // [!code ++]
 */   // [!code ++]
// eslint-disable-next-line @typescript-eslint/no-explicit-any   // [!code ++]
export function generateWlsExceptionFromJson(content: any): WLSException {   // [!code ++]
  return {   // [!code ++]
    category: content.category,   // [!code ++]
    code: content.code,   // [!code ++]
    message: content.message,   // [!code ++]
    service: content.service,   // [!code ++]
  };   // [!code ++]
}   // [!code ++]

export const COLLECTION_FORMATS = {
    csv: ",",
    ssv: " ",
    tsv: "\t",
    pipes: "|",
};
```

``` [apis.mustache]
{{/isArray}}
{{/formParams}}
{{/hasFormParams}}
const response = await this.request({
    path: `{{{path}}}`{{#pathParams}}.replace(`{${"{{baseName}}"}}`, encodeURIComponent(String(requestParameters['{{paramName}}']))){{/pathParams}},
    method: '{{httpMethod}}',
    headers: headerParameters,
    query: queryParameters,
    {{#hasBodyParam}}
    {{#bodyParam}}
    {{#isContainer}}
    {{^withoutRuntimeChecks}}
    body: requestParameters['{{paramName}}']{{#isArray}}{{#items}}{{^isPrimitiveType}}!.map({{datatype}}ToJSON){{/isPrimitiveType}}{{/items}}{{/isArray}},
    {{/withoutRuntimeChecks}}
    {{#withoutRuntimeChecks}}
    body: requestParameters['{{paramName}}'],
    {{/withoutRuntimeChecks}}
    {{/isContainer}}
    {{^isContainer}}
    {{^isPrimitiveType}}
    {{^withoutRuntimeChecks}}
    body: {{dataType}}ToJSON(requestParameters['{{paramName}}']),
    {{/withoutRuntimeChecks}}
    {{#withoutRuntimeChecks}}
    body: requestParameters['{{paramName}}'],
    {{/withoutRuntimeChecks}}
    {{/isPrimitiveType}}
    {{#isPrimitiveType}}
    body: requestParameters['{{paramName}}'] as any,
    {{/isPrimitiveType}}
    {{/isContainer}}
    {{/bodyParam}}
    {{/hasBodyParam}}
    {{#hasFormParams}}
    body: formParams,
    {{/hasFormParams}}
}, initOverrides);

if (response.status === 204) {   // [!code ++]
    throw new runtime.WLSError(response, "Es konnten keine Daten gefunden werden", "T", response.status.toString())   // [!code ++]
}   // [!code ++]

{{#returnType}}
{{#isResponseFile}}
return new runtime.BlobApiResponse(response);
{{/isResponseFile}}
{{^isResponseFile}}
{{#returnTypeIsPrimitive}}
{{#isMap}}
return new runtime.JSONApiResponse<any>(response);
{{/isMap}}
{{#isArray}}
return new runtime.JSONApiResponse<any>(response);
{{/isArray}}
{{#returnSimpleType}}
if (this.isJsonMime(response.headers.get('content-type'))) {
    return new runtime.JSONApiResponse<{{returnType}}>(response);
} else {
    return new runtime.TextApiResponse(response) as any;
}
{{/returnSimpleType}}
{{/returnTypeIsPrimitive}}
{{^returnTypeIsPrimitive}}
{{#isArray}}
return new runtime.JSONApiResponse(response{{^withoutRuntimeChecks}}, (jsonValue) => {{#uniqueItems}}new Set({{/uniqueItems}}jsonValue.map({{returnBaseType}}FromJSON){{/withoutRuntimeChecks}}){{#uniqueItems}}){{/uniqueItems}};
{{/isArray}}
{{^isArray}}
{{#isMap}}
return new runtime.JSONApiResponse(response{{^withoutRuntimeChecks}}, (jsonValue) => runtime.mapValues(jsonValue, {{returnBaseType}}FromJSON){{/withoutRuntimeChecks}});
{{/isMap}}
{{^isMap}}
return new runtime.JSONApiResponse(response{{^withoutRuntimeChecks}}, (jsonValue) => {{returnBaseType}}FromJSON(jsonValue){{/withoutRuntimeChecks}});
{{/isMap}}
{{/isArray}}
{{/returnTypeIsPrimitive}}
{{/isResponseFile}}
{{/returnType}}
{{^returnType}}
return new runtime.VoidApiResponse(response);
{{/returnType}}
}
```

:::

## Generierung des Codes

Im Terminal kann jetzt, wenn man sich innerhalb der `wls-gui-wahllokalsystem`-Directory befindet, für jedes
`openapi.json`-File mit folgendem Befehl der entsprechende Code generiert werden:

```shell
openapi-generator-cli generate -i src/resources/openapis/openapi.broadcast.0.2.0.json -g typescript-fetch -o src/api/wls-clients/generated-broadcast-api --template-dir src/api/wls-clients/custom-openapi-template-files
```

- Das `-i` steht für Input und gibt den Ort an, an welchem das `openapi.json`-File gespeichert ist:
  _"src/resources/openapis/openapi.broadcast.0.2.0.json"_
- Das `-o` steht für Output und gibt den Ort an, an welchem der generierte Code gespeichert werden soll:
  _"src/api/wls-clients/generated-broadcast-api"_
- Das `--template-dir` sorgt dafür, dass die angepassten Templates bei der Generierung berücksichtigt werden und gibt
  den Ort an, an dem diese gespeichert sind: _"src/api/wls-clients/custom-openapi-template-files"_

## Nutzung des generierten Codes

Es werden bei der Generierung unter anderem `**ControllerApi.ts`-Files und `**DTO.ts`-Files erstellt.
Am Beispiel vom Broadcast-Service wird gezeigt, wie der Code anschließend aufgerufen werden kann:

Damit die korrekte URL hinterlegt wird, muss beim Erstellen jeder `**ControllerApi`-Instanz der `basePath` überschrieben
werden:

```javascript 
import {BroadcastControllerApi, Configuration} from "@/api/wls-clients/generated-broadcast-api";

const broadcastCA = new BroadcastControllerApi(
    new Configuration({
        basePath: "http://localhost:8083/api/broadcast-service",
    })
);
```

Die Fetch Aufrufe erfolgen dann zum Beispiel so:

```javascript
import type {DeleteMessageRequest, GetMessageRequest} from "@/api/wls-clients/generated-broadcast-api";
import {WLSError} from "@/api/wls-clients/generated-broadcast-api";

const getParams: GetMessageRequest = {wahlbezirkID};
broadcastCA
    .getMessage(getParams)
    .then((content) => {
        const nachrichtID = content.oid;
        const deleteParams: DeleteMessageRequest = {nachrichtID};
        broadcastCA.deleteMessage(deleteParams, postConfig()).catch(() => {
            errorToShow.value = "Es ist ein Fehler beim Lesen der Nachricht aufgetreten";
        });
        messageToShow.value = content.nachricht;
    })
    .catch((error: WLSError) => {
        errorToShow.value = error.message;
    });
```

Im Fall eines `400`er Codes in der Response, was in den meisten Fällen einer WlsException entspricht, können diese Werte
dann wie folgt aufgerufen und weiterverarbeitet werden:

```javascript
broadcastCA
    .then(xyz)
    .catch((error: WLSError) => {
        errorToShow.value = error.service + " - " + error.message + " (Code: " + error.code + ")";
    });
```

Die Ausgabe wäre dann: `WLS-BROADCAST - Das Object BroadcastMessage ist nicht vollständig. (Code: 150)`