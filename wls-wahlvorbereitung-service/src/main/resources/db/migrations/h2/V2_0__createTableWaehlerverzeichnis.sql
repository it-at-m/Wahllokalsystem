create table Waehlerverzeichnis
(
    waehlerverzeichnisNummer    BIGINT        not null,
    wahlbezirkid                varchar(1000) not null,
    verzeichnislagvor           boolean,
    berichtigungvorbeginn       boolean,
    nachtraeglicheberichtigung  boolean,
    mitteilungungueltigescheine boolean,
    primary key (waehlerverzeichnisNummer, wahlbezirkid)
);
