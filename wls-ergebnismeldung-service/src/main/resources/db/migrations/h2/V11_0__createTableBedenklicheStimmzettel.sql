CREATE TABLE BedenklicheStimmzettel
(
    wahlID       VARCHAR(1024) NOT NULL,
    wahlbezirkID VARCHAR(1024) NOT NULL,
    orderIndex   BIGINT        NOT NULL,

    supplements  VARCHAR(1024),
    validity     VARCHAR(1024),

    CONSTRAINT PK_BedenklicheStimmzettel PRIMARY KEY (wahlid, wahlbezirkid, orderIndex)
)