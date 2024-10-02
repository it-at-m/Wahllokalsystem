CREATE TABLE ergebnisse
(
    stimmenart                 VARCHAR2(36) NOT NULL,
    wahlvorschlagsordnungszahl BIGINT NOT NULL,
    ergebnis                   BIGINT NOT NULL,
    kandidatID                 VARCHAR2(36) NOT NULL,
    wahlvorschlagID            VARCHAR2(36) NOT NULL,
    ergebnismeldungID          VARCHAR2(36) NOT NULL,

    FOREIGN KEY (ergebnismeldungID) REFERENCES ergebnismeldung (id)
);


