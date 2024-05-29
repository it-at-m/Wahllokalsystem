create table Wahlbriefdaten
(
    wahlbezirkid                  varchar(1024) not null,
    wahlbriefe                    NUMBER(19, 0),
    verzeichnisseungueltige       NUMBER(19, 0),
    nachtraege                    NUMBER(19, 0),
    nachtraeglichueberbrachte     NUMBER(19, 0),
    zeitnachtraeglichueberbrachte TIMESTAMP,
    primary key (wahlbezirkid)
);
