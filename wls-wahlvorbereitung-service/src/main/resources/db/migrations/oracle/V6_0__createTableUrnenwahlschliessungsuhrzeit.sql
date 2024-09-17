create table UrnenwahlSchliessungsUhrzeit
(
    wahlbezirkid        varchar(1000) not null,
    schliessungsuhrzeit TIMESTAMP     not null,
    primary key (wahlbezirkid)
);
