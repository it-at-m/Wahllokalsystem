CREATE TABLE ungueltigestimmzettel
(
    stimmenart         VARCHAR2(36),
    anzahl             NUMBER,
    wahlvorschlagID    VARCHAR2(36) NOT NULL,
    ergebnismeldungID  VARCHAR2(36) NOT NULL,

    FOREIGN KEY (ergebnismeldungID) REFERENCES ergebnismeldung (id)
);