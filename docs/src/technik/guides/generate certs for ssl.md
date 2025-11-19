# Zertifikate für SSL erstellen

## Kontext

Der Zugriff auf das Wahllokalsystem soll über eine gesicherte Verbindung erfolgen (siehe
[Architektur der Laufzeitumgebung](/technik/systemspecification/#architektur-der-laufzeitumgebung)). Bei der Entwicklung
kommen Reverse-Proxies zum Einsatz, welche eine https-Verbindung verlangen, aber mit den Services dann via http
kommunizieren. Für die https-Verbindungen müssen die Reverse-Proxies entsprechend Zertifikate bereitstellen.

Die Verwendung der Zertifikate ist in der Konfiguration des nginx (`nginx.conf`) vorgenommen.

## Zertifikat erstellen

Public und Private Key werden mit OpenSSL erzeugt

```bash
# Zertifikat für ca. 10 Jahre erstellen
openssl req -x509 -nodes -days 3650 -newkey rsa:2048 -keyout key.pem -out cert.pem
```

Dabei werden diverse Parameter abgefragt. Folgende Werte sollten dabei verwendet werden:
- country: `de`
- province: `bavaria`
- city: `munich`
- organization: `wls`
- organization unit: `it@m`
- common name: Hier muss der FQDN des Reverse-Proxies verwendet werden (siehe `server_name` in `nginx.conf`)

## Truststore erstellen

Service die eine https-Verbindung aufbauen müssen benötigen entsprechend einen Truststore. Dieser wird
wie folgt erstellt:

```bash
# Konvertierung des public key
openssl x509 -outform der -in cert.pem -out cert.der

# Erstellung des Truststore
keytool -importcert -alias wls-auth-service -file cert.der -keystore truststore.p12 -storetype PKCS12 -storepass changeit
```

## Truststore einbinden

Die Einbindung des Truststore in einen Service erfolgt durch folgende vm-Options:
- `-Djavax.net.ssl.trustStore=./../stack/reverse-proxy-auth-service/ssl/truststore.p12 -Djavax.net.ssl.trustStorePassword=changeit -Djavax.net.ssl.trustStoreType=PKCS12`