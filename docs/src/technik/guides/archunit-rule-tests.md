# Testen der ArchUnit Rules

Um sicherzustellen, dass die in `wls-common` definierten ArchUnit Rules auch korrekt implementiert wurden, werden diese 
separat getestet.

## Bekanntes Problem
Um einen der Tests zur Verifizierung von ArchUnit Rules auszuführen, kann dieser zum aktuellen Zeitpunkt nicht über den
play-button in der IDE gestartet werden, da dies zu einem Fehler _"No tests were found"_ führt.

Um also die Verifizierungstests laufen lassen zu können, muss stattdessen `mvn test` ausgeführt werden.

::: info
🚧 dieses Verhalten wird mit [Issue #838](https://github.com/it-at-m/Wahllokalsystem/issues/838) geklärt.
::: 