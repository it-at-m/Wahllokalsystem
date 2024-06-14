create table UrnenwahlSchliessungsUhrzeit
(
    wahlbezirkid                 varchar(1000) not null,
    urnenwahlSchliessungsUhrzeit TIMESTAMP     not null,
    primary key (wahlbezirkid)
);
