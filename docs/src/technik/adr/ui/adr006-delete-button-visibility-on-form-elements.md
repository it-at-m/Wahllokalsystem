# Sichtbarkeit des Löschen-Buttons bei Formelementen

## Status

<adr-status status='accepted'></adr-status>

## Kontext

In der Anwendung hatten wir einen Mischbetrieb, bei dem bei Eingabefeldern für Text oder Auswahlfeldern der Button zum
Löschen des Inhalts angezeigt wird. Bei einigen war der Button immer sichtbar, bei anderen Elementen nur, wenn man darüber
hovered, und einige Elemente nutzen die Möglichkeit nicht.

In einer Demo hatten wir besprochen, wie wir mit dem Mischbetrieb umgehen wollen.

## Entscheidung

Bei allen Elementen, bei denen der Benutzer einen Wert einträgt, sei es ein Text, ein Datum oder eine Auswahl, soll der
Löschbutton immer angezeigt werden, sobald ein Wert eingetragen wurde.

## Konsequenzen

Über die Vuetify-Config wird für alle Komponenten definiert, dass der Löschbutton sichtbar ist und auch sichtbar bleibt,
wenn man nicht über das Element hovered.

### Positiv

Weniger individuelle Konfiguration.

### Negativ

Den Benutzern werden mehr Steuerelemente angezeigt. In Bezug auf die Konfiguration gibt es keine negativen Folgen, da
bei Bedarf weiterhin eine individuelle Konfiguration erfolgen kann.
