CREATE TABLE WahllokalZustand
(
    id              VARCHAR2(36) NOT NULL,
    wahlbezirkID    VARCHAR2(36) NOT NULL,
    zuletztGesehen  TIMESTAMP,
    letzteAbmeldung TIMESTAMP,

    PRIMARY KEY (id),

    FOREIGN KEY (wahlbezirkID) REFERENCES Wahlbezirk(id)
);

CREATE TABLE Druckzustand
(
    wahllokalzustandID          VARCHAR2(36) NOT NULL,
    wahlID                      VARCHAR2(36) NOT NULL,
    schnellmeldungSendenUhrzeit TIMESTAMP,
    niederschriftSendenUhrzeit  TIMESTAMP,
    schnellmeldungDruckUhrzeit  TIMESTAMP,
    niederschriftDruckUhrzeit   TIMESTAMP,

    FOREIGN KEY (wahllokalzustandID) REFERENCES WahllokalZustand(id),
    FOREIGN KEY (wahlID) REFERENCES Wahl(id)
);