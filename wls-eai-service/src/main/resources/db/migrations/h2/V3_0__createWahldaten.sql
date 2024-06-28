CREATE TABLE wahltag
(
    id           VARCHAR2(36)   NOT NULL,
    tag          TIMESTAMP      NOT NULL,
    beschreibung VARCHAR2(1024) NOT NULL,
    nummer       VARCHAR2(10)   NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE wahl
(
    id        VARCHAR2(36)  NOT NULL,
    name      VARCHAR2(100) NOT NULL,
    wahlart   VARCHAR2(5)   NOT NULL,
    nummer    VARCHAR2(10)  NOT NULL,

    wahltagID VARCHAR2(36)  NOT NULL,

    FOREIGN KEY (wahltagID) REFERENCES wahltag (id),

    PRIMARY KEY (id)

);