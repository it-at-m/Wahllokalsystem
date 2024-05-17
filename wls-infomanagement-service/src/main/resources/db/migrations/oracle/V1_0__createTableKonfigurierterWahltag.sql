create table KonfigurierterWahltag
(
    wahltagid varchar(1024) not null,
    wahltag   TIMESTAMP     not null,
    active    number        not null,
    nummer    varchar(1024),
    primary key (wahltagid)
);
