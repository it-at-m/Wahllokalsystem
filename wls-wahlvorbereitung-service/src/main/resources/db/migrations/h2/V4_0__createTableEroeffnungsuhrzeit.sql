create table Eroeffnungsuhrzeit
(
    wahlbezirkid          varchar(1000) not null,
    eroeffnungsuhrzeit TIMESTAMP     not null,
    primary key (wahlbezirkid)
);
