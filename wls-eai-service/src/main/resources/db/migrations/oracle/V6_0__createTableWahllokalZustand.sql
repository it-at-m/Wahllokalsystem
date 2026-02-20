CREATE TABLE WahllokalZustand
(
    id              VARCHAR2(36) NOT NULL,
    wahlbezirkID    VARCHAR2(36) NOT NULL,
    zuletztGesehen  TIMESTAMP,
    letzteAbmeldung TIMESTAMP,

    PRIMARY KEY (id),

    FOREIGN KEY (wahlbezirkID) REFERENCES Wahlbezirk (id)
);

CREATE TABLE Druckzustand
(
    wahllokalzustandID          VARCHAR2(36) NOT NULL
        CONSTRAINT FK_Wahllokalzustand REFERENCES WahllokalZustand (id),
    wahlID                      VARCHAR2(36) NOT NULL
        CONSTRAINT FK_Wahl REFERENCES Wahl (id),
    schnellmeldungSendenUhrzeit TIMESTAMP,
    niederschriftSendenUhrzeit  TIMESTAMP,
    schnellmeldungDruckUhrzeit  TIMESTAMP,
    niederschriftDruckUhrzeit   TIMESTAMP,

    CONSTRAINT UNIQUE_ENTRIES UNIQUE (wahllokalzustandID, wahlID, schnellmeldungDruckUhrzeit,
                                      schnellmeldungSendenUhrzeit, niederschriftDruckUhrzeit,
                                      niederschriftSendenUhrzeit)
);