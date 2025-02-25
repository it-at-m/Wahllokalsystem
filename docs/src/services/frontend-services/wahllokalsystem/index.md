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