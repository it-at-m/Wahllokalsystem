#!/bin/bash

# Überprüfen, ob die Eingabedatei angegeben wurde
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <input_file>"
    exit 1
fi

input_file="$1"
output_file="output.ldif"

# Erstellen einer neuen LDIF-Datei
{
    # Fester Teil
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

    # Benutzer hinzufügen
    while IFS= read -r username; do
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
    done < "$input_file"

    # Letzter Teil
    echo "dn: cn=user,ou=groups,dc=springframework,dc=org"
    echo "objectclass: top"
    echo "objectclass: groupOfNames"
    echo "cn: user"

    # Mitglieder hinzufügen
    while IFS= read -r username; do
        echo "member: uid=${username},ou=people,dc=springframework,dc=org"
    done < "$input_file"

} > "$output_file"

echo "LDIF file '$output_file' has been generated."
