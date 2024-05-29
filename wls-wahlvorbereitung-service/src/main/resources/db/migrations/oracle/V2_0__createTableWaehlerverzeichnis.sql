create table Waehlerverzeichnis
(
    waehlerverzeichnisNummer    NUMBER(19, 0) not null,
    wahlbezirkid                varchar(1000) not null,
    verzeichnislagvor           NUMBER(1),
    berichtigungvorbeginn       NUMBER(1),
    nachtraeglicheberichtigung  NUMBER(1),
    mitteilungungueltigescheine NUMBER(1),
    primary key (waehlerverzeichnisNummer, wahlbezirkid)
);
