# Broadcast-Service

Dieser Service ermöglicht den Benutzern des Admin-Tools, eine Broadcast-Nachricht über die POST-Methode `broadcast()` an alle Wahllokale zu senden. 
Im [Frontend des Wahllokalsystems](/services/frontend-services/wahllokalsystem/) prüft jedes Wahllokal in regelmäßigen Abständen mit der `getMessage()`-Methode, ob es neue, noch ungelesene Broadcast-Nachrichten gibt, die ihm zugewiesen wurden.
Wenn dies der Fall ist, wird die Nachricht an das Wahllokal übermittelt. Nach der Empfangsbestätigung wird die entsprechende Nachricht für das betroffene Wahllokal über die POST-Methode `deleteMessage()` gelöscht.

## Abhängigkeiten

Der Service hat keine Abhängigkeiten zu anderen Services.

## Daten und Funktionen

### BroadcastMessageDTO

Enthält die Nachricht und die IDs aller zu benachrichtigenden Wahllokalen.

### MessageDTO

Enthält die Nachricht eines einzelnen Wahllokals.

### Post-Methode broadcast(BroadcastMessageDTO broadcastMessageDTO)

Mit dem Objekt `broadcastMessageDTO` erhält der Controller sowohl eine Textnachricht als auch eine ID-Liste aller adressierten Wahllokale.
Das führt zur Speicherung der Text-Nachricht für jedes einzelne Wahllokal aus der Liste.

### Get-Methode getMessage(String wahlbezirkID)

Sucht nach der ältesten Nachricht für die gegebene Wahlbezirk-Id und gibt diese zurück.

### Post-Methode deleteMessage(String nachrichtID)

Löscht die Nachricht mit der gegebenen ID, nachdem sie gelesen wurde. Es wird nur der dem entsprechenden Wahllokal zugewiesene Datenbankeintrag gelöscht.