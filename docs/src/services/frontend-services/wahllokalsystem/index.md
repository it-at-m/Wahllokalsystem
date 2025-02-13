# Wahllokalsystem

## ServiceWorker

Damit die Anwendung auch ohne eine Netzwerkverbindung genutzt werden kann, wurde mithilfe des 
[`vite-pwa-plugin`](https://vite-pwa-org.netlify.app/) ein Service Worker eingerichtet.

Um während der Entwicklung die Funktionalität des Service Workers zu testen, muss das Skript `build-preview` aus der
`package.json`-Datei gestartet werden. Der SW bezieht sich auf die Elemente, die im `dist`-Ordner liegen, daher muss
die Anwendung nach jeder Änderung im SW-File neu gebaut werden.

### Debugging

Je nach verwendetem Browser sind unterschiedliche Schritte notwendig, um die Konsolenausgaben des SW zu sehen.

- **Chrome**: log-Ausgaben werden in der Browser Konsole mit ausgegeben (F12)
- **Firefox**: log-Ausgaben werden in einer eigenen SW-Konsole angezeigt. 
  ::: details SW-Konsole in Firefox finden
    1. Entwicklertools öffnen (F12)
    2. Zum Tab `Anwendung` navigieren
    3. Auf `about:debugging` klicken
    4. Im neuen Tab den gewünschten SW finden (`wahl-worker.js`) und auf `Untersuchen` klicken
    5. Die Konsole öffnet sich in einem neuen Tab
  :::

### Funktionsweise des SW

Nachdem der SW erfolgreich registriert und installiert wurde, fängt er alle Api-Anfragen ab.
Einige Requests sollten ohne Netzwerkverbindung allerdings nicht durchgeführt werden, daher ist bei diesen die 
`ONLINE_ONLY` Strategie gesetzt. Erkennt der Serviceworker solche Anfragen, werden sie direkt weitergeleitet und 
nicht weiter vom SW verarbeitet. Dies ist zum Beispiel für das Empfangen und Lesen von Broadcast-Nachrichten der Fall. 
Weiterhin prüft der SW, ob beim angemeldeten Benutzer ein Pin hinterlegt ist, welcher benötigt wird, um die Daten
für den lokalen Cache zu verschlüsseln.

**Abfangen von GET Requests**

GET Requests werden grundsätzlich nach der `OFFLINE_FIRST` Strategie behandelt. Das bedeutet, dass die angefragten
Daten vorzugsweise aus dem Cache geladen werden. Ist allerdings die `ONLINE_FIRST` Strategie gesetzt, werden die
Daten direkt aus der Datenbank geholt. Falls dies nicht erfolgreich war, erfolgt ein Fallback auf die Daten im Cache.

::: details Ablaufdiagramm
![sw-get-request.png](/serviceworker/sw-get-request.png)
::: 

**Abfangen von POST Requests**

Bei POST Requests wird immer automatisch versucht, sie direkt in der Backend-Datenbank zu speichern. Anschließend 
werden die Informationen in jedem Fall auch im lokalen Cache hinterlegt. Sollte das Speichern in der Datenbank nicht
erfolgreich sein, erhalten die im Cache hinterlegten Daten das Flag `dirty`. So wird sichergestellt, dass im Nachhinein
alle fehlgeschlagenen Requests erneut synchronisiert werden.

::: details Ablaufdiagramm
![sw-post-request.png](/serviceworker/sw-post-request.png)
::: 