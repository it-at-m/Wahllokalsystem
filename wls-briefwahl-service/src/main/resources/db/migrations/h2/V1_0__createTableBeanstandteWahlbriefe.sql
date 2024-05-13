CREATE TABLE BeanstandeteWahlbriefe
(
    wahlbezirkid             varchar(1024) not null,
    waehlerverzeichnisnummer BIGINT        not null,
    primary key (wahlbezirkid, waehlerverzeichnisnummer)
);
