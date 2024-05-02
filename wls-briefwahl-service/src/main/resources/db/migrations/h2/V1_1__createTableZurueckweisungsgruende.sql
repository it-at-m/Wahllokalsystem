CREATE TABLE Zurueckweisegruende
(
    bw_wahlbezirkid             varchar(1024) not null,
    bw_waehlerverzeichnisnummer BIGINT        not null,
    wahlID                      varchar(1024) not null,
    zurueckweisegruende         CLOB ARRAY    not null,

    CONSTRAINT fk_bW FOREIGN KEY (bw_wahlbezirkid, bw_waehlerverzeichnisnummer) REFERENCES BeanstandeteWahlbriefe (wahlbezirkid, waehlerverzeichnisnummer)
);
