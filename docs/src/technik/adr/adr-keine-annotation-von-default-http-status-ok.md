# Verzicht auf Annotation von HttpStatus OK bei Controllern

## Status

<adr-status status='accepted'></adr-status>

## Kontext

Wir haben in den Controllern ein gemischtes Verhalten. Manchmal ist die Annotation
`org.springframework.web.bind.annotation.ResponseStatus` mit dem Wert `HttpStatus.OK` und bei manchen Controllern
wieder nicht.

Die Annotation ist nicht erforderlich, wenn der Controller als Returntype keine ResponseEntity hat und den Status
`OK` liefern soll, weil es in diesem Fall das Standardverhalten ist.

## Entscheidung

Wir wollen konsequent auf die Annotation verzichten, wenn sie nicht notwendig ist. Sollte sich das Verhalten ändern
stellen Tests sicher, dass der erwartet HttpStatus geliefert wird.

Mittels ArchUnit-Rules soll die Umsetzung des ADRs sichergestellt werden.

## Konsequenzen

### positiv

Wir haben einen einheitlichen Code.

### negativ

Aufwand durch Anpassung des bestehenden Codes.
