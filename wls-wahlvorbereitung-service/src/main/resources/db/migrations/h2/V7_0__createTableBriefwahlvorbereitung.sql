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
    urneversiegelt            BOOLEAN,
    constraint fk_UrnenwahlVorbereitung
        foreign key (vorbereitung_wahlbezirkID)
            references Briefwahlvorbereitung (wahlbezirkID)
);