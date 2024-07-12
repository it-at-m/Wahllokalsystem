CREATE TABLE wahltag
(
    id           VARCHAR2(36)   NOT NULL,
    tag          TIMESTAMP      NOT NULL,
    beschreibung VARCHAR2(1024) NOT NULL,
    nummer       VARCHAR2(10)   NOT NULL,

    PRIMARY KEY (id)
);