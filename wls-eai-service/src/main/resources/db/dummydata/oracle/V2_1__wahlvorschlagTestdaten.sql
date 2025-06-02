-- for user wls_all_uwb
INSERT INTO wahlvorschlaegeliste (id, wahltag, wahlID)
VALUES ('00000001-0000-0000-0000-000000000001', TO_TIMESTAMP('2026-01-01', 'YYYY-MM-DD'),
        'd8d2dd22-cbf6-488e-b9bc-b8c2b0ab31a1');

INSERT INTO wahlvorschlaege (id, wahlbezirkID, wahlID, stimmzettelgebietID, wahlvorschlaegelisteID)
VALUES ('00000000-0000-0000-0000-000000000001', '9899cae4-df9a-4ffc-a940-f20cf2280171',
        'd8d2dd22-cbf6-488e-b9bc-b8c2b0ab31a1', '00000000-0000-0000-0000-000000000001',
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


-- for user wls_all_bwb
INSERT INTO wahlvorschlaegeliste (id, wahltag, wahlID)
VALUES ('00000001-0000-0000-0000-000000000002', TO_TIMESTAMP('2026-01-01', 'YYYY-MM-DD'),
        '28039463-3d3d-4ce5-acc1-0bef931a4409');

INSERT INTO wahlvorschlaege (id, wahlbezirkID, wahlID, stimmzettelgebietID, wahlvorschlaegelisteID)
VALUES ('00000000-0000-0000-0000-000000000005', '9081145c-dc16-4a9b-8255-5c49ddb98c14',
        '28039463-3d3d-4ce5-acc1-0bef931a4409', '00000000-0000-0000-0000-000000000005',
        '00000001-0000-0000-0000-000000000002');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000013', 1, 'Vorschlag für Refendum', 1,
        '00000000-0000-0000-0000-000000000005');

INSERT INTO referendumvorlagen (id, wahlbezirkID, wahlID, stimmzettelgebietID)
VALUES ('00000000-0000-0000-0001-000000000001', '9081145c-dc16-4a9b-8255-5c49ddb98c14',
        '28039463-3d3d-4ce5-acc1-0bef931a4409', '00000000-0000-0000-0000-000000000005');

INSERT INTO referendumvorlage (id, wahlvorschlagID, ordnungszahl, kurzname, frage, referendumvorlagenID)
VALUES ('00000000-0000-0002-0002-000000000001', '00000000-0000-0000-0001-000000000013', 1, 'kurzname1', 'frage1',
        '00000000-0000-0000-0001-000000000001');
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
VALUES ('00000000-0000-0002-0002-000000000002', '00000000-0000-0000-0001-000000000013', 2, 'kurzname2', 'frage2',
        '00000000-0000-0000-0001-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000004', 'name1', 1,
        '00000000-0000-0002-0002-000000000002');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000005', 'name2', 2,
        '00000000-0000-0002-0002-000000000002');


INSERT INTO referendumvorlage (id, wahlvorschlagID, ordnungszahl, kurzname, frage, referendumvorlagenID)
VALUES ('00000000-0000-0002-0002-000000000003', '00000000-0000-0000-0001-000000000013', 3, 'kurzname3', 'frage3',
        '00000000-0000-0000-0001-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000006', 'name1', 1,
        '00000000-0000-0002-0002-000000000003');
