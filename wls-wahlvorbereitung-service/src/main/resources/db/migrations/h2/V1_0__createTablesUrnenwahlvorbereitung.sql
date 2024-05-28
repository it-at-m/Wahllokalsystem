create table UrnenwahlVorbereitung
(
    wahlbezirkid      varchar(1000) not null,
    anzahlwahlkabinen BIGINT        not null,
    anzahlwahltische  BIGINT        not null,
    anzahlnebenraeume BIGINT        not null,
    primary key (wahlbezirkid)
);

create table UWVorbereitung_Urnen
(
    vorbereitung_wahlbezirkID varchar(1000) not null,
    wahlid                    varchar(1000) not null,
    anzahl                    BIGINT        not null,
    urneversiegelt            BOOLEAN,
    constraint fk_UrnenwahlVorbereitung
        foreign key (vorbereitung_wahlbezirkID)
            references UrnenwahlVorbereitung (wahlbezirkID)
);
