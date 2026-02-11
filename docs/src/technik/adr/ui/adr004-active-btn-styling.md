# Styling des `active` Buttons

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Ein Button mit dem `active`-Prop wird für die Nutzer\*innen hervorgehoben. Wenn dieser disabled ist, oder darüber
gehovert wird, wird von vuetify ein overlay darüber gelegt, welches den Butten heller und somit schwieriger lesbar
macht.

## Entscheidung

- Alle Buttons mit `active`-Prop haben die `primary`-Farbe der Anwendung.
- Alle Buttons mit `active`-Prop, die disabled sind, haben keine Farbe mehr (--> grau)
- Alle Buttons mit `active`-Prop, über die entweder gehovert wird, oder die per Tab fokussiert wurden, werden nicht
  heller, sondern dunkler, um nicht zu viel an Kontrast zu verlieren

## Konsequenzen

Die Einstellungen sind alle zentral definiert und gelten für alle `v-btn`-Tags, die das `active`-Prop erhalten.
