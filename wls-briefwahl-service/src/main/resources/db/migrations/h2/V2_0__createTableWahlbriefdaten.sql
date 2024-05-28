create table Wahlbriefdaten
(
    wahlbezirkid                  varchar(1024) not null,
    wahlbriefe                    BIGINT,
    verzeichnisseungueltige       BIGINT,
    nachtraege                    BIGINT,
    nachtraeglichueberbrachte     BIGINT,
    zeitnachtraeglichueberbrachte TIMESTAMP,
    primary key (wahlbezirkid)
);
