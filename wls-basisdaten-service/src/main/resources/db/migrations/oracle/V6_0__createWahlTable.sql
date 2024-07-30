create table Wahl
(
    wahlid                      varchar(1024) not null,
    name                        varchar(255) not null,
    reihenfolge                 NUMBER(19, 0) not null,
    waehlerverzeichnisnummer    NUMBER(19, 0) not null,
    wahltag                     TIMESTAMP  not null,
    wahlart                     varchar(255) not null,
    r                           NUMBER(19, 0) not null,
    g                           NUMBER(19, 0) not null,
    b                           NUMBER(19, 0) not null,
    nummer                      varchar(255) not null,

    PRIMARY KEY (wahlid)
);