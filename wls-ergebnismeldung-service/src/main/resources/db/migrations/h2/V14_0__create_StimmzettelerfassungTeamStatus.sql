CREATE TABLE StimmzettelerfassungTeamStatus
(
    wahlID       VARCHAR(1000) NOT NULL,
    wahlbezirkID VARCHAR(1000) NOT NULL,
    teamID       VARCHAR(1000) NOT NULL,
    status       VARCHAR(255)  NOT NULL,
    PRIMARY KEY (wahlID, wahlbezirkID, teamID)
);
