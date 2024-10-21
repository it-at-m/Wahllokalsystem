# Auth-Service

Zuständig für die Authentifizierung und Verwaltung der Rechte der User des Systems.

Der Service stellt auch die Loginmaske zur Verfügung. Dazu wird [Freemarker](https://freemarker.apache.org/index.html)
verwendet. Mittels [wro4j](https://github.com/wro4j/wro4j) werden JavaScript Ressource (jquery und Bootstrap)
zur Verfügung gestellt. Im Projekt sind zusätzliche Ressourcen im Ordner `resources-non-filtered` hinterlegt.

## Abhängigkeiten

- Infomanagement-Service

## Datenmodell

```mermaid

erDiagram
    User 1--0+ Authority : hat
    Authority 1--0+ Permission : hat
    User 1--|o LoginAttempt : unternahm
    
    User {
        String username
        boolean userEnabled
        boolean accountNonLocked
        String wahltagID
        LocalDate wahltag
        String wahlbezirkID
        String wahlbezirkNummer
        Wahlbezirksart wahlbezirksArt
        String pin
        String wbid_wahlnummer
    }
    
    Authority {
        String authority
    }
    
    Permission {
        String permission
    }
    
    LoginAttempt {
        int attempts
        LocalDateTime lastModified
    }
```

> [!IMPORTANT]
> Der Benutzername liegt in der Datenbank nur verschlüsselt vor.
 
## Login

```mermaid

sequenceDiagram
    
    actor User
    
    User->>+AuthService : Request Login
    
    AuthService->>AuthService: bestimme LoginView
    alt LoginView == Wahllokalsystem
        AuthService->>+InfomanagementService : get Willkommenstext Konfiguration
        InfomanagementService->>-AuthService : Willkommenstext
    end
    AuthService->>AuthService: ergänze Werte für View
    
    AuthService->>-User : LoginView
```

## Konfigrationsparameter

Alle Konfigurationsparameter beginnen mit dem Prefix `serviceauth`

| Name                    | Beschreibung                                                                           | Default |
|-------------------------|----------------------------------------------------------------------------------------| ------- |
| crypto.encryptionPrefix | String vor dem verschlüssten Wert. Auf diese Weise sind verschlüsselte Werte erkennbar | ENCRYPTED: |
| crypto.key              | Schlüssel zum ver- und entschlüsseln                                                   | |
| maxLoginAttempts        | Maximale Anzahl an Fehlersuchen bis der Account gesperrt wird.                         | 5 |
| clients.infomanagement.basepath | URL zum Infomanagement-Service | `http://localhost:39146` |
| clients.infomanagement.configkey.welcomeMessage | Schlüssel für Konfiguration der Willkommensnachricht | WILLKOMMENSTEXT |
| serviceauth.welcomemessage.default | Standartd Willkommensnachricht falls die definierte Willkommensnachricht nicht geladen werden kann | Willkommen zur Wahl! |