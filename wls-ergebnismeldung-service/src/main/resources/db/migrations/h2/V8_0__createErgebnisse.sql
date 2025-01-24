CREATE TABLE Ergebnisse
(
    wahlid       VARCHAR(1024) NOT NULL,
    wahlbezirkid VARCHAR(1024) NOT NULL,
    stapelart    VARCHAR(255)  NOT NULL,
    PRIMARY KEY (wahlid, wahlbezirkid, stapelart)
);

CREATE TABLE Ergebnissammlung
(
    fk_wahlid                  VARCHAR(1024) NOT NULL,
    fk_wahlbezirkid            VARCHAR(1024) NOT NULL,
    fk_stapelart               VARCHAR(255)  NOT NULL,
    wahlvorschlagid            VARCHAR(1024),
    kandidatid                 VARCHAR(1024),
    wahlvorschlagsordnungszahl BIGINT,
    ergebnis                   BIGINT        NOT NULL,
    numindex                   BIGINT,
    CONSTRAINT fk_Ergebnisse
        FOREIGN KEY (fk_wahlid, fk_wahlbezirkid, fk_stapelart)
            REFERENCES Ergebnisse (wahlid, wahlbezirkid, stapelart)
);