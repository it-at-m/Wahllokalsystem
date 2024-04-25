package de.muenchen.oss.wahllokalsystem.wls.common.exception.util;

public interface ExceptionKonstanten {

    /**
     * Code in der Kategorie CATEGORY_FACHLICH für Parameter unvollständig
     */
    String CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG = "150";
    /**
     * Code in der Kategorie CATEGORY_FACHLICH für gesuchte Entität nicht gefunden
     */
    String CODE_ENTITY_NOT_FOUND = "204";

    /**
     * Code der Kategorie CATEGORY_FACHLICH für "HTTP-Nachricht nicht lesbar".
     */
    String CODE_HTTP_MESSAGE_NOT_READABLE = "450";

    /**
     * Code der Kategorie CATEGORY_TECHNISCH für Fehlersituationen, die bei erneutem Senden des unter
     * fachlichen Gesichtspunkten unveränderten Requests gelingen
     * könnten.
     */
    String CODE_TRANSIENT = "451";

    /**
     * Allgemeiner Code für einen unbekannten Fehler
     */
    String CODE_ALLGEMEIN_UNBEKANNT = "999";

    /**
     * Sicherheitscode wenn der Zugriff verweigert wurde
     */
    String CODE_SECURITY_ACCESS_DENIED = "403";

    /**
     * Fehlermeldung fuer einen unbekannten Fehler
     */
    String MESSAGE_UNBEKANNTER_FEHLER = "Es ist ein unbekannter Fehler aufgetreten";

    /**
     * String fuer Service in Fehlerdaten wenn der Service nicht bekannt ist
     */
    String SERVICE_UNBEKANNT = "_SERVICE_UNBEKANNT_";
}
