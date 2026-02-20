# Systemspezifikation vom Wahllokalsystem

🚧 -> <https://github.com/it-at-m/Wahllokalsystem/issues/741>

## Systemarchitektur

### Übersicht der Systeme

```mermaid

flowchart LR

    Wahllokalsystem --> electionManagementSystem[System zur Wahlverwaltung]
    Wahllokalsystem --> LDAP
```

Damit das Wahllokalsystem betrieben werden kann, wird ein externes System benötigt. Dieses stellt Informationen
zu den Wahlen bereit, welche durch das WLS unterstützt werden sollen.

LDAP wird zur [Authentifizierung](security#authentifizierung) der Benutzer verwendet,

### Komponenten des WLS

```mermaid

flowchart TD

    subgraph Wahllokalsystem    
        subgraph Frontends
            wls[Wahllokalsystem UI]
            admin[Admin UI]
        end
    
        subgraph Apigateways
            apiGwWls[ApiGatewayWls]
            apiGwAdmin[ApiGatewayAdminTool]
        end
    
        subgraph Backendservices
            ms1[Microservice 1]
            ms2[Microservice 2]
            ms3[Microservice 3]
            ms4[Microservice 4]
            msn[Microservice N]
        end
    end

    wls --> apiGwWls
    admin --> apiGwAdmin

    apiGwWls --> ms1
    apiGwWls --> ms2
    apiGwWls --> ms3
    
    apiGwAdmin --> ms3
    apiGwAdmin --> ms4
    ms2 --> msn
    ms3 --> msn
    ms4 --> msn
```

Das WLS besteht aus 3 Arten von Komponenten. Die **Frontends** stellen das Userinterface für die Benutzer dar.
Über die **Apigateways** wird der Zugriff auf die **Backendservices** ermöglicht, welche die Anwendungslogik
umsetzen und sich um die Datenhaltung kümmern.

### Architektur der Laufzeitumgebung

![Architektur der Laufzeitumgebung](/RuntimeEnvironment.png)

Das Wahllokalsystem wird in OpenShift betrieben. Der Zugriff auf den Cluster erfolgt über die Routes (Reverse Proxy)
mittels HTTPS. Innerhalb des Clusters wird über HTTP kommuniziert.

Die Datenbank der Services ist nicht Teil des Clusters, sondern befindet sich separat.
