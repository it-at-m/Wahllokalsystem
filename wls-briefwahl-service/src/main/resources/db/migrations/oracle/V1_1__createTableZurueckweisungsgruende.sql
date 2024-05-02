CREATE TABLE Zurueckweisegruende
(
    bw_wahlbezirkid             varchar(1024) not null,
    bw_waehlerverzeichnisnummer NUMBER(19, 0) not null,
    wahlID                      varchar(1024) not null,
    zurueckweisegruende         blob          not null,

    CONSTRAINT fk_bW FOREIGN KEY (bw_wahlbezirkid, bw_waehlerverzeichnisnummer) REFERENCES BeanstandeteWahlbriefe (wahlbezirkid, waehlerverzeichnisnummer)
);
