# Verzicht auf Annotation von HttpStatus OK bei Controllern

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Wir haben in den Controllern ein gemischtes Verhalten. Manchmal ist die Annotation  
`org.springframework.web.bind.annotation.ResponseStatus` mit dem Wert `HttpStatus.OK` gesetzt, und bei manchen Controllern  
ist die Annotation wieder nicht vorhanden.

Die Annotation ist nicht erforderlich, wenn der Controller als Return-Typ keine `ResponseEntity` hat und den Status  
`OK` liefern soll, da dies in diesem Fall das Standardverhalten ist.

## Entscheidung

Wir wollen konsequent auf die Annotation mit dem Wert `HttpStatus.OK` verzichten, wenn sie nicht notwendig ist.
Sollte sich das Verhalten ändern, stellen Integrationstests sicher, dass der erwartete `HttpStatus` geliefert wird.
Dies betrifft sowohl die Methoden des Controllers als auch den Controller selbst.

Methoden mit dem Rückgabetyp `ResponseEntity` dürfen keine `ResponseStatus`-Annotation besitzen, da bei Verwendung
von `ResponseEntity` die Statusinformationen durch die `ResponseEntity` überschrieben wird.

Mittels ArchUnit-Rules soll die Umsetzung des ADRs sichergestellt werden.

## Konsequenzen

### positiv

- Wir haben einen einheitlichen Code.

### negativ

- Aufwand durch Anpassung des bestehenden Codes.
