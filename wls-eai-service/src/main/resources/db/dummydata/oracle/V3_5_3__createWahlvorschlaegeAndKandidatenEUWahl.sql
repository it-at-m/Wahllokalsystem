-- #########################
-- ## für EU-Wahl
-- #########################
-- ## Wahlbezirk1 -
-- #########################
INSERT INTO wahlvorschlaege (id, wahlbezirkID, wahlID, stimmzettelgebietID, wahlvorschlaegelisteID)
VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001',
        '00000001-0000-0000-0000-000000000001');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000001', 1, 'Die Besten', 1,
        '00000000-0000-0000-0000-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000001', 'Clapton', 1, 0, 10, 0,
        '00000000-0000-0000-0001-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000002', 'Jagger', 2, 0, 11, 0,
        '00000000-0000-0000-0001-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000003', 'Prince', 3, 0, 15, 0,
        '00000000-0000-0000-0001-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000004', 'Mac', 4, 0, 16, 1,
        '00000000-0000-0000-0001-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000005', 'Cool', 5, 1, 25, 1,
        '00000000-0000-0000-0001-000000000001');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000002', 2, 'Die Zweitbesten', 1,
        '00000000-0000-0000-0000-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000006', 'Bush', 1, 0, 10, 0,
        '00000000-0000-0000-0001-000000000002');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000007', 'Clinton', 2, 0, 11, 0,
        '00000000-0000-0000-0001-000000000002');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000008', 'Biden', 3, 0, 15, 0,
        '00000000-0000-0000-0001-000000000002');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000009', 'Lincoln', 4, 0, 16, 1,
        '00000000-0000-0000-0001-000000000002');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000010', 'Reagan', 5, 1, 25, 1,
        '00000000-0000-0000-0001-000000000002');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000003', 3, 'Die Drittbesten', 1,
        '00000000-0000-0000-0000-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000011', 'Bush', 1, 0, 10, 0,
        '00000000-0000-0000-0001-000000000003');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000012', 'Clinton', 2, 0, 11, 0,
        '00000000-0000-0000-0001-000000000003');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000013', 'Biden', 3, 0, 15, 0,
        '00000000-0000-0000-0001-000000000003');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000014', 'Lincoln', 4, 0, 16, 1,
        '00000000-0000-0000-0001-000000000003');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000015', 'Reagan', 5, 1, 25, 1,
        '00000000-0000-0000-0001-000000000003');

-- #########################
-- ## Wahlbezirk2 -
-- #########################
INSERT INTO wahlvorschlaege (id, wahlbezirkID, wahlID, stimmzettelgebietID, wahlvorschlaegelisteID)
VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002',
        '00000001-0000-0000-0000-000000000001');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000004', 1, 'Die Besten', 1,
        '00000000-0000-0000-0000-000000000002');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000016', 'Clapton', 1, 0, 10, 0,
        '00000000-0000-0000-0001-000000000004');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000017', 'Jagger', 2, 0, 11, 0,
        '00000000-0000-0000-0001-000000000004');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000018', 'Prince', 3, 0, 15, 0,
        '00000000-0000-0000-0001-000000000004');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000019', 'Mac', 4, 0, 16, 1,
        '00000000-0000-0000-0001-000000000004');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000020', 'Cool', 5, 1, 25, 1,
        '00000000-0000-0000-0001-000000000004');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000005', 2, 'Die Zweitbesten', 1,
        '00000000-0000-0000-0000-000000000002');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000021', 'Bush', 1, 0, 10, 0,
        '00000000-0000-0000-0001-000000000005');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000022', 'Clinton', 2, 0, 11, 0,
        '00000000-0000-0000-0001-000000000005');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000023', 'Biden', 3, 0, 15, 0,
        '00000000-0000-0000-0001-000000000005');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000024', 'Lincoln', 4, 0, 16, 1,
        '00000000-0000-0000-0001-000000000005');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000025', 'Reagan', 5, 1, 25, 1,
        '00000000-0000-0000-0001-000000000005');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000006', 3, 'Die Drittbesten', 1,
        '00000000-0000-0000-0000-000000000002');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000026', 'Bush', 1, 0, 10, 0,
        '00000000-0000-0000-0001-000000000006');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000027', 'Clinton', 2, 0, 11, 0,
        '00000000-0000-0000-0001-000000000006');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000028', 'Biden', 3, 0, 15, 0,
        '00000000-0000-0000-0001-000000000006');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000029', 'Lincoln', 4, 0, 16, 1,
        '00000000-0000-0000-0001-000000000006');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000030', 'Reagan', 5, 1, 25, 1,
        '00000000-0000-0000-0001-000000000006');

