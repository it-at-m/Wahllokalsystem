# Common Monitoring
Monitoring und Health Checks mit [Micrometer](https://micrometer.io/) und [Jolokia](https://jolokia.org/).


## Setup

### Installation
In der Projekt `pom.xml` ist folgende Abhängigkeit notwendig.

```xml
<dependency>
    <groupId>de.muenchen.wls</groupId>
    <artifactId>wls-common-monitoring</artifactId>
    <version>${wls.commons.version}</version>
</dependency>
```

Anschließend muss die Konfiguration `MonitoringConfig.class` aus den `wls-common-monitoring` importiert werden.
Dies kann zum Beispiel in der `MicroServiceApplication`-Klasse erfolgen.

```java
@Import({
        MonitoringConfig.class,
})
public class MicroServiceApplication {
    
    public static void main(String[] args) {
        
    }
    
}

```

### Konfiguration
Das Monitoring bietet folgende Konfigurationen an.

```yaml
wls:
  monitoring:
      enabled: true                 # default true
      jolokia:
        enabled: true               # default false
        hasDatabaseConnection: true # default false
        requiredServices:           # list of required services
          - CONFIGSERVER
          - AUTHSERVICE
      metrics:
        enabled: true                   # default true
        influx:                         # optional, influx properties as key value pairs in camel-case. [Docx](https://micrometer.io/docs/registry/influx)
          enabled: true
          step: PT30S                   # java time format. See [docs](https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html#parse-java.lang.CharSequence-)
          uri: http://10.160.235.225:8087
          db: nagfluxdb
          userName: user
          password: pwd
          connectTimeout: PT5S
          autoCreateDb: false
        prometheus:                     # optional, prometheus properties
          enabled: true                 
        tags:                           # adds key value pair to global tags
          hello: world
          server: wlsc
        hibernate-metrics: true         # default false. Requirement: spring.jpa.properties.hibernate.generate_statistics: true
        open-files-limit-metrics: true  # default false
        hikari-cp-metrics: true         # default false
        processor-metrics: true         # default false
        tomcat-metrics: true            # default false
        process-memory-metrics: true    # default false
        process-thread-metrics: true    # default false
        logback-metrics: true           # default false
        uptime-metrics: true            # default false
        jvm-gc-metrics: true            # default false
        jvm-memory-metrics: true        # default false
        http-request-metrics: true      # default false
        http-request-blacklist:         # default empty list
        http-request-whitelist:         # default list with '/businessActions/'
        http-request-histogram-enabled: true # default true
```


## Jolokia
Das Monitoring stellt einen neuen Rest Endpoint unter `GET http://localhost:8080/jolokia/` bereit.

Eine Beispiel Response für `/jolokia/read/java.lang:type=Memory/HeapMemoryUsage/` ist:
```json
{
  "value":{
    "init":134217728,
    "max":532742144,
    "committed":133365760,
     "used":19046472
  },
  "status":200,
  "timestamp":1244839118,
  "request":{
    "mbean":"java.lang:type=Memory",
    "type":"read",
    "attribute":"HeapMemoryUsage"
  },
  "history":[
    {
      "value":{
        "init":134217728,
        "max":532742144,
        "committed":133365760,
        "used":18958208
      },
    "timestamp":1244839045
    }
  ]
 }
```

Weitere Möglichkeiten finden sich in der [Dokumentation](https://jolokia.org/reference/html/).
