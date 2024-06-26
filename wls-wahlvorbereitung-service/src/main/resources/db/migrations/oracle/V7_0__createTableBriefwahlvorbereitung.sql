create table Briefwahlvorbereitung
(
    wahlbezirkid      varchar(1000) not null,
    primary key (wahlbezirkid)
);

create table BWVorbereitung_Urnen
(
    vorbereitung_wahlbezirkID varchar(1000) not null,
    wahlid                    varchar(1000) not null,
    anzahl                    BIGINT        not null,
    urneversiegelt            NUMBER(1),
    constraint fk_Briefwahlvorbereitung
        foreign key (vorbereitung_wahlbezirkID)
            references Briefwahlvorbereitung (wahlbezirkID)
);