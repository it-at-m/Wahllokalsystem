-- insert wahlvorstand for user wls_all_uwb
INSERT INTO wahlvorstand (id, wahlbezirkID)
VALUES ('00000000-0000-0000-0001-000000000001', '9899cae4-df9a-4ffc-a940-f20cf2280171');
INSERT INTO wahlvorstandsmitglied (id, vorname, nachname, funktion, anwesend, wahlvorstandID, anwesenheitUpdatedOn)
VALUES ('00000000-0000-0000-0001-000000000001', 'Homer', 'Simpson', 'W', 0, '00000000-0000-0000-0001-000000000001',
        null);
INSERT INTO wahlvorstandsmitglied (id, vorname, nachname, funktion, anwesend, wahlvorstandID, anwesenheitUpdatedOn)
VALUES ('00000000-0000-0000-0001-000000000002', 'Marge', 'Simpson', 'SB', 0, '00000000-0000-0000-0001-000000000001',
        null);
INSERT INTO wahlvorstandsmitglied (id, vorname, nachname, funktion, anwesend, wahlvorstandID, anwesenheitUpdatedOn)
VALUES ('00000000-0000-0000-0001-000000000003', 'Bart', 'Simpson', 'SWB', 0,
        '00000000-0000-0000-0001-000000000001', null);
INSERT INTO wahlvorstandsmitglied (id, vorname, nachname, funktion, anwesend, wahlvorstandID, anwesenheitUpdatedOn)
VALUES ('00000000-0000-0000-0001-000000000004', 'Lisa', 'Simpson', 'SSB', 0, '00000000-0000-0000-0001-000000000001',
        null);
INSERT INTO wahlvorstandsmitglied (id, vorname, nachname, funktion, anwesend, wahlvorstandID, anwesenheitUpdatedOn)
VALUES ('00000000-0000-0000-0001-000000000005', 'Maggie', 'Simpson', 'B', 0, '00000000-0000-0000-0001-000000000001',
        null);

-- insert wahlvorstand for user wls_all_bwb
INSERT INTO wahlvorstand (id, wahlbezirkID)
VALUES ('00000000-0000-0000-0001-000000000002', '9081145c-dc16-4a9b-8255-5c49ddb98c14');
INSERT INTO wahlvorstandsmitglied (id, vorname, nachname, funktion, anwesend, wahlvorstandID, anwesenheitUpdatedOn)
VALUES ('00000000-0000-0000-0002-000000000001', 'Peter', 'Griffin', 'W', 0, '00000000-0000-0000-0001-000000000002',
        null);
INSERT INTO wahlvorstandsmitglied (id, vorname, nachname, funktion, anwesend, wahlvorstandID, anwesenheitUpdatedOn)
VALUES ('00000000-0000-0000-0002-000000000002', 'Lois', 'Griffin', 'SB', 0, '00000000-0000-0000-0001-000000000002',
        null);
INSERT INTO wahlvorstandsmitglied (id, vorname, nachname, funktion, anwesend, wahlvorstandID, anwesenheitUpdatedOn)
VALUES ('00000000-0000-0000-0002-000000000003', 'Megan', 'Griffin', 'SWB', 0,
        '00000000-0000-0000-0001-000000000002', null);
INSERT INTO wahlvorstandsmitglied (id, vorname, nachname, funktion, anwesend, wahlvorstandID, anwesenheitUpdatedOn)
VALUES ('00000000-0000-0000-0002-000000000004', 'Christopher', 'Griffin', 'SSB', 0,
        '00000000-0000-0000-0001-000000000002', null);
INSERT INTO wahlvorstandsmitglied (id, vorname, nachname, funktion, anwesend, wahlvorstandID, anwesenheitUpdatedOn)
VALUES ('00000000-0000-0000-0002-000000000005', 'Stewie', 'Griffin', 'B', 0, '00000000-0000-0000-0001-000000000002',
        null);
INSERT INTO wahlvorstandsmitglied (id, vorname, nachname, funktion, anwesend, wahlvorstandID, anwesenheitUpdatedOn)
VALUES ('00000000-0000-0000-0002-000000000006', 'Brian', 'Griffin', 'B', 0, '00000000-0000-0000-0001-000000000002',
        null);