# Keine Verwendung von i18n von Beginn an

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Analog zur RefArch-[Diskussion](https://github.com/it-at-m/refarch-templates/issues/116) haben wir im Projekt überlegt,
ob wir i18n einsetzen werden.

Bibliotheken wie [vue-i18n](https://vue-i18n.intlify.dev/) erlauben es auf einfache Weise zwischen Sprachen zu wechseln.
Dies umfasst sowohl Texte als auch die Formatierung von Zahlen und Daten. In dem die Texte in Ressourcen-Dateien gebündelt werden
und in den Komponenten über Schlüssel auf die entsprechende Texte referenziert wird, können dieselben Texte leicht
wiederverwendet werden.

## Entscheidung

Wir haben uns dagegen entschieden, i18n in der aktuellen Phase einzusetzen. WLS ist für den Münchner Prozess zur
Auszählung der Stimmen. Wir gehen aktuell nicht davon aus der Nutzerkreis der Software eine Mehrsprachigkeit erfordert.

Eine spätere Verwendung von i18n hat keinen relevanten erhöhten Aufwand als wenn wir es von Beginn an verwenden würden.
Alle erstellten Komponenten müssten analysiert und die Texte extrahiert und durch Referenzen ersetzt werden.

## Konsequenzen

### positiv

Die Texte sind direkt in den Komponenten erkennbar was die Verständlichkeit der Komponenten und der Fachdomain erleichtert.
Es gibt eine Bibliothek weniger, von der wir abhängig sind und die zu Pflegen ist, was die Komplexität geringer hält. 

### negativ

Dieselben Texte, vermutlich einzelne Wörter, lassen sich schwer über mehrere Komponenten hinweg identisch halten.

Wir müssen eigenen Formatierungsfunktionen für Zahlen und Daten erstellen.