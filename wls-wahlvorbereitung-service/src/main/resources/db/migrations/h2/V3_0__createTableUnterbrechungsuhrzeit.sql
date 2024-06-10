create table UnterbrechungsUhrzeit
(
    wahlbezirkid          varchar(1000) not null,
    unterbrechungsuhrzeit TIMESTAMP     not null,
    primary key (wahlbezirkid)
);
