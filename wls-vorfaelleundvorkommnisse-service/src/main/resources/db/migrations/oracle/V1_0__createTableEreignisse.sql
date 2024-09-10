create TABLE Ereignis
(
    id                            VARCHAR(36) NOT NULL,
    wahlbezirkID                  VARCHAR(1024) NOT NULL,
    beschreibung                  VARCHAR(1024),
    uhrzeit                       TIMESTAMP,
    ereignisart                   VARCHAR(255)  NOT NULL,

    PRIMARY KEY (id)
)