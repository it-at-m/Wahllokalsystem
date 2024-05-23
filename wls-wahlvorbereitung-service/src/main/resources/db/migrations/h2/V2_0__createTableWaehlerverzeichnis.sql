create table Waehlerverzeichnis
(
    waehlerverzeichnisNummer    NUMBER(19, 0) not null,
    wahlbezirkid                varchar(1000) not null,
    verzeichnislagvor           boolean,
    berichtigungvorbeginn       boolean,
    nachtraeglicheberichtigung  boolean,
    mitteilungungueltigescheine boolean,
    primary key (waehlerverzeichnisNummer, wahlbezirkid)
);
