# Städtische Referenzarchitektur für das Frontend

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Es gibt zwei verschiedene Templates der städtischen Referenzarchitektur. 
Einmal den internen maven-Archetype, bei welchem das Frontend Teil des ApiGateway ist und zum Anderen 
das [Github-Template](https://github.com/it-at-m/refarch-templates/tree/main), bei welchem das Frontend eigenständig ist und das ApiGateway als Docker-Image 
bereitgestellt wird.

In Bezug auf die Wartbarkeit und Komplexität sollte entschieden werden, welches Template für das WLS-Projekt besser geeignet ist.

## Entscheidung

Es wurden damals im alten Code einige Anpassungen am Gateway vorgenommen. Zum Beispiel wurden neue Filter hinzugefügt, 
User-Sessions in der Datenbank gespeichert und alle Daten wurden verschlüsselt übertragen.

Entscheidungskriterium ist, ob diese Änderungen dem generierten ApiGateway-Image des Github-Templates hinzugefügt werden können oder nicht.
Es steht allerdings noch nicht fest, inweiweit diese Einstellungen übernommen oder ggf. abgelöst werden können, daher wurde sich darauf geeinigt, 
vorerst mit dem Github-Template und dem generierten ApiGateway zu arbeiten. Sollten im Nachhinein doch noch Änderungen am Gateway vorgenommen werden müssen, 
wird nicht auf das maven-Archetype-Template gewechselt, sondern der Source-Code des Images manuell zum Projekt hinzugefügt und angepasst.

## Konsequenzen

### positiv

Das Github-Template kommt mit aktuelleren Versionen der Dependencies. Die Wartung und Komplexität des Frontendprojekts gestaltet sich einfacher, da es 
ein kleineres Projekt ist.

### negativ

Sollten noch Änderungen am Gateway vorgenommen werden müssen, muss dieses im Nachhinein manuell erfolgen und könnte etwas mehr Aufwand erfordern.