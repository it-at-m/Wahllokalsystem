create Table Ereignisse
(
    wahlbezirkID                  varchar(1024) not null,
    keineVorfaelle                BOOLEAN,
    keineVorkommnisse             BOOLEAN,

    PRIMARY KEY (wahlbezirkID)
);

create Table Ereigniseintrag
(
    ereignisse_wahlbezirkID       VARCHAR(1024),
    beschreibung                  VARCHAR(1024),
    uhrzeit                       TIMESTAMP,
    ereignisart                   VARCHAR(255)  NOT NULL,

    ereignisseID                  VARCHAR(1024) NOT NULL,

    FOREIGN KEY (ereignisseID) REFERENCES ereignisse (wahlbezirkID),

    PRIMARY KEY (ereignisse_wahlbezirkID)
);