package de.muenchen.oss.wahllokalsystem.authservice.security;

public class ErrorMessages {

    public static final String BENUTZER_WIRD_GESPERRT = "Falscher Benutzername oder Passwort. Ab dem nächsten Versuch wird der Zugang für zwei Minuten gesperrt.";
    public static final String BENUTZER_WURDE_GESPERRT = "Falscher Benutzername oder Passwort. Benutzer wurde für zwei Minuten gesperrt.";
    public static final String BENUTZER_WURDE_GESPERRT_DAUERT = " Benutzer wurde für zwei Minuten gesperrt. Die Sperre dauert noch: ";
    public static final String INVALID_USERNAME_OR_PASSWORD = "Falscher Benutzername oder Passwort.";
    public static final String INVALID_LOGIN_TIMES = "Anmeldung erfolgte ausserhalb der gültigen Login-Zeiten.";
    public static final String NOT_IN_ACTIVE_ELECTION = "Der Benutzer ist nicht für die aktive Wahl zugelassen.";
}
