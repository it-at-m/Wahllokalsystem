CREATE TABLE Ereignisse
(
    wahlbezirkID                  VARCHAR(1024) NOT NULL,
    keineVorfaelle                BOOLEAN      NOT NULL,
    keineVorkommnisse             BOOLEAN      NOT NULL,

    PRIMARY KEY (wahlbezirkID)
);

CREATE TABLE Ereignis
(
    ereignisse_wahlbezirkID       VARCHAR(1024) NOT NULL,
    beschreibung                  VARCHAR(1024),
    uhrzeit                       TIMESTAMP,
    ereignisart                   VARCHAR(255)  NOT NULL,

    constraint fk_Ereignisse
        foreign key (ereignisse_wahlbezirkID)
            references Ereignisse (wahlbezirkID)
)
