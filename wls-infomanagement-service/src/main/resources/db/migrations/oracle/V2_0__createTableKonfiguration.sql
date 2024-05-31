create table Konfiguration
(
    schluessel   varchar(255) not null,
    wert         varchar(1024),
    beschreibung varchar(1024),
    standardwert varchar(1024),
    primary key (schluessel)
);