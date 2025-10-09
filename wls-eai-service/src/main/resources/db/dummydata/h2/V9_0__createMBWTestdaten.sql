-- Testdaten mit AI erstellt
-- Wahl erstellen (Migrationsbeiratswahl)
INSERT INTO Wahl (id, name, wahlart, wahltagId)
VALUES ('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'Migrationsbeiratswahl München 2026', 'MBW',
        '827e9e6a-ebe9-483f-a9cd-162b26f348e8');

-- Stimmzettelgebiet erstellen
INSERT INTO Stimmzettelgebiet (id, nummer, name, stimmzettelgebietsart, wahlId)
VALUES ('c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', 'SZG-001', 'Stimmzettelgebiet München MBW', 'SK',
        'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e');

-- Wahlbezirk 1 (UWB - Urnenwahl)
INSERT INTO Wahlbezirk (id, wahlbezirkArt, nummer, a1, a2, a3, stimmzettelgebietId)
VALUES ('d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a', 'UWB', 'WB-UWB-001', 1500, 1200, 300,
        'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f');

-- Wahlbezirk 2 (BWB - Briefwahl)
INSERT INTO Wahlbezirk (id, wahlbezirkArt, nummer, a1, a2, a3, stimmzettelgebietId)
VALUES ('e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b', 'BWB', 'WB-BWB-001', 2000, 1800, 200,
        'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f');

-- Wahlvorschlägeliste erstellen
INSERT INTO Wahlvorschlaegeliste (id, wahltag, wahlId)
VALUES ('c5d6e7f8-a9b0-4c1d-2e3f-4a5b6c7d8e9f', TIMESTAMP '2026-01-01 00:00:00',
        'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e');

-- ====================================
-- WAHLBEZIRK 1 (UWB) - Wahlvorschläge
-- ====================================
INSERT INTO Wahlvorschlaege (id, wahlbezirkId, wahlId, stimmzettelgebietId, wahlvorschlaegelisteId)
VALUES ('d6e7f8a9-b0c1-4d2e-3f4a-5b6c7d8e9f0a', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a',
        'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f',
        'c5d6e7f8-a9b0-4c1d-2e3f-4a5b6c7d8e9f');

-- Wahlvorschlag 1 (UWB) - 3 Kandidaten
INSERT INTO Wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeId)
VALUES ('e7f8a9b0-c1d2-4e3f-4a5b-6c7d8e9f0a1b', 1, 'Liste Vielfalt', 1, 'd6e7f8a9-b0c1-4d2e-3f4a-5b6c7d8e9f0a');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('f8a9b0c1-d2e3-4f4a-5b6c-7d8e9f0a1b2c', 'Ali Demir', 1, 0, 1, 0, 'e7f8a9b0-c1d2-4e3f-4a5b-6c7d8e9f0a1b');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('a9b0c1d2-e3f4-4a5b-6c7d-8e9f0a1b2c3d', 'Elena Rossi', 2, 0, 2, 0, 'e7f8a9b0-c1d2-4e3f-4a5b-6c7d8e9f0a1b');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('b0c1d2e3-f4a5-4b6c-7d8e-9f0a1b2c3d4e', 'Mohamed Ibrahim', 3, 0, 3, 0, 'e7f8a9b0-c1d2-4e3f-4a5b-6c7d8e9f0a1b');

-- Wahlvorschlag 2 (UWB) - 5 Kandidaten
INSERT INTO Wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeId)
VALUES ('c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f', 2, 'Integration Aktiv', 1, 'd6e7f8a9-b0c1-4d2e-3f4a-5b6c7d8e9f0a');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('d2e3f4a5-b6c7-4d8e-9f0a-1b2c3d4e5f6a', 'Natalia Kowalczyk', 1, 0, 1, 0,
        'c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('e3f4a5b6-c7d8-4e9f-0a1b-2c3d4e5f6a7b', 'Hassan Al-Rashid', 2, 0, 2, 0, 'c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8c', 'Anastasia Petrov', 3, 0, 3, 0, 'c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('a5b6c7d8-e9f0-4a1b-2c3d-4e5f6a7b8c9d', 'Carlos Sanchez', 4, 0, 4, 0, 'c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('b6c7d8e9-f0a1-4b2c-3d4e-5f6a7b8c9d0e', 'Aisha Kamara', 5, 0, 5, 0, 'c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5f');

-- Wahlvorschlag 3 (UWB) - 4 Kandidaten
INSERT INTO Wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeId)
VALUES ('c7d8e9f0-a1b2-4c3d-4e5f-6a7b8c9d0e1f', 3, 'Gemeinsam Stark', 1, 'd6e7f8a9-b0c1-4d2e-3f4a-5b6c7d8e9f0a');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('d8e9f0a1-b2c3-4d4e-5f6a-7b8c9d0e1f2a', 'Dimitrios Papadopoulos', 1, 0, 1, 0,
        'c7d8e9f0-a1b2-4c3d-4e5f-6a7b8c9d0e1f');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('e9f0a1b2-c3d4-4e5f-6a7b-8c9d0e1f2a3b', 'Leyla Öztürk', 2, 0, 2, 0, 'c7d8e9f0-a1b2-4c3d-4e5f-6a7b8c9d0e1f');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('f0a1b2c3-d4e5-4f6a-7b8c-9d0e1f2a3b4c', 'Ivan Petrov', 3, 0, 3, 0, 'c7d8e9f0-a1b2-4c3d-4e5f-6a7b8c9d0e1f');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5e', 'Maria Santos', 4, 0, 4, 0, 'c7d8e9f0-a1b2-4c3d-4e5f-6a7b8c9d0e1f');

-- Wahlvorschlag 4 (UWB) - 6 Kandidaten
INSERT INTO Wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeId)
VALUES ('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6f', 4, 'Brücken Bauen', 1, 'd6e7f8a9-b0c1-4d2e-3f4a-5b6c7d8e9f0a');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7a', 'Yuki Tanaka', 1, 0, 1, 0, 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6f');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8b', 'Fatima Nguyen', 2, 0, 2, 0, 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6f');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9c', 'Andrei Ivanov', 3, 0, 3, 0, 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6f');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8a9b0d', 'Amina Diallo', 4, 0, 4, 0, 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6f');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('a7b8c9d0-e1f2-4a3b-4c5d-6e7f8a9b0c1e', 'Nikolai Volkov', 5, 0, 5, 0, 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6f');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('b8c9d0e1-f2a3-4b4c-5d6e-7f8a9b0c1d2f', 'Zahra Hosseini', 6, 0, 6, 0, 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6f');

-- Wahlvorschlag 5 (UWB) - 3 Kandidaten
INSERT INTO Wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeId)
VALUES ('c9d0e1f2-a3b4-4c5d-6e7f-8a9b0c1d2e3a', 5, 'Kulturmosaik', 1, 'd6e7f8a9-b0c1-4d2e-3f4a-5b6c7d8e9f0a');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('d0e1f2a3-b4c5-4d6e-7f8a-9b0c1d2e3f4b', 'Jin Lee', 1, 0, 1, 0, 'c9d0e1f2-a3b4-4c5d-6e7f-8a9b0c1d2e3a');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('e1f2a3b4-c5d6-4e7f-8a9b-0c1d2e3f4a5c', 'Miriam Cohen', 2, 0, 2, 0, 'c9d0e1f2-a3b4-4c5d-6e7f-8a9b0c1d2e3a');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('f2a3b4c5-d6e7-4f8a-9b0c-1d2e3f4a5b6d', 'Omar Khalil', 3, 0, 3, 0, 'c9d0e1f2-a3b4-4c5d-6e7f-8a9b0c1d2e3a');

