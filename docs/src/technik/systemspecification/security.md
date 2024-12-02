# Sicherheitskonzept von WLS

## Authentifizierung

Zur Authentifizierung wird OAuth2 mit dem Grant Type `authorization_code` verwenden.
Dabei wird ein Token vom Authorization-Server ([`wls-auth-service`](/services/auth-service)) ausgestellt.
Der ausgestellt Token wird in der Session hinterlegt, die der Client verwenden soll. Anfragen an
Services erfolgen mit der SessionsID an das Gatway, welches die Anfrage dann an den jeweiligen Service
unter Verwendung des Bearer-Tokens weiterleiten.

```mermaid

sequenceDiagram    
    actor User
    
    User->>+Gateway : Anfrage an geschützte Ressource
    Gateway->>-User : Weiterleitung an Login
    
    User->>+AuthService : Loginseite anfordern
    AuthService->>-User : Loginseite
    
    User->>+AuthService : Übermittlung Logindaten
    
    AuthService->>LDAP : Prüfung Userlogin via BIND
    AuthService->>AuthService : Sicherstellen dass User sich anmelden darf
    
    AuthService->>-User: Weiterleiten an `/oauth2/authorize` mit Einmalcode
    
    User->>+AuthService: Aufruf `/oauth2/authorize` mit Einmalcode
    AuthService->>-User: Weiterleiten Gateway mit Einmalcode
    
    User->>+Gateway : /login/oauth2/code mit Einmalcode
    Gateway->>+AuthService : Anfrage nach Token mit Einmalcode
    AuthService->>-Gateway : Bearertoken als JWT
    Gateway->>-User : Setzen von Session-Cookie<br> und Weiterleiten auf geschützte Resource
    
    User->>+Gateway : Anfrage an geschützte Ressource mit Session
    Gateway->>+WlsServiceXYZ : mit Bearer-Token der Session
    WlsServiceXYZ->>-Gateway : Resource
    Gateway->>-User : Resource
```

> [!NOTE]
> Nutzer des Wahllokalsystems dürfen sich nur innerhalb einer bestimmten Zeit anmelden. Nutzer des Admin-Tools
> dürfen sich zu jeder Zeit anmelden. Um welche Art eines Nutzers es sich handelt, wird anhand von Authorities
> bestimmt.