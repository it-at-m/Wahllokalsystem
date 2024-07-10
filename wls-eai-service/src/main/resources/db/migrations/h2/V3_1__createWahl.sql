CREATE TABLE wahl
(
    id        VARCHAR2(36)  NOT NULL,
    name      VARCHAR2(100) NOT NULL,
    wahlart   VARCHAR2(5)   NOT NULL,

    wahltagID VARCHAR2(36)  NOT NULL,

    FOREIGN KEY (wahltagID) REFERENCES wahltag (id),

    PRIMARY KEY (id)

)