CREATE TABLE BedenklicheStimmzettel
(
    wahlID       VARCHAR(1024) NOT NULL,
    wahlbezirkID VARCHAR(1024) NOT NULL,

    CONSTRAINT PK_BedenklicheStimmzettel PRIMARY KEY (wahlid, wahlbezirkid)
);

CREATE TABLE BedenklicherStimmzettel
(
    fk_wahlID       VARCHAR(1024) NOT NULL,
    fk_wahlbezirkID VARCHAR(1024) NOT NULL,
    orderIndex      BIGINT        NOT NULL,

    supplements     VARCHAR(1024),
    validity        VARCHAR(1024),

    CONSTRAINT PK_BedenklicherStimmzettel PRIMARY KEY (fk_wahlID, fk_wahlbezirkID, orderIndex),
    CONSTRAINT FK_BedenklicheStimmzettel FOREIGN KEY (fk_wahlID, fk_wahlbezirkID) REFERENCES BedenklicheStimmzettel (wahlID, wahlbezirkID)
);
