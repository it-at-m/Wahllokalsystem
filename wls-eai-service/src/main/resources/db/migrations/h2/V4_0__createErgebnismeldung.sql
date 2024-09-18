CREATE TABLE ergebnismeldung
(
    id                          VARCHAR2(36)   NOT NULL,
    wahlbezirkID                VARCHAR2(36)   NOT NULL,
    wahlID                      VARCHAR2(36)   NOT NULL,
    meldungsart                 VARCHAR2(36),
    a1                      BIGINT,
    a2    BIGINT,
    b    BIGINT,
    b1    BIGINT,
    b2    BIGINT,
    zurueckgewiesenGesamt BIGINT,
    ungueltigeStimmzettel       CLOB  NOT NULL,
    ungueltigeStimmzettelAnzahl BIGINT,
    ergebnisse                  CLOB  NOT NULL,
    wahlart                     VARCHAR2(36)   NOT NULL,

    PRIMARY KEY (id)
);