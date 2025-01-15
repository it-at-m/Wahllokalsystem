CREATE TABLE Ergebnisse
(
    wahlid       VARCHAR(1024) NOT NULL,
    wahlbezirkid VARCHAR(1024) NOT NULL,
    stapelart    VARCHAR(255)  NOT NULL,
    PRIMARY KEY (wahlid, wahlbezirkid, stapelart)
);

CREATE TABLE Ergebnissammlung
(
    fk_wahlid                  VARCHAR(1024) not null,
    fk_wahlbezirkid            VARCHAR(1024) not null,
    fk_stapelart               VARCHAR(255)  not null,
    wahlvorschlagid            VARCHAR(1024) NOT NULL,
    kandidatid                 VARCHAR(1024) NOT NULL,
    wahlvorschlagsordnungszahl BIGINT        NOT NULL,
    ergebnis                   BIGINT        NOT NULL,
    numindex                   BIGINT        NOT NULL,
    CONSTRAINT fk_Ergebnisse
        FOREIGN KEY (fk_wahlid, fk_wahlbezirkid, fk_stapelart)
            REFERENCES Ergebnisse (wahlid, wahlbezirkid, stapelart)
);