CREATE TABLE ergebnisse
(
    stimmenart                 VARCHAR2(36) NOT NULL,
    wahlvorschlagsordnungszahl NUMBER NOT NULL,
    ergebnis                   NUMBER NOT NULL,
    kandidatID                 VARCHAR2(36) NOT NULL,
    wahlvorschlagID            VARCHAR2(36) NOT NULL,
    ergebnismeldungID          VARCHAR2(36) NOT NULL,
    erstellungszeit            TIMESTAMP,

    FOREIGN KEY (ergebnismeldungID) REFERENCES ergebnismeldung (id)
);


