# Aktualisierung von Images

Um die bestehenden Images zu aktualisieren und neue Versionen zu erhalten, müssen die folgenden Schritte durchgeführt werden:

1. Zuerst müssen die bestehenden Container über die Podman-GUI gelöscht werden.

2. Anschließend müssen im Terminal die neuen Images heruntergeladen werden:

    ```bash
    podman compose pull
    ```

3. Zuletzt müssen die Images gestartet werden, damit sie über die GUI verwendet werden können:

    ```bash
    podman compose up -d
    ```
