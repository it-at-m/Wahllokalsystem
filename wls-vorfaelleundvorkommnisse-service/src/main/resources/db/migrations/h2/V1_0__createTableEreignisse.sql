create TABLE Ereignis
(
    wahlbezirkID                  varchar(1024) not null,
    beschreibung                  VARCHAR(1024),
    uhrzeit                       TIMESTAMP,
    ereignisart                   VARCHAR(255)  NOT NULL,

    PRIMARY KEY (wahlbezirkID)
)