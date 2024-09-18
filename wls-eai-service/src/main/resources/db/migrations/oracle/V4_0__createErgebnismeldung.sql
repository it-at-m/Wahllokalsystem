CREATE TABLE ergebnismeldung
(
    id                          VARCHAR2(36)   NOT NULL,
    wahlbezirkID                VARCHAR2(36)   NOT NULL,
    wahlID                      VARCHAR2(36)   NOT NULL,
    meldungsart                 VARCHAR2(36),
    a1                      NUMBER,
    a2    NUMBER,
    b    NUMBER,
    b1    NUMBER,
    b2    NUMBER,
    zurueckgewiesenGesamt NUMBER,
    ungueltigeStimmzettel       CLOB  NOT NULL,
    ungueltigeStimmzettelAnzahl NUMBER,
    ergebnisse                  CLOB  NOT NULL,
    wahlart                     VARCHAR2(36)   NOT NULL,

    PRIMARY KEY (id)
);