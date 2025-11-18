# Anpassungen

## hosts file
Adden:

127.0.0.1 auth.wls.host.docker.internal
127.0.0.1 gui.wls.host.docker.internal

## AuthService
- bei oAuth2 Client mit Port 8083, https als redirect ergänzt
- session cookie muss bei https secure sein
  - bei http muss secure false sein 

# Cert erstellen

openssl req -x509 -nodes -days 3650 -newkey rsa:2048 -keyout key.pem -out cert.pem
- country: de
- province: bavaria
- city: munich
- organization: wls
- org unit: it@m
- common-name: => FQDN (siehe nginx.config `server_name`)

## Truststore erstellen
- openssl x509 -outform der -in cert.pem -out cert.der
- keytool -importcert -alias wls-auth-service -file cert.der -keystore truststore.p12 -storetype PKCS12 -storepass changeit