-- Wahlvorschlag 6 (UWB) - 5 Kandidaten
INSERT INTO Wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeId)
VALUES ('a3b4c5d6-e7f8-4a9b-0c1d-2e3f4a5b6c7e', 6, 'Weltbürger München', 1, 'd6e7f8a9-b0c1-4d2e-3f4a-5b6c7d8e9f0a');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('b4c5d6e7-f8a9-4b0c-1d2e-3f4a5b6c7d8f', 'Priya Sharma', 1, 0, 1, 0, 'a3b4c5d6-e7f8-4a9b-0c1d-2e3f4a5b6c7e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('c5d6e7f8-a9b0-4c1d-2e3f-4a5b6c7d8e9a', 'Bogdan Nowak', 2, 0, 2, 0, 'a3b4c5d6-e7f8-4a9b-0c1d-2e3f4a5b6c7e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('d6e7f8a9-b0c1-4d2e-3f4a-5b6c7d8e9f0b', 'Layla Abdullah', 3, 0, 3, 0, 'a3b4c5d6-e7f8-4a9b-0c1d-2e3f4a5b6c7e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('e7f8a9b0-c1d2-4e3f-4a5b-6c7d8e9f0a1c', 'Giorgio Bianchi', 4, 0, 4, 0, 'a3b4c5d6-e7f8-4a9b-0c1d-2e3f4a5b6c7e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('f8a9b0c1-d2e3-4f4a-5b6c-7d8e9f0a1b2d', 'Tatiana Sokolova', 5, 0, 5, 0, 'a3b4c5d6-e7f8-4a9b-0c1d-2e3f4a5b6c7e');

-- Wahlvorschlag 7 (UWB) - 4 Kandidaten
INSERT INTO Wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeId)
VALUES ('a9b0c1d2-e3f4-4a5b-6c7d-8e9f0a1b2c3e', 7, 'Zusammen Leben', 1, 'd6e7f8a9-b0c1-4d2e-3f4a-5b6c7d8e9f0a');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('b0c1d2e3-f4a5-4b6c-7d8e-9f0a1b2c3d4f', 'Mehmet Yildiz', 1, 0, 1, 0, 'a9b0c1d2-e3f4-4a5b-6c7d-8e9f0a1b2c3e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('c1d2e3f4-a5b6-4c7d-8e9f-0a1b2c3d4e5a', 'Katarina Novak', 2, 0, 2, 0, 'a9b0c1d2-e3f4-4a5b-6c7d-8e9f0a1b2c3e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('d2e3f4a5-b6c7-4d8e-9f0a-1b2c3d4e5f6b', 'Abdul Rahman', 3, 0, 3, 0, 'a9b0c1d2-e3f4-4a5b-6c7d-8e9f0a1b2c3e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('e3f4a5b6-c7d8-4e9f-0a1b-2c3d4e5f6a7c', 'Rosa Martinez', 4, 0, 4, 0, 'a9b0c1d2-e3f4-4a5b-6c7d-8e9f0a1b2c3e');

-- ====================================
-- WAHLBEZIRK 2 (BWB) - Wahlvorschläge
-- (verwendet die gleichen Kandidaten wie UWB)
-- ====================================
INSERT INTO Wahlvorschlaege (id, wahlbezirkId, wahlId, stimmzettelgebietId, wahlvorschlaegelisteId)
VALUES ('f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8d', 'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b',
        'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f',
        'c5d6e7f8-a9b0-4c1d-2e3f-4a5b6c7d8e9f');

-- Wahlvorschlag 1 (BWB) - 3 Kandidaten (gleiche wie UWB)
INSERT INTO Wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeId)
VALUES ('a5b6c7d8-e9f0-4a1b-2c3d-4e5f6a7b8c9e', 1, 'Liste Vielfalt', 1, 'f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8d');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('b6c7d8e9-f0a1-4b2c-3d4e-5f6a7b8c9d0f', 'Ali Demir', 1, 0, 1, 0, 'a5b6c7d8-e9f0-4a1b-2c3d-4e5f6a7b8c9e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('c7d8e9f0-a1b2-4c3d-4e5f-6a7b8c9d0e1a', 'Elena Rossi', 2, 0, 2, 0, 'a5b6c7d8-e9f0-4a1b-2c3d-4e5f6a7b8c9e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('d8e9f0a1-b2c3-4d4e-5f6a-7b8c9d0e1f2b', 'Mohamed Ibrahim', 3, 0, 3, 0, 'a5b6c7d8-e9f0-4a1b-2c3d-4e5f6a7b8c9e');

-- Wahlvorschlag 2 (BWB) - 5 Kandidaten (gleiche wie UWB)
INSERT INTO Wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeId)
VALUES ('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6a', 2, 'Integration Aktiv', 1, 'f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8d');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7b', 'Natalia Kowalczyk', 1, 0, 1, 0,
        'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6a');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8c', 'Hassan Al-Rashid', 2, 0, 2, 0, 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6a');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9d', 'Anastasia Petrov', 3, 0, 3, 0, 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6a');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8a9b0a', 'Carlos Sanchez', 4, 0, 4, 0, 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6a');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('a7b8c9d0-e1f2-4a3b-4c5d-6e7f8a9b0c1a', 'Aisha Kamara', 5, 0, 5, 0, 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6a');

-- Wahlvorschlag 3 (BWB) - 4 Kandidaten (gleiche wie UWB)
INSERT INTO Wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeId)
VALUES ('f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8a9b0e', 3, 'Gemeinsam Stark', 1, 'f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8d');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('a7b8c9d0-e1f2-4a3b-4c5d-6e7f8a9b0c1f', 'Dimitrios Papadopoulos', 1, 0, 1, 0,
        'f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8a9b0e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('b8c9d0e1-f2a3-4b4c-5d6e-7f8a9b0c1d2a', 'Leyla Öztürk', 2, 0, 2, 0, 'f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8a9b0e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('c9d0e1f2-a3b4-4c5d-6e7f-8a9b0c1d2e3b', 'Ivan Petrov', 3, 0, 3, 0, 'f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8a9b0e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('d0e1f2a3-b4c5-4d6e-7f8a-9b0c1d2e3f4c', 'Maria Santos', 4, 0, 4, 0, 'f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8a9b0e');

-- Wahlvorschlag 4 (BWB) - 6 Kandidaten (gleiche wie UWB)
INSERT INTO Wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeId)
VALUES ('f2a3b4c5-d6e7-4f8a-9b0c-1d2e3f4a5b6e', 4, 'Brücken Bauen', 1, 'f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8d');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('a3b4c5d6-e7f8-4a9b-0c1d-2e3f4a5b6c7f', 'Yuki Tanaka', 1, 0, 1, 0, 'f2a3b4c5-d6e7-4f8a-9b0c-1d2e3f4a5b6e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('b4c5d6e7-f8a9-4b0c-1d2e-3f4a5b6c7d8a', 'Fatima Nguyen', 2, 0, 2, 0, 'f2a3b4c5-d6e7-4f8a-9b0c-1d2e3f4a5b6e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('c5d6e7f8-a9b0-4c1d-2e3f-4a5b6c7d8e9b', 'Andrei Ivanov', 3, 0, 3, 0, 'f2a3b4c5-d6e7-4f8a-9b0c-1d2e3f4a5b6e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('d6e7f8a9-b0c1-4d2e-3f4a-5b6c7d8e9f0c', 'Amina Diallo', 4, 0, 4, 0, 'f2a3b4c5-d6e7-4f8a-9b0c-1d2e3f4a5b6e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('e7f8a9b0-c1d2-4e3f-4a5b-6c7d8e9f0a1e', 'Nikolai Volkov', 5, 0, 5, 0, 'f2a3b4c5-d6e7-4f8a-9b0c-1d2e3f4a5b6e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('f8a9b0c1-d2e3-4f4a-5b6c-7d8e9f0a1b2a', 'Zahra Hosseini', 6, 0, 6, 0, 'f2a3b4c5-d6e7-4f8a-9b0c-1d2e3f4a5b6e');

-- Wahlvorschlag 5 (BWB) - 3 Kandidaten (gleiche wie UWB)
INSERT INTO Wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeId)
VALUES ('e7f8a9b0-c1d2-4e3f-4a5b-6c7d8e9f0a1d', 5, 'Kulturmosaik', 1, 'f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8d');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('f8a9b0c1-d2e3-4f4a-5b6c-7d8e9f0a1b2e', 'Jin Lee', 1, 0, 1, 0, 'e7f8a9b0-c1d2-4e3f-4a5b-6c7d8e9f0a1d');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('a9b0c1d2-e3f4-4a5b-6c7d-8e9f0a1b2c3f', 'Miriam Cohen', 2, 0, 2, 0, 'e7f8a9b0-c1d2-4e3f-4a5b-6c7d8e9f0a1d');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('b0c1d2e3-f4a5-4b6c-7d8e-9f0a1b2c3d4a', 'Omar Khalil', 3, 0, 3, 0, 'e7f8a9b0-c1d2-4e3f-4a5b-6c7d8e9f0a1d');

-- Wahlvorschlag 6 (BWB) - 5 Kandidaten (gleiche wie UWB)
INSERT INTO Wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeId)
VALUES ('f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8e', 6, 'Weltbürger München', 1, 'f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8d');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('a5b6c7d8-e9f0-4a1b-2c3d-4e5f6a7b8c9f', 'Priya Sharma', 1, 0, 1, 0, 'f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('b6c7d8e9-f0a1-4b2c-3d4e-5f6a7b8c9d0a', 'Bogdan Nowak', 2, 0, 2, 0, 'f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('c7d8e9f0-a1b2-4c3d-4e5f-6a7b8c9d0e1b', 'Layla Abdullah', 3, 0, 3, 0, 'f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('d8e9f0a1-b2c3-4d4e-5f6a-7b8c9d0e1f2d', 'Giorgio Bianchi', 4, 0, 4, 0, 'f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8e');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('e9f0a1b2-c3d4-4e5f-6a7b-8c9d0e1f2a3e', 'Tatiana Sokolova', 5, 0, 5, 0, 'f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8e');

-- Wahlvorschlag 7 (BWB) - 4 Kandidaten (gleiche wie UWB)
INSERT INTO Wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeId)
VALUES ('d8e9f0a1-b2c3-4d4e-5f6a-7b8c9d0e1f2c', 7, 'Zusammen Leben', 1, 'f4a5b6c7-d8e9-4f0a-1b2c-3d4e5f6a7b8d');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('e9f0a1b2-c3d4-4e5f-6a7b-8c9d0e1f2a3d', 'Mehmet Yildiz', 1, 0, 1, 0, 'd8e9f0a1-b2c3-4d4e-5f6a-7b8c9d0e1f2c');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('f0a1b2c3-d4e5-4f6a-7b8c-9d0e1f2a3b4e', 'Katarina Novak', 2, 0, 2, 0, 'd8e9f0a1-b2c3-4d4e-5f6a-7b8c9d0e1f2c');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5a', 'Abdul Rahman', 3, 0, 3, 0, 'd8e9f0a1-b2c3-4d4e-5f6a-7b8c9d0e1f2c');

INSERT INTO Kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagId)
VALUES ('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6b', 'Rosa Martinez', 4, 0, 4, 0, 'd8e9f0a1-b2c3-4d4e-5f6a-7b8c9d0e1f2c');
