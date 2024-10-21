INSERT INTO wahlvorschlaegeliste (id, wahltag, wahlID)
VALUES ('00000001-0000-0000-0000-000000000001', '2024-10-10', 'wahlID');

INSERT INTO wahlvorschlaege (id, wahlbezirkID, wahlID, stimmzettelgebietID, wahlvorschlaegelisteID)
VALUES ('00000000-0000-0000-0000-000000000001', 'wahlbezirkID', 'wahlID', 'stimmzettelgebietID',
        '00000001-0000-0000-0000-000000000001');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000001', 1, 'Die Besten', true,
        '00000000-0000-0000-0000-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000001', 'Clapton', 1, false, 10, false,
        '00000000-0000-0000-0001-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000002', 'Jagger', 2, false, 11, false,
        '00000000-0000-0000-0001-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000003', 'Prince', 3, false, 15, false,
        '00000000-0000-0000-0001-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000004', 'Mac', 4, false, 16, true,
        '00000000-0000-0000-0001-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000005', 'Cool', 5, true, 25, true,
        '00000000-0000-0000-0001-000000000001');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000002', 2, 'Die Zweitbesten', true,
        '00000000-0000-0000-0000-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000006', 'Bush', 1, false, 10, false,
        '00000000-0000-0000-0001-000000000002');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000007', 'Clinton', 2, false, 11, false,
        '00000000-0000-0000-0001-000000000002');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000008', 'Biden', 3, false, 15, false,
        '00000000-0000-0000-0001-000000000002');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000009', 'Lincoln', 4, false, 16, true,
        '00000000-0000-0000-0001-000000000002');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000010', 'Reagan', 5, true, 25, true,
        '00000000-0000-0000-0001-000000000002');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000003', 3, 'Die Drittbesten', true,
        '00000000-0000-0000-0000-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000011', 'Bush', 1, false, 10, false,
        '00000000-0000-0000-0001-000000000003');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000012', 'Clinton', 2, false, 11, false,
        '00000000-0000-0000-0001-000000000003');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000013', 'Biden', 3, false, 15, false,
        '00000000-0000-0000-0001-000000000003');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000014', 'Lincoln', 4, false, 16, true,
        '00000000-0000-0000-0001-000000000003');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0001-000000000015', 'Reagan', 5, true, 25, true,
        '00000000-0000-0000-0001-000000000003');


INSERT INTO referendumvorlagen (id, wahlbezirkID, wahlID, stimmzettelgebietID)
VALUES ('00000000-0000-0000-0002-000000000001', 'wahlbezirkID', 'wahlID', 'stimmzettelgebietID');

INSERT INTO referendumvorlage (id, wahlvorschlagID, ordnungszahl, kurzname, frage, referendumvorlagenID)
VALUES ('00000000-0000-0002-0002-000000000001', 'wahlvorschlagID', 1, 'kurzname1', 'frage1',
        '00000000-0000-0000-0002-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000001', 'name1', 1,
        '00000000-0000-0002-0002-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000002', 'name2', 2,
        '00000000-0000-0002-0002-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000003', 'name3', 3,
        '00000000-0000-0002-0002-000000000001');

INSERT INTO referendumvorlage (id, wahlvorschlagID, ordnungszahl, kurzname, frage, referendumvorlagenID)
VALUES ('00000000-0000-0002-0002-000000000002', 'wahlvorschlagID', 2, 'kurzname2', 'frage2',
        '00000000-0000-0000-0002-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000004', 'name1', 1,
        '00000000-0000-0002-0002-000000000002');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000005', 'name2', 2,
        '00000000-0000-0002-0002-000000000002');


INSERT INTO referendumvorlage (id, wahlvorschlagID, ordnungszahl, kurzname, frage, referendumvorlagenID)
VALUES ('00000000-0000-0002-0002-000000000003', 'wahlvorschlagID', 3, 'kurzname3', 'frage3',
        '00000000-0000-0000-0002-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000006', 'name1', 1,
        '00000000-0000-0002-0002-000000000003');