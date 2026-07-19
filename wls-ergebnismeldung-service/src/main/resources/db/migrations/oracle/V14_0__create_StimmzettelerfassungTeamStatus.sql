-- Flyway migration (Oracle): create table for per-team Stimmzettelerfassung status
CREATE TABLE StimmzettelerfassungTeamStatus (
  wahlID VARCHAR2(1000) NOT NULL,
  wahlbezirkID VARCHAR2(1000) NOT NULL,
  teamID VARCHAR2(1000) NOT NULL,
  status VARCHAR2(255) NOT NULL,
  CONSTRAINT PK_StimmzettelerfassungTeamStatus PRIMARY KEY (wahlID, wahlbezirkID, teamID)
);
