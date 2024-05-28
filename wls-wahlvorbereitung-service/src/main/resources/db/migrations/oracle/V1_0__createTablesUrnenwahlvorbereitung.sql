create table UrnenwahlVorbereitung
(
    wahlbezirkid      varchar(1000) not null,
    anzahlwahlkabinen NUMBER(19, 0) not null,
    anzahlwahltische  NUMBER(19, 0) not null,
    anzahlnebenraeume NUMBER(19, 0) not null,
    primary key (wahlbezirkid)
);

create table UWVorbereitung_Urnen
(
    vorbereitung_wahlbezirkID varchar(1000) not null,
    wahlid                    varchar(1000) not null,
    anzahl                    NUMBER(19, 0) not null,
    urneversiegelt            NUMBER(1),
    constraint fk_UrnenwahlVorbereitung
        foreign key (vorbereitung_wahlbezirkID)
            references UrnenwahlVorbereitung (wahlbezirkID)
);
