CREATE TABLE NACHLIEFERUNGSBEZIRK
(
    wahltagID      VARCHAR(1024) not null,
    wahlbezirkID    VARCHAR(1024) NOT NULL,

    primary key (wahltagID, wahlbezirkID)
);