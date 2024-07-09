CREATE TABLE stimmzettelgebiet
(
    id                    VARCHAR2(36) NOT NULL,
    nummer                VARCHAR2(36),
    name                  VARCHAR2(100),
    stimmzettelgebietsart VARCHAR2(3)  NOT NULL,

    wahlID                VARCHAR2(36) NOT NULL,

    FOREIGN KEY (wahlID) REFERENCES wahl (id),

    primary key (id)
)