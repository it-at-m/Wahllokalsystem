# Pfadvariablen als Methodenargumente

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Beim Update der OpenAPI Beschreibung für den ErgebnismeldungsService kam es beim Build zu einem
[Fehler](https://github.com/it-at-m/Wahllokalsystem/issues/760#issuecomment-2650329956).
Der Generator war nicht in der Lage aus dem File die notwendigen Klassen zu generieren. Der Grund dafür war, dass
im File beim Endpunkt mit dem Pfad `/businessActions/sendErgebnismeldung/{wahlID}/{wahlbezirkID}/{waehlerverzeichnisNummer}/{meldungsart}/{hauptwahlbezirkID}`
die Pfadvariablen nicht beschrieben waren. Das OpenAPI-File wurde so generiert, weil im Controller die Pfadvariablen
nicht explizit definiert wurden, sondern in einer Klasse zusammen gefasst waren:

::: code-group

```java [Controller]
@PostMapping("{wahlID}/{wahlbezirkID}/{waehlerverzeichnisNummer}/{meldungsart}/{hauptwahlbezirkID}")
public ResponseEntity<?> sendErgebnisse(
        @RequestHeader(required = false, name = "forceergebnismeldung") final String forceUpdate,
        final SendErgebnisParameter sendErgebnisParameter) {
        }
```

```java [SendErgebnisParameter]
public record SendErgebnisParameter(
        @NotNull String wahlID,
        @NotNull String wahlbezirkID,
        @NotNull Long waehlerverzeichnisNummer,
        @NotNull MeldungsartDTO meldungsart,
        @NotNull String hauptwahlbezirkID
) {
}
```

```json [openAPI.json fehlerhaft]
{
  "/businessActions/sendErgebnismeldung/{wahlID}/{wahlbezirkID}/{waehlerverzeichnisNummer}/{meldungsart}/{hauptwahlbezirkID}": {
    "post": {
      "tags": [
        "ergebnismeldung-controller"
      ],
      "description": "Übermitteln einer Ergebnismeldung an das externe System für eine konkrete Wahl eines Wahlbezirkes",
      "operationId": "sendErgebnisse",
      "parameters": [
        {
          "name": "forceergebnismeldung",
          "in": "header",
          "required": false,
          "schema": {
            "type": "string"
          }
        },
        {
          "name": "arg1",
          "in": "query",
          "required": true,
          "schema": {
            "$ref": "#/components/schemas/SendErgebnisParameter"
          }
        }
      ],
      "responses": {
        "200": {
          "description": "Die Übermittlung war erfolgreich"
        }
      }
    }
  }
}
```

```json [openAPI.json fixed]
{
  "/businessActions/sendErgebnismeldung/{wahlID}/{wahlbezirkID}/{waehlerverzeichnisNummer}/{meldungsart}/{hauptwahlbezirkID}": {
    "post": {
      "tags": [
        "ergebnismeldung-controller"
      ],
      "description": "Übermitteln einer Ergebnismeldung an das externe System für eine konkrete Wahl eines Wahlbezirkes",
      "operationId": "sendErgebnisse",
      "parameters": [
        {
          "name": "forceergebnismeldung",
          "in": "header",
          "required": false,
          "schema": {
            "type": "string"
          }
        },
        {
          "name": "wahlID",
          "in": "path",
          "required": true,
          "schema": { "type": "string" }
        },
        {
          "name": "wahlbezirkID",
          "in": "path",
          "required": true,
          "schema": { "type": "string" }
        },
        {
          "name": "waehlerverzeichnisNummer",
          "in": "path",
          "required": true,
          "schema": { "type": "integer", "format": "int64" }
        },
        {
          "name": "meldungsart",
          "in": "path",
          "required": true,
          "schema": { "type": "string", "enum": ["V1", "V3"] }
        },
        {
          "name": "hauptwahlbezirkID",
          "in": "path",
          "required": true,
          "schema": { "type": "string" }
        }
      ],
      "responses": {
        "200": {
          "description": "Die Übermittlung war erfolgreich"
        }
      }
    }
  }
}
```

:::

Im fehlerhaften `openAPI.json` kann man erkennen, dass die Pfadvariablen nicht deklariert sind. In der `fixed`-Version
die Pfadvariablen korrekt deklariert.

Um eine korrekte OpenAPI Spezifikation zu erhalten, aus der die Client-Klassen generiert werden können, könnte die
Pfadvariablen auch via Annotation definiert werden:

```java

@Operation(
            description = "Übermitteln einer Ergebnismeldung an das externe System für eine konkrete Wahl eines Wahlbezirkes",
            parameters = { @Parameter(name = "wahlbezirkID", in = ParameterIn.PATH),
                    @Parameter(name = "wahlID", in = ParameterIn.PATH),
                    @Parameter(name = "waehlerverzeichnisNummer", in = ParameterIn.PATH, schema = @Schema(implementation = Long.class)),
                    @Parameter(name = "meldungsart", in = ParameterIn.PATH, schema = @Schema(implementation = MeldungsartDTO.class)),
                    @Parameter(name = "hauptwahlbezirkID", in = ParameterIn.PATH)
            }
    )
@PostMapping("{wahlID}/{wahlbezirkID}/{waehlerverzeichnisNummer}/{meldungsart}/{hauptwahlbezirkID}")
public ResponseEntity<?> sendErgebnisse(
        @RequestHeader(required = false, name = "forceergebnismeldung") final String forceUpdate,
        final SendErgebnisParameter sendErgebnisParameter) {
}
```

## Entscheidung

Die Pfadvariablen sollen direkt als Parameter der Method definiert werden. Eine Zusammenfassung in eine Klasse darf
nicht erfolgen.

## Konsequenzen

### positiv

Auf diese Weise werden die Pfadvariablen direkt sichtbar. Eine separate Klasse steigert die Komplexität, da die
Deklaration der Pfadvariablen nicht direkt erkennbar ist.

Bei einer separaten Definition über die Annotation muss zusätzlich sichergestellt werden, dass die Annotationen mit den
Properties der Klassen übereinstimmen. Was ebenfalls die Komplexität steigern würde.

### negativ

Keine, da sich aktuelle alle Controller nach dieser Entscheidung richten. 