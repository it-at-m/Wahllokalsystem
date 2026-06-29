ALTER TABLE Kopfdaten
  ADD COLUMN maxstimmenprowaehler INT;

UPDATE Kopfdaten
SET maxstimmenprowaehler = 40
WHERE WAHLID IN (
    SELECT wahlid FROM WAHL WHERE wahlart = 'MBW'
    );