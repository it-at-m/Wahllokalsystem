create table Briefwahlvorbereitung
(
    wahlbezirkid      varchar(1000) not null,
    urnenanzahl       BIGINT        not null,
    primary key (wahlbezirkid)
);