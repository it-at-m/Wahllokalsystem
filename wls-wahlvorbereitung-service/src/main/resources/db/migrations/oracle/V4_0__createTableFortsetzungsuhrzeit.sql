create table FortsetzungsUhrzeit
(
    wahlbezirkid        varchar(1000) not null,
    fortsetzungsuhrzeit TIMESTAMP     not null,
    primary key (wahlbezirkid)
);
