UPDATE Zurueckweisegruende
SET zurueckweisegruende = REPLACE(zurueckweisegruende, 'LOSE_STIMMZETTEL', 'ZUGELASSEN')
WHERE INSTR(zurueckweisegruende, 'LOSE_STIMMZETTEL') > 0;