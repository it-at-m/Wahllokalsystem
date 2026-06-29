ALTER TABLE Kopfdaten
  ADD maxstimmenprowaehler NUMBER;

UPDATE Kopfdaten
SET maxstimmenprowaehler = 40
WHERE WAHLID = 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e';