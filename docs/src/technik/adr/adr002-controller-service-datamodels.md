# Kriterien für shared oder separated Datenmodell zwischen Service und Controller

## Status

<adr-status status='accepted'></adr-status>

## Kontext

In den Services haben wir unterschiedliche Varianten was die Kopplung der einzelnen Schichten betrifft.

Diese Unterscheidung halten wir für vertretbar da nicht alle Services gleich aufgebaut sein müssen. Wichtig ist, dass
nachvollziehbar ist, welche Variante wann zu bevorzugen ist.

### Shared-Datenmodell bei Broadcast-Service

Im Broadcast-Service verwenden sowohl Controller als auch Service dasselbe Datenmodell. Der Grund dafür war, dass die
Operationen im Service überschaubar waren und das gleiche Modellmodell wie der Controller erforderte. Um die
Komplexität und den Umfang nicht zu erhöhen, ohne dass ein Mehrwert entsteht, haben wir uns dafür entschieden, dass
dieselben Klassen verwendet werden sollen.

### Separates Modell beim Briefwahl-Service

Beim Briefwahl-Service gibt es für Controller und Service jeweils eigene Klassen.

Das ermöglichte, dass im Service monadische Methoden, Methoden mit einem Argument, verwendet werden konnten.
Im Controller waren es drei Argumente, die aber zu einer semantischen Einheit gehörten. Im Service ist es nur ein Argument.

## Entscheidung

Services sollen einheitlich aufgebaut sein. In einem Service sollen die Varianten sich nicht vermischen, um durch
die Einheitlichkeit die Verständlichkeit zu wahren.

### Separates Datenmodell je Layer

![separate models](/allLayersSeparateModel.drawio.png)

Jeder Layer hat sein eigenes Datenmodell. Der Controller mappt bevor er den Service verwendet auf Model, bzw. dessen
Antworten. Der Service macht das Gleiche bei der Kommunikation mit dem Repository.

Dieses Vorgehen ist in der Regel zu wählen, sobald im Controller Pathvariablen oder Requestparameter im Kombination mit
einem Requestbody auftreten.

In diesen Fällen werden für die gleiche Semantik jeweils unterschiedliche Datenmodell verwendet: Mehere Argumente vs.
ein Argument.

### Gemeinsames Datenmodell für Controller und Service

![shared model](/controllerServiceSharedModel.drawio.png)

Controller und Service verwenden dasselbe Datenmodell. Das Repository hat sein eigenes Datenmodell. Der Service
verwendet einen Mapper.

Kann verwendet werden, wenn Controller und Service das gleiche Datenmodell verwenden. Kein Umorganisieren der Parameter
notwendig

## Konsequenzen

### negativ

- zusätzliche Klassen die teilweise identisch sind
- zusätzliches Mapping

### positiv

- konsequente Entkopplung der jeweiligen Schichten (Änderung am Service führt nicht zwangsläufig zu einer Änderung der Rest-API)
- bessere innere kohäsion der Schichten
- bessere Lesbarkeit von Code durch weniger Argumente in Methoden