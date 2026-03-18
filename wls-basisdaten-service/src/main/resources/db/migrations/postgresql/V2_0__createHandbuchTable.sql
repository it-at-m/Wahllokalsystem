CREATE TABLE handbuch
(
    wahltagid      VARCHAR(1024) NOT NULL,
    wahlbezirksart VARCHAR(255)  NOT NULL,
    handbuch       BYTEA         NOT NULL,

    PRIMARY KEY (wahltagid, wahlbezirksart)
);