-- #########################
-- ## Wahlbezirk3 -
-- #########################
INSERT INTO wahlvorschlaege (id, wahlbezirkID, wahlID, stimmzettelgebietID, wahlvorschlaegelisteID)
VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003',
        '00000001-0000-0000-0000-000000000001');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000007', 1, 'Die Besten', 1,
        '00000000-0000-0000-0000-000000000003');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000031', 'Clapton', 1, 0, 10, 0,
        '00000000-0000-0000-0001-000000000007');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000032', 'Jagger', 2, 0, 11, 0,
        '00000000-0000-0000-0001-000000000007');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000033', 'Prince', 3, 0, 15, 0,
        '00000000-0000-0000-0001-000000000007');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000034', 'Mac', 4, 0, 16, 1,
        '00000000-0000-0000-0001-000000000007');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000035', 'Cool', 5, 1, 25, 1,
        '00000000-0000-0000-0001-000000000007');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000008', 2, 'Die Zweitbesten', 1,
        '00000000-0000-0000-0000-000000000003');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000036', 'Bush', 1, 0, 10, 0,
        '00000000-0000-0000-0001-000000000008');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000037', 'Clinton', 2, 0, 11, 0,
        '00000000-0000-0000-0001-000000000008');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000038', 'Biden', 3, 0, 15, 0,
        '00000000-0000-0000-0001-000000000008');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000039', 'Lincoln', 4, 0, 16, 1,
        '00000000-0000-0000-0001-000000000008');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000040', 'Reagan', 5, 1, 25, 1,
        '00000000-0000-0000-0001-000000000008');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000009', 3, 'Die Drittbesten', 1,
        '00000000-0000-0000-0000-000000000003');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000041', 'Bush', 1, 0, 10, 0,
        '00000000-0000-0000-0001-000000000009');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000042', 'Clinton', 2, 0, 11, 0,
        '00000000-0000-0000-0001-000000000009');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000043', 'Biden', 3, 0, 15, 0,
        '00000000-0000-0000-0001-000000000009');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000044', 'Lincoln', 4, 0, 16, 1,
        '00000000-0000-0000-0001-000000000009');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000045', 'Reagan', 5, 1, 25, 1,
        '00000000-0000-0000-0001-000000000009');

-- #########################
-- ## Wahlbezirk4 -
-- #########################
INSERT INTO wahlvorschlaege (id, wahlbezirkID, wahlID, stimmzettelgebietID, wahlvorschlaegelisteID)
VALUES ('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000004',
        '00000001-0000-0000-0000-000000000001');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000010', 1, 'Die Besten', 1,
        '00000000-0000-0000-0000-000000000004');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000046', 'Clapton', 1, 0, 10, 0,
        '00000000-0000-0000-0001-000000000010');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000047', 'Jagger', 2, 0, 11, 0,
        '00000000-0000-0000-0001-000000000010');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000048', 'Prince', 3, 0, 15, 0,
        '00000000-0000-0000-0001-000000000010');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000049', 'Mac', 4, 0, 16, 1,
        '00000000-0000-0000-0001-000000000010');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000050', 'Cool', 5, 1, 25, 1,
        '00000000-0000-0000-0001-000000000010');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000011', 2, 'Die Zweitbesten', 1,
        '00000000-0000-0000-0000-000000000004');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000051', 'Bush', 1, 0, 10, 0,
        '00000000-0000-0000-0001-000000000011');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000052', 'Clinton', 2, 0, 11, 0,
        '00000000-0000-0000-0001-000000000011');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000053', 'Biden', 3, 0, 15, 0,
        '00000000-0000-0000-0001-000000000011');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000054', 'Lincoln', 4, 0, 16, 1,
        '00000000-0000-0000-0001-000000000011');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000055', 'Reagan', 5, 1, 25, 1,
        '00000000-0000-0000-0001-000000000011');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000012', 3, 'Die Drittbesten', 1,
        '00000000-0000-0000-0000-000000000004');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000056', 'Bush', 1, 0, 10, 0,
        '00000000-0000-0000-0001-000000000012');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000057', 'Clinton', 2, 0, 11, 0,
        '00000000-0000-0000-0001-000000000012');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000058', 'Biden', 3, 0, 15, 0,
        '00000000-0000-0000-0001-000000000012');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000059', 'Lincoln', 4, 0, 16, 1,
        '00000000-0000-0000-0001-000000000012');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000060', 'Reagan', 5, 1, 25, 1,
        '00000000-0000-0000-0001-000000000012');