create table Wahl
(
    wahlid                      varchar(1024) not null,
    name                        varchar(255) not null,
    reihenfolge                 bigint not null,
    waehlerverzeichnisnummer    bigint not null,
    wahltag                     datetime not null,
    wahlart                     varchar(255) not null,
    r                           bigint,
    g                           bigint,
    b                           bigint,
    nummer                      varchar(255) not null,

    primary key (wahlid)
);