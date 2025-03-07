# Mock Applikation für wls-gui-wahllokal

## Grundlagen

Um zu vermeiden, dass das gesamte Backend ständig neu gestartet werden muss, wurde ein Mock-Server eingerichtet.
Dadurch genügt es für die Frontend-Entwicklung, lediglich den Mock-Server zu starten, anstatt das komplette Backend zu aktivieren.

## Entwicklung

Der Server wurde mit dem Framework expressJs erstellt.
Wenn neue Services gebraucht werden, können diese im Ordner `server_mock` ergänzt werden

### settings.js

Ein Node-Modul, das Konfigurationswerte enthält, die in Mock-Modulen verwendet werden. Beachten Sie, dass dieses Modul nicht zwischengespeichert wird. Sie können die Werte daher ändern, während der Mock-Server läuft.

### Mock-Server Anfrage-/Antwortmodule (nur REST)

Jedes Mock-Server-Modul ist ein Node.js-Modul, das eine Funktion exportiert.

`module.exports = function () {}`

Diese Funktion kann bis zu zwei Parameter haben:

`module.exports = function (server, config) {}`

Dabei ist

- `server` eine Instanz des `server.js` Moduls (siehe wilma/server_mock/modules/server.js)
- `config` ein Objekt mit den Werten, die in `settings.js` definiert sind.

Diese Funktion kann entweder ein einfaches JavaScript-Objekt zurückgeben (das als HTTP 200-Antwort an den Client gesendet wird) oder eine Antwortfunktion.

#### Antwortfunktion des Mock-Moduls

Wie oben erwähnt, kann ein Mock-Modul eine Antwortfunktion mit der folgenden Signatur zurückgeben:

`function (res, urlParam1, urlParam2)`

Dabei ist `res` das Anfrageobjekt und alle verbleibenden Parameter repräsentieren die zusätzlichen URL-Segmente nach der Basis-REST-URL (Beispiel: Wenn die REST-URL ist /foo/rest/entity/bar/baz, dann wird urlParam1 "bar" und urlParam2 "baz" sein).

Der `this`-Verweis innerhalb unserer Funktion ist eine Instanz von `MockModuleContext` (definiert in modules/mockDispatcher.js). Bis jetzt bietet das Kontextobjekt die folgenden Eigenschaften:

- `storage`: eine Instanz von modules/storage.js, die Lese- und Schreibzugriff auf einfache JSON-Dateien im Verzeichnis `data/` bietet.
- `statusCode`: der HTTP-Statuscode, der an den Browser gesendet werden soll (Standard: 200)
- `statusMessage`: die HTTP-Statusnachricht, die gesendet werden soll
- `response`: der Antwortkörper, der an den Browser gesendet werden soll
- `debug`: Eine Funktion, um Debug-Nachrichten in die Konsole auszugeben
- `notFound`: Eine Funktion, um HTTP 404-Antworten zu simulieren
- `fail`: Eine Funktion, um HTTP 500-Antworten zu simulieren

### Beispiele

#### Einfachstes Beispiel

```javascript
module.exports = function () {
  "use strict";

  return {
    foo: "this is a string",
    bar: 666,
  };
};
```

Gibt eine HTTP 200 JSON-Antwort an den Client zurück.

#### Einfaches Beispiel

```javascript
module.exports = function () {
  "use strict";

  /** @this MockModuleContext */
  return function (req, additionalUrlParam1) {
    this.debug("additionalUrlParam1: " + additionalUrlParam1);

    if (additionalUrlParam1 === "cool") {
      this.response = {
        param: "is pretty cool",
      };
    } else {
      this.notFound();
    }
  };
};
```

- Wir protokollieren den Wert von `additionalUrlParam1` in der Konsole.

- Wenn das letzte Segment der REST-URL "cool" ist, antworten wir mit einer JSON-Response, andernfalls geben wir HTTP 404 Not Found zurück.

#### Fortgeschritteneres Beispiel

```javascript
module.exports = function () {
  "use strict";

  /** @this MockModuleContext */
  return function (req, wahlID, wahlbezirkID) {
    var stored = this.storage.read("eroeffnungsuhrzeit") || [];

    if (!Array.isArray(stored)) {
      stored = [stored];
    }

    this.debug({ wahlID: wahlID, wahlbezirkID: wahlbezirkID });

    if (!stored) {
      this.notFound();
    } else {
      var result = stored.find(function (item) {
        return (
          item["wahlID"] === wahlID && item["wahlbezirkID"] === wahlbezirkID
        );
      });

      if (!!result === false) {
        this.notFound();
      } else {
        this.response = result;
      }
    }
  };
};
```
