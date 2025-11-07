#!/bin/bash

# Überprüfen, ob die Eingabedatei angegeben wurde
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <input_file>"
    exit 1
fi

input_file="$1"
output_file="output.ldif"

# Array für Benutzernamen initialisieren
usernames=()

# Benutzer aus der Eingabedatei lesen und in das Array einfügen
while IFS= read -r username || [[ -n "$username" ]]; do
    # Entfernen von führenden und nachfolgenden Leerzeichen
    username="${username#"${username%%[![:space:]]*}"}" # leading whitespace
    username="${username%"${username##*[![:space:]]}"}" # trailing whitespace
    if [ -n "$username" ]; then  # Nur wenn der Benutzername nicht leer ist
        usernames+=("$username")
    fi
done < "$input_file"

# Erstellen einer neuen LDIF-Datei
{
    # Gruppen definieren
    echo "dn: ou=groups,dc=springframework,dc=org"
    echo "objectclass: top"
    echo "objectclass: organizationalUnit"
    echo "ou: groups"
    echo ""
    echo "dn: ou=people,dc=springframework,dc=org"
    echo "objectclass: top"
    echo "objectclass: organizationalUnit"
    echo "ou: people"
    echo ""

    # Benutzer erstellen
    for username in "${usernames[@]}"; do
        echo "dn: uid=${username},ou=people,dc=springframework,dc=org"
        echo "objectclass: top"
        echo "objectclass: person"
        echo "objectclass: organizationalPerson"
        echo "objectclass: inetOrgPerson"
        echo "cn: ${username}"
        echo "sn: ${username}"
        echo "uid: ${username}"
        echo "userPassword: test"
        echo ""
    done

    # Userzuordnung erstellen
    echo "dn: cn=user,ou=groups,dc=springframework,dc=org"
    echo "objectclass: top"
    echo "objectclass: groupOfNames"
    echo "cn: user"

    # Mitglieder der Userzuordnung hinzufügen
    for username in "${usernames[@]}"; do
        echo "member: uid=${username},ou=people,dc=springframework,dc=org"
    done

} > "$output_file"

echo "LDIF file '$output_file' has been generated."
