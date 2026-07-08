ALTER TABLE Kopfdaten
  ADD maxstimmenprowaehler NUMBER;

UPDATE Kopfdaten
SET maxstimmenprowaehler = 40
WHERE WAHLID IN (
    SELECT wahlid FROM WAHL WHERE wahlart = 'MBW'
    );