# Aktualisieren der embedded-LDAP User

## Kontext

Der [Auth-Service](/services/backend-services/auth-service/index.md) verwendet einen embedded LDAP-Server,
wenn keine LDAP-Verbindung konfiguriert ist. Die Daten dieses Servers werden über ein
[LDIF](https://de.wikipedia.org/wiki/LDAP_Data_Interchange_Format)-File
definiert. Ergibt sich beim Anlegen von Wahltermindaten und damit verbunden bei der Erstellung von Usern, dass neue
User hinzukommen, müssen diese im LDIF-File nachgetragen werden.

## Kurzbeschreibung des Vorgehens

Über das Skript `generateLdif.sh` im Ordner `stack/ldap` des Repositories kann eine ldif-Datei erstellt werden. Als
Input benötigt das Skript eine Datei, die zeilenweise die Benutzer enthält. Nach dem Ausführen des Skriptes sind
die neuen Benutzer und die Zuordnung der Nutzer zu einer Gruppe aus der erzeugten Datei zu kopieren und in das
bestehende Skript zu integrieren. Danach kann man sich zusätzlich mit den generierten Usern einloggen.

## Beschreibung im Detail

### Ausgangslage

Gehen wir davon aus, es gibt folgendes ldif-File:

```text
dn: ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: groups

dn: ou=people,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: people

dn: uid=wls_all_uwb,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: wls_all_uwb
sn: uwb
uid: wls_all_uwb
userPassword: test

dn: uid=wls_all_bwb,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: wls_all_bwb
sn: bwb
uid: wls_all_bwb
userPassword: test

dn: cn=user,ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: groupOfNames
cn: user
member: uid=wls_all_uwb,ou=people,dc=springframework,dc=org
member: uid=wls_all_bwb,ou=people,dc=springframework,dc=org
```

Über dieses File werden zwei User (`wls_all_uwb` und `wls_all_bwb`) definiert. Nur mit diesen beiden Usern ist ein
Login möglich.

### Generierung eines neuen Wahltermins

Wenn ein neuer Wahltermin durch das Wahllokalsystem unterstützt werden soll, muss dieser Wahltermin initialisiert werden.
Das erfolgt über die Admin-GUI. Im Rahmen des Initialisierungsprozesses werden auch Benutzer generiert.
Für das Beispiel gehen wir davon aus, dass folgende Benutzer generiert wurden:

```text
fzh56-wahlbezirk0001
ujt9a-wahlbezirk0002
78nmr-wahlbezirk0003
```

### Erzeugung des neuen ldif-Files

Die Benutzer sollten in einer Datei vorliegen. Im Beispiel gehen wir davon aus, dass die Datei den Namen `exportusers.csv`
hat und im selben Ordner wie das Skript liegt.

```bash
./generateLdif.sh exportusers.csv
```

<em>Befehl zum Ausführen des Bash-Skriptes zur Erzeugung des ldif-Files</em>

Nach erfolgreicher Beendigung des Skripts liegt die Datei `output.ldif` mit dem generierten ldif-File vor.

### Zusammenführen der LDIF

```text:line-numbers {11-19,21-29,31-39,45-47}
dn: ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: groups

dn: ou=people,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: people

dn: uid=fzh56-wahlbezirk0001,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: fzh56-wahlbezirk0001
sn: fzh56-wahlbezirk0001
uid: fzh56-wahlbezirk0001
userPassword: test

dn: uid=ujt9a-wahlbezirk0002,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: ujt9a-wahlbezirk0002
sn: ujt9a-wahlbezirk0002
uid: ujt9a-wahlbezirk0002
userPassword: test

dn: uid=78nmr-wahlbezirk0003,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: 78nmr-wahlbezirk0003
sn: 78nmr-wahlbezirk0003
uid: 78nmr-wahlbezirk0003
userPassword: test

dn: cn=user,ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: groupOfNames
cn: user
member: uid=fzh56-wahlbezirk0001,ou=people,dc=springframework,dc=org
member: uid=ujt9a-wahlbezirk0002,ou=people,dc=springframework,dc=org
member: uid=78nmr-wahlbezirk0003,ou=people,dc=springframework,dc=org
```

Aus dem erzeugten File müssen die erzeugten User und die Zuordnung zur Gruppe (siehe den hervorgehobenen Bereich)
in das bestehende ldif-File übertragen werden.

Nach einem Neustart des Auth-Service kann zusätzlich ein Login mit den neuen Usern erfolgen.

### Das finale ldif-File

```text
dn: ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: groups

dn: ou=people,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: people

dn: uid=wls_all_uwb,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: wls_all_uwb
sn: uwb
uid: wls_all_uwb
userPassword: test

dn: uid=wls_all_bwb,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: wls_all_bwb
sn: bwb
uid: wls_all_bwb
userPassword: test

dn: uid=fzh56-wahlbezirk0001,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: fzh56-wahlbezirk0001
sn: fzh56-wahlbezirk0001
uid: fzh56-wahlbezirk0001
userPassword: test

dn: uid=ujt9a-wahlbezirk0002,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: ujt9a-wahlbezirk0002
sn: ujt9a-wahlbezirk0002
uid: ujt9a-wahlbezirk0002
userPassword: test

dn: uid=78nmr-wahlbezirk0003,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: 78nmr-wahlbezirk0003
sn: 78nmr-wahlbezirk0003
uid: 78nmr-wahlbezirk0003
userPassword: test

dn: cn=user,ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: groupOfNames
cn: user
member: uid=wls_all_uwb,ou=people,dc=springframework,dc=org
member: uid=wls_all_bwb,ou=people,dc=springframework,dc=org
member: uid=fzh56-wahlbezirk0001,ou=people,dc=springframework,dc=org
member: uid=ujt9a-wahlbezirk0002,ou=people,dc=springframework,dc=org
member: uid=78nmr-wahlbezirk0003,ou=people,dc=springframework,dc=org
```
