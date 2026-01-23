# Navigation und Bearbeitungsreihenfolge

Die Bearbeitungsreihenfolge folgt einem gewissen Schema. Welche Schritte am Wahltag erfolgen kann der
[dieser Übersicht](/about#was-kann-die-software) entnommen werden.

Das Wahllokalsystem unterstützt dabei, in dem bestimmte Funktionen erst genutzt werden können, wenn die notwendigen
Schritte zuvor abgeschlossen wurden.

Umgesetzt wird dies durch Navigationguards, bedingt verfügbare Links und einer dynamischen Ermittlung der nächsten Seite.

Der aktuelle Bearbeitungszustand wird im `StatusStore` gepflegt.

## Navigationguards

[Navigationguards](https://router.vuejs.org/guide/advanced/navigation-guards.html) ermöglichen es, dann der Aufruf
einer View unterbunden wird.

Im Wahllokalsystem wird geprüft, ob die direkt notwendigen Schritte abgeschlossen wurden.

```mermaid
classDiagram
        direction RL

        class RouteConfig {

        }

        class WahlArtRoutes {
            WahlArtRoute1: SimpleRouteDefinition
            WahlArtRoute2: SimpleRouteDefinition
            WahlArtRouteN: SimpleRouteDefinition
        }

        class SimpleRouteDefinition {
            path: String
            component: Component
            beforeEnter: NavigationGuard | NavigationGuard[] | undefined
        }

        WahlArtRoutes "*" <--  RouteConfig : uses
```

Für jede Wahlart (MBW, BTW, ...) werden die Routen der jeweiligen möglichen Schritte definiert. Über die Property
`beforeEnter` werden die Navigationguards definiert.

## Aktive/Inaktive Links

Über die Links in der Navigation können Benutzer des Wahllokalsystems gezielt die Seite für bestimmte
Bearbeitungsschritte aufrufen. Diese Links sind inaktiv, solange nicht alle notwendigen Schritte abgeschlossen wurden.
Sind alle notwendigen Schritte abgeschlossen, wird der Link aktiv.

> [!important] Auf Reaktivität achten
> Die Navigation wird mit dem Start der Anwendung erstellt. Damit der Abschluss eines Bearbeitungsschrittes den
> nächsten Schritt verfügbar macht, muss die Navigation reaktiv sein.

### Beschreibung anhand der Implementierung für die MBW

```mermaid
classDiagram
        direction RL

        class StatusStore {
            status: Status[]
            setStepDone: (stepName, wahlbezirkID, wahlID, isDone) void
        }

        class MbwNavigationService {
            mbwStatus : ComputedRef&lt;Status&gt;
            navigation: ComputedRef&lt;NavigationDefinition[]&gt;
            MbwNavigationService(wahlID, wahlbezirkD)
        }

        StatusStore <-- MbwNavigationService : uses

        class NavigationDefinition {
            title: String
            targetRoute: RouteLocationAsRelativeGeneric
            disabled: Boolean
        }

        class Status {
            wahlID: String
            wahlbezirkID: String
            stepsDone: Record&lt;String, boolean&gt;
        }
```

Das Composable `mbwNavigationService` liefert die einzelnen Bearbeitungsschritte für die MBW, die in der Navigation
angezeigt werden. Bei der Initialisierung muss die `wahlID` und `wahlbezirkID` definiert werden. Anhand der IDs
wird der Status für diese konkrete Wahl ermittelt. Anhand des Status wird dann bestimmte, ob die Links aktiv oder inaktiv
sind. Die Reaktivität wird durch `ComputedRef` erreicht. Ändert sich am Status etwas, erfolgt eine Evaluierung
entlang der Reaktivitätskette und ggf. eine Änderung der Verfügbarkeit der Links.

## dynamische Bestimmung des nächsten Schrittes

Fast alle Seiten leiten den Nutzer weiter zum nächsten Schritt, sobald die Daten der aktuellen Seite gespeichert wurden.
Die Bestimmung der nächsten Seite erfolgt über die Funktion `getNextRoute` der `navigationUtils`. Folgende Schritte
erfolgen dabei:

- welcher allgemeine, nicht wahlspezifischen, Schritt ist als Nächstes zu bearbeiten
- welche Wahl ist noch nicht abgeschlossen
- welcher Schritt der Wahl ist als Nächstes zu bearbeiten

> [!NOTE]
> Eine Wahl gilt als abgeschlossen, wenn die Niederschrift der Wahl gedruckt wurde.

> [!NOTE]
> Konnte kein nächster Schritt ermittelt werden, wird der Benutzer auf die `Home`-Seite weitergeleitet.
