CREATE TABLE wahlbezirk
(
    id                  VARCHAR2(36) NOT NULL,
    wahlbezirkArt       VARCHAR2(3)  NOT NULL,
    nummer              VARCHAR2(36) NOT NULL,
    a1                  NUMBER       NOT NULL,
    a2                  NUMBER       NOT NULL,
    a3                  NUMBER       NOT NULL,

    stimmzettelgebietID VARCHAR2(36) NOT NULL,

    FOREIGN KEY (stimmzettelgebietID) REFERENCES stimmzettelgebiet (id),

    PRIMARY KEY (id)
)