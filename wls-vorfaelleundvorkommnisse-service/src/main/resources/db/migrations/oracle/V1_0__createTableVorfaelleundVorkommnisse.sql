create Table Ereignisse
(
    wahlbezirkID                  varchar(1024) not null,
    keineVorfaelle                NUMBER,
    keineVorkommnisse             NUMBER,

    PRIMARY KEY (wahlbezirkID)
);

create Table Ereigniseintrag
(
    beschreibung                  VARCHAR(1024),
    uhrzeit                       TIMESTAMP,
    ereignisart                   VARCHAR(255)  NOT NULL,

    fk_wahlbezirkID               VARCHAR(1024) NOT NULL,

    FOREIGN KEY (fk_wahlbezirkID) REFERENCES ereignisse (wahlbezirkID)
);