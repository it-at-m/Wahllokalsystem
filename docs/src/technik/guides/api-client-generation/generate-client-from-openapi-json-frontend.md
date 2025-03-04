# API-Client aus einem openapi.json File im Frontend

## Installation der openapitools

Anders als im Backend gibt es für das Frontend kein Plugin, um den Openapi Generator zu integrieren, sondern muss
als CLI Befehl ausgeführt werden. Mit diesem Befehl kann der openapi-generator
[typescript-axios](https://openapi-generator.tech/docs/generators/typescript-axios/) global auf dem Rechner
installiert werden:

```shell
npm install @openapitools/openapi-generator-cli -g typescript-axios
```

::: details Errorhandling
Möglicherweise erscheint bei dem Versuch die openapitools zu installieren, diese Fehlermeldung:
![error_install_openapi-generator.png](/error_install_openapi-generator.png)

In diesem Fall können die folgenden Schritte ausgeführt werden ([Quelle](https://stackoverflow.com/questions/18088372/how-to-npm-install-global-not-as-root/59227497#59227497))

> [!TIP]
> Es ist empfehlenswert, für die folgenden Befehle das `Git Bash`-Terminal zu nutzen.

1. **npm Konfigurieren**

   ```shell
   npm config set prefix '~/.local/'
   ```

   Durch diesen Befehl wird die Zeile `prefix=~/.local/` zur `.npmrc`-Datei hinzugefügt. Alternativ kann dies auch
   manuell erfolgen.

2. **Umgebungsvariablen prüfen**

   `~/.local/` sollte in der `PATH`-Umgebungsvariable enthalten sein. Dies kann entweder manuell, oder über diesen
   Befehl erfolgen. `./bashrc` sollte dabei durch das entsprechende Config-File des genutzten Terminals ersetzt werden.

   ```shell
   mkdir -p ~/.local
   echo 'export PATH="$HOME/.local/:$PATH"' >> ~/.bashrc
   ```

3. **Proxy konfigurieren**

   Bei Bedarf muss zusätzlich noch mit folgendem Befehl der https-Proxy konfiguriert werden:

   ```shell
   export HTTPS_PROXY=<proxy url>
   ```

Nach Durchführung dieser Schritte kann der Installationsbefehl erneut ausgeführt werden.
:::

Anschließend kann im Terminal mit dem Befehl `openapi-generator-cli version` geprüft werden, ob die Installation
erfolgreich war.

## Generierung des Codes

Es gibt zwei Möglichkeiten, den Befehl auszuführen, um den gewünschten Code generieren zu lassen.

#### 1) Ausführen des Befehls im Terminal

Im Terminal kann, wenn man sich innerhalb der `wls-gui-wahllokalsystem`-Directory befindet, für jedes
`openapi.json`-File mit folgendem Befehl der entsprechende Code generiert werden:

```shell
openapi-generator-cli generate -i src/resources/openapis/<openapi-file> -g typescript-axios -o src/api/wls-clients/generated-<domain>-api
```

Dabei gilt:

- Das `-i` steht für Input und gibt den Ort an, an welchem das `openapi.json`-File gespeichert ist:
  _"src/resources/openapis/\<openapi-file\>"_. `<openapi-file>` wird dabei durch das entsprechende
  Release-File ersetzt. Beispiel: `openapi.broadcast.0.2.0.json`
- Das `-o` steht für Output und gibt den Ort an, an welchem der generierte Code gespeichert werden soll:
  _"src/api/wls-clients/generated-\<domain\>-api"_. `<domain>` wird dabei durch den entsprechenden
  WLS-Service ersetzt. Beispiel: `generated-broadcast-api`

::: details Beispiel Broadcast-API
Der komplette, zusammengesetzte Befehl für die Generierung der Broadcast API über das Terminal würde so aussehen:

```shell
openapi-generator-cli generate -i src/resources/openapis/openapi.broadcast.0.2.0.json -g typescript-axios -o src/api/wls-clients/generated-broadcast-api
```

:::

#### 2) Ausführen des Skripts `gen:<domain>-api`

In der `package.json` kann der oben genannte Befehl als Skript hinzugefügt werden. Das sieht dann so aus:

```json
 "scripts": {
    "dev": "vite",
    /* ... */
    "gen:<domain>-api": "openapi-generator-cli generate -i src/resources/openapis/<openapi-file> -g typescript-axios -o src/api/wls-clients/generated-<domain>-api " // [!code ++]
  },
```

::: details Beispiel Broadcast-API
Der komplette Befehl für die Generierung der Broadcast API über das `package.json`-File würde so aussehen:

```json
 "scripts": {
    "gen:<domain>-api": "openapi-generator-cli generate -i src/resources/openapis/openapi.broadcast.0.2.0.json -g typescript-axios -o src/api/wls-clients/generated-broadcast-api "
  },
```

:::

::: details Errorhandling
Möglicherweise ist die Generierung nicht erfolgreich, und diese Fehlermeldung tritt auf:

```shell
Error: Error: Unable to access jarfile { path-to-project }\WLS 3.0\node_modules\@openapitools\openapi-generator-cli\versions\7.10.0.jar

    at { path-to-project }\WLS 3.0\node_modules\@openapitools\openapi-generator-cli\main.js:2:47463
    at ChildProcess.exithandler (node:child_process:427:5)
    at ChildProcess.emit (node:events:518:28)
    at maybeClose (node:internal/child_process:1104:16)
    at ChildProcess._handle.onexit (node:internal/child_process:304:5)

Node.js v22.11.0

Process finished with exit code 1
```

In diesem Fall muss bitte nach Möglichkeit 1 - [Ausführen des Befehls im Terminal](#_1-ausfuhren-des-befehls-im-terminal)
verfahren werden.

🚧 -> Die behebung dieses Problems wird in [diesem Issue](https://github.com/it-at-m/Wahllokalsystem/issues/809) behandelt.
:::

## Nutzung des generierten Codes

Es werden bei der Generierung unter anderem folgende Dateien erstellt:

- `base.ts`: enthält die BaseApi Klasse als Grundlage für alle anderen Api Klassen
- `configuration.ts`: enthält die Konfigurationsoptionen für den Client --> _bietet die Möglichkeit den `BASE_PATH` aus
  dem `base.ts`-File zu überschreiben_
- `common.ts`: enthält allgemeine Hilfsfunktionen und Typdefinitionen
- `api.ts`: enthält die spezifischen Datenmodelle, sowie die Api-Klasse mit den entsprechenden Methoden des Clients

Am Beispiel vom Broadcast-Service wird gezeigt, wie der Code anschließend aufgerufen werden kann:

Damit die korrekte URL hinterlegt wird, muss beim Erstellen jeder `*ControllerApi`-Instanz der `basePath` überschrieben
werden:

::: code-group

```typescript [useBroadcastService.ts]
import {
  BroadcastControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-broadcast-api";
import { BROADCAST_SERVICE_API_URL } from "@/constants";

export function useBroadcastService() {
  const broadcastCA = new BroadcastControllerApi(
    new Configuration({ basePath: BROADCAST_SERVICE_API_URL })
  );
}
```

```typescript [constants.ts]
const WLS_SERVICE_API_URL = "/api/";

export const BROADCAST_SERVICE_API_URL =
  WLS_SERVICE_API_URL + "broadcast-service";
```

:::

Die Api-Aufrufe erfolgen dann zum Beispiel so:

```typescript {10,18}
// useBroadcastService.ts
import {
  BroadcastControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-broadcast-api";
import { BROADCAST_SERVICE_API_URL } from "@/constants";

export function useBroadcastService() {
  const broadcastCA = new BroadcastControllerApi(/* ... */);

  async function getMessage(wahlbezirkID: string) {
    // [!code focus:20]
    try {
      const response = await broadcastCA.getMessage(wahlbezirkID);
      if (response.status == 204) {
        return { message: "", error: "Es konnten keine Daten gefunden werden" };
      }

      const messageDTO = response.data;
      const nachrichtID = messageDTO.oid;
      try {
        await broadcastCA.deleteMessage(nachrichtID);
      } catch {
        return {
          message: "",
          error: "Es ist ein Fehler beim Lesen der Nachricht aufgetreten",
        };
      }

      return { message: messageDTO.nachricht, error: "" };
    } catch (e) {
      return { message: "", error: (e as Error).message };
    }
  }

  return { getMessage };
}
```

Im Fall eines `400`er Codes in der Response, was in den meisten Fällen einer WlsException entspricht, können diese Werte
dann wie folgt aufgerufen und weiterverarbeitet werden:

```typescript {21-24}
// useBroadcastService.ts
import type { BroadcastMessageDTO } from "@/api/wls-clients/generated-broadcast-api";

import axios from "axios";

import {
  BroadcastControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-broadcast-api";
import { WLSError } from "@/api/WLSError";
import { BROADCAST_SERVICE_API_URL } from "@/constants";

export function useBroadcastService() {
  const broadcastCA = new BroadcastControllerApi(/* ... */);

  async function getMessage(wahlbezirkID: string) {
    /* ... */
  }

  async function postMessage(nachricht: string, wahlbezirkIDs: string[]) {
    // [!code focus:20]
    const broadcastMessageDTO = {
      wahlbezirkIDs,
      nachricht,
    } as BroadcastMessageDTO;

    try {
      await broadcastCA.broadcast(broadcastMessageDTO);
      return { error: "" };
    } catch (e) {
      if (axios.isAxiosError(e)) {
        if (e.response) {
          const error: WLSError = e.response.data;
          const errorMessage =
            error.service +
            " - " +
            error.message +
            " (Code: " +
            error.code +
            ")";
          return { error: errorMessage };
        } else {
          return { error: "Fehler beim Senden der Broadcast Nachricht" };
        }
      } else {
        return { error: "Fehler beim Senden der Broadcast Nachricht" };
      }
    }
  }

  return {
    getMessage,
    postMessage,
  };
}
```

Die Ausgabe des Errors könnte zum Beispiel folgende sein: `WLS-BROADCAST - Das Object BroadcastMessage ist nicht
vollständig. (Code: 150)`
