# Kriterien für shared oder separated Datenmodell zwischen Service und Controller

## Status

<adr-status status='accepted'></adr-status>

## Kontext

In den Services haben wir unterschiedliche Varianten was die Kopplung der einzelnen Schichten betrifft.

Diese Unterscheidung halten wir für vertretbar da nicht alle Services gleich aufgebaut sein müssen. Wichtig ist, dass
nachvollziehbar ist, welche Variante wann zu bevorzugen ist.

### Shared-Datenmodell bei Broadcast-Service

Im Broadcast-Service verwenden sowohl Controller als auch Service dasselbe Datenmodell. Der Grund dafür war, dass die
Operationen im Service überschaubar sind und das gleiche Modell wie der Controller erfordern. Um die
Komplexität und den Code-Umfang nicht ohne Mehrwert zu erhöhen, haben wir uns für die Verwendung der gleichen Klassen in Controller und Service entschieden.

### Separated-Datenmodell beim Briefwahl-Service

Beim Briefwahl-Service gibt es für Controller und Service jeweils eigene Klassen.

Dadurch wird ermöglicht, dass im Service Methoden mit nur einem Argument verwendet werden können.
Im Controller waren es drei Argumente, die aber zu einer semantischen Einheit gehörten. Im Service ist es nur ein Argument.

## Entscheidung

Grundsätzlich sind mit dem Shared- und Separated-Datenmodell zwei Varianten zur Kopplung von Controller und Service möglich. Die Kriterien dazu sind oben beispielhaft beschrieben.

Services sollen aber einheitlich aufgebaut sein. Um die Verständlichkeit zu wahren dürfen sich die beiden Varianten innerhalb eines Service nicht vermischen.

### Separated-Datenmodell je Layer

![separate models](/allLayersSeparateModel.drawio.png)

Jeder Layer hat sein eigenes Datenmodell. Der Controller mappt die DTO-Klassen vor Verwendung des Service auf die Model-Klassen. Der Service mappt die Model-Klassen auf die Entity-Klassen des Repository.

Dieses Vorgehen ist in der Regel zu wählen, sobald im Controller Pathvariablen oder Requestparameter im Kombination mit
einem Requestbody auftreten.

In diesen Fällen werden für die gleiche Semantik jeweils unterschiedliche Datenmodelle verwendet: Mehrere Argumente vs. ein Argument.

### Shared-Datenmodell für Controller und Service

![shared model](/controllerServiceSharedModel.drawio.png)

Controller und Service verwenden die gleichen DTO-Klassen, ergo dasselbe Datenmodell. Es erfolgt hier kein Mapping. Der Service mappt dann die Model-Klassen auf die Entity-Klassen des Repository.

Kann verwendet werden, wenn Controller und Service das gleiche Datenmodell verwenden. Kein Umorganisieren der Parameter
notwendig

## Konsequenzen

### negativ

- zusätzliche Klassen beim Shared-Datenmodell die teilweise identisch sind
- zusätzliches Mapping beim Shared-Datenmodell

### positiv

- Möglichkeit der Auswahl zwischen den beiden Modellen und dadurch Reduktion der Komplexität beim Shared-Datenmodell
- konsequente Entkopplung der jeweiligen Schichten (Änderung am Service führt nicht zwangsläufig zu einer Änderung der Rest-API) beim Shared-Datenmodell
- bessere innere Kohäsion der Schichten
- bessere Lesbarkeit von Code durch weniger Argumente in Methoden beim Shared-Datenmodell