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

## Servicearchitektur

### Backendservices

![Aufbau eines Backenservices](/structureOfABackendservice.drawio.png)

Ein Backendservice besteht in der Regel aus 3 Layern.

- `access layer` ... Ermöglicht den Zugriff auf den Service
- `service layer` ... Durchführung der fachlichen Logik, wie Validierung oder Lesen und Speichern sowie die Verwendung  
dritter Services zur Erfüllung der Aufgaben
- `persistence layer` ... Zugriff auf die Datenbank

#### Security

Komponenten die eine rote Umrandung haben sind Teil der Security.

Zum einen gibt es eine Zugriffskontrolle im `access layer`. Hier wird geprüft, ob für den Zugriff auf die geforderte
Resource die erforderliche Authentifizierung gegeben ist. Die meisten Ressourcen erfordern eine Authentifizierung. Es gibt 
wenige Resource die ohne Authentifizierung abrufbar sind.

Die Security im `service layer` und `persistence layer` prüft die Autorisierung. Die Methoden erfordern in der Regel
mindestens 1 der funktion entsprechendes Recht. Das Verändern der Daten eines bestimmten Wahlbezirkes erfordert auch
der User berechtigt ist für diesen Wahlbezirk Daten zu verändern.

```mermaid

sequenceDiagram
    participant request as Request
    participant proxy as ServiceProxy
    participant hasAuthority
    participant bezirkIDCheck
    participant service as Service

    request ->> proxy : call Method A
    
    proxy ->> hasAuthority : check Authority
    hasAuthority ->> hasAuthority : throw Exception when missing

    proxy ->> bezirkIDCheck : check wahlbezirkID
    bezirkIDCheck ->> bezirkIDCheck : throw Exception when not matching

    proxy ->> service : call Method A
    service ->> proxy : result of A
    proxy ->> request : result of A

```

`bezirkIDCheck` wird definiert durch das Interface
`de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator` und hat folgende Implementierungen:

- `BezirkIDPermissionEvaluatorImpl` ... führt eine Prüfung des Authenticationobjektes durch
- `DummyBezirkIdPermissionEvaluatorImpl` ... liefert immer `true`

#### access layer

#### service layer

Im Servicelayer befinden sind die Klassen, Interfaces und Records die zur Umsetzung der fachlichen Anforderungen
notwendig sind.

##### Komponenten

![Aufbau eines Backenservices](/componentsOfServiceLayer.drawio.png)

[Designentscheidung](../adr/adr002-controller-service-datamodels.md) zu den Datenmodellen für die Kommunikation zwischen den Layern.



```mermaid

classDiagram

    namespace package_domain {

        class EntityA {

        }

    }

    namespace package_service_serviceA {
        class ServiceA {
            <<Class>>
            doSth(inputModel: ModelA1) ModelA2
        }

        class ModelA1 {
            <<Record>>
        }

        class ModelA2 {
            <<Record>>
        }

        class AValidator {
            <<Class>>
            validateA1(model: A1) throw FachlicheWlsException
        }

        class AMapper {
            <<Interface>>

            toModel(entity: EntityA) ModelA1
            toEntity(model: ModelA1) EntityA
        }

        class ClientExternalService {
            <<Interface>>
            getData() ModelA2
        }
    }

    namespace package_clients {
        class DummyClientImpl {
            <<Class>>
        }

        class ExternalServiceImpl {
            <<Class>>
        }
    }

    namespace package_eai_externalService {

        class ExternalControllerAPI {
            <<generated>>
        }

        class ExternalDtoA {
            <<generated>>
        }

        class ExternalDtoB {
            <<generated>>
        }
    }

    ServiceA --> AMapper : use
    ServiceA --> AValidator : use
    ServiceA --> ClientExternalService : use
    AValidator -- ModelA1
    AValidator -- ModelA2

    AMapper -- EntityA
    AMapper -- ModelA1

    DummyClientImpl ..|> ClientExternalService
    ExternalServiceImpl ..|> ClientExternalService

    ExternalControllerAPI -- ExternalDtoA
    ExternalControllerAPI -- ExternalDtoB
    ExternalServiceImpl --> ExternalControllerAPI : use

```

<details>

<summary>Beispiel Packagestruktur in Basisdatenservice</summary>

```
├─ clients
|     ├─ DummyClientImpl
|     ├─ WahlbezirkeClientImpl
|     └─ WahlbezirkeClientMapper
├─ service
|     ├─ common
|     |    ├─ WahlbezirkArtModel
|     |    └─ WahltagIdUndWahlbezirksart
|     ├─ wahlbezirke
|     |    ├─ WahlbezirkeClient
|     |    ├─ WahlbezirkeValidator
|     |    ├─ WahlbezirkeService
|     |    ├─ WahlbezirkModel
|     |    └─ WahlbezirkModelMapper
```

</details>

#### persistence layer

```mermaid

classDiagram
    class EntityXRepository~wlsService.EntityX,UUID|String~ {
        <<interface>>
    }

    class EntityX {
        <<Entity>>
    }

    class CrudRepository {
        <<interface>>
    }
    
    class Service {
        <<Class>>        
    }

    EntityXRepository --|> CrudRepository
    EntityXRepository --> EntityX : for
    Service --> EntityXRepository : call

```

Für den Zugriff verwenden wird [Spring-Data](https://spring.io/projects/spring-data).

Die Klasse und Interfaces werden im Package `domain` abgelegt. Analog zu den Services werden Subpackages je
Themenkomplex definiert.

Klassen die durch Entitäten verschiedene Subpackages verwenden werden sind in dem Subpackage `common` abzulegen.

<details>

<summary>Beispiel Packagestruktur in Basisdatenservice</summary>

```
├─ domain
|     ├─ common
|     |    ├─ WahlbezirkArt
|     |    └─ WahltagIdUndWahlbezirksart
|     ├─ handbuch
|     |    ├─ Handbuch
|     |    └─ HandbuchRepository
|     ├─ ungueltigeWahlscheine
|     |    ├─ UngueltigeWahlscheine
|     |    └─ UngueltigeWahlscheineRepository
```

</details>