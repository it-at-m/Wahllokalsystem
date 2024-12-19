CREATE TABLE Begruendung
(
    wahlbezirkid        varchar(1024) not null,
    wahlid              varchar(1024) not null,
    stapelart           varchar(1024) not null,
    grund1              varchar(1024) not null,
    grund2              varchar(1024) not null,
    nachzaehlung        BOOLEAN not null,
    unstimmigkeiten     BOOLEAN not null,
    primary key (wahlbezirkid, wahlid, stapelart)
);