# Auslagerung von Authority Strings

## Status

<adr-status status='rejected'></adr-status>

## Kontext

Es stand die Überlegung im Raum, die in den Services verwendeten Authority Strings als Konstanten auszulagern.
Beispiel:

```java
// Code bisher:
@PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_WRITE_Ereignisse')")

// Vorschlag:
String AUTHORITY_WRITE_EREIGNIS = "hasAuthority('VorfaelleUndVorkommnisse_WRITE_Ereignisse')";

@PreAuthorize(AUTHORITY_WRITE_EREIGNIS)
```

Dies hätte die Vorteile der Wiederverwendbarkeit, besseren Lesbarkeit und ggf. der Reduzierung von Errors durch Tippfehler, würde aber auf der Anderen Seite die Komplexität etwas erhöhen und zu ein paar mehr Codezeilen führen.

## Entscheidung

In einer gemeinsamen Diskussion wurde Entschieden, die Auslagerung nicht vorzunehmen. Sie hat keinen signifikanten Vorteil, da die meisten Strings nur einmal verwendet werden. Der Aufwand, den bestehenden Code anzupassen kann nicht mit dem geringen Nutzen der Änderung gerechtfertigt werden.

## Konsequenzen

### positiv
Es müssen durch den Entschluss keine bestehenden Services angepasst werden. Es entsteht kein zusätzlicher Handlungsbedarf.

### negativ
Möglicherweise ist der Code weniger übersichtlich und es ergeben sich teilweise redundante/doppelte Codezeilen mit gleichem Inhalt.