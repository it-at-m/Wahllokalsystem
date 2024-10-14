CREATE TABLE Wahlvorstand
(
    wahlbezirkID      VARCHAR(1024) NOT NULL,
    anwesenheitBeginn TIMESTAMP,

    PRIMARY KEY (wahlbezirkID)
);

CREATE TABLE Wahlvorstandsmitglied
(
    wahlvorstand_wahlbezirkID   VARCHAR(1024) NOT NULL,
    identifikator               VARCHAR(1024) NOT NULL,
    familienname                VARCHAR(255) NOT NULL,
    vorname                     VARCHAR(255) NOT NULL,
    funktion                    VARCHAR(255) NOT NULL,
    funktionsname               VARCHAR(255),
    anwesend                    NUMBER NOT NULL,

    FOREIGN KEY (wahlvorstand_wahlbezirkID) REFERENCES Wahlvorstand (wahlbezirkID)
);