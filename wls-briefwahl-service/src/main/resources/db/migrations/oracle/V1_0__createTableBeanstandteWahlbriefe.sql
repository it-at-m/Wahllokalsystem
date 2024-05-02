CREATE TABLE BeanstandeteWahlbriefe
(
    wahlbezirkid             varchar(1024) not null,
    waehlerverzeichnisnummer NUMBER(19, 0) not null,
    primary key (wahlbezirkid, waehlerverzeichnisnummer)
);
