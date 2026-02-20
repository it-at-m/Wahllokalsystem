-- Wahlvorschläge für die OBW im UWB (wahlbezirkID 9899cae4-df9a-4ffc-a940-f20cf2280172)
INSERT INTO wahlvorschlaege (id, wahlbezirkID, wahlID, stimmzettelgebietID, wahlvorschlaegelisteID)
VALUES ('00000000-0000-0000-0000-000000000006', '9899cae4-df9a-4ffc-a940-f20cf2280172',
        'd8d2dd22-cbf6-488e-b9bc-b8c2b0ab31a1', '00000000-0000-0000-0000-000000000001',
        '00000001-0000-0000-0000-000000000001');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0006-000000000001', 1, 'Vorschlag für Referendum', 1,
        '00000000-0000-0000-0000-000000000006');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0006-0006-000000000001', 'Stark', 1, 0, 11, 0,
        '00000000-0000-0000-0006-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0006-0006-000000000002', 'Lannister', 2, 0, 16, 0,
        '00000000-0000-0000-0006-000000000001');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0006-0006-000000000003', 'Baratheon', 3, 1, 25, 1,
        '00000000-0000-0000-0006-000000000001');

INSERT INTO referendumvorlagen (id, wahlbezirkID, wahlID, stimmzettelgebietID)
VALUES ('00000000-0006-0001-0001-000000000001', '9899cae4-df9a-4ffc-a940-f20cf2280172',
        'd8d2dd22-cbf6-488e-b9bc-b8c2b0ab31a1', '00000000-0000-0000-0000-000000000001');

INSERT INTO referendumvorlage (id, wahlvorschlagID, ordnungszahl, kurzname, frage, referendumvorlagenID)
VALUES ('00000000-0006-0001-0002-000000000001', '00000000-0000-0000-0006-000000000001', 1, 'kurzname1', 'frage1',
        '00000000-0006-0001-0001-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0006-0002-0002-000000000001', 'name1', 1, '00000000-0006-0001-0002-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0006-0002-0002-000000000002', 'name2', 2, '00000000-0006-0001-0002-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0006-0002-0002-000000000003', 'name3', 3, '00000000-0006-0001-0002-000000000001');

INSERT INTO referendumvorlage (id, wahlvorschlagID, ordnungszahl, kurzname, frage, referendumvorlagenID)
VALUES ('00000000-0006-0001-0002-000000000002', '00000000-0000-0000-0006-000000000001', 2, 'kurzname2', 'frage2',
        '00000000-0006-0001-0001-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0006-0002-0002-000000000004', 'name1', 1, '00000000-0006-0001-0002-000000000002');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0006-0002-0002-000000000005', 'name2', 2, '00000000-0006-0001-0002-000000000002');


INSERT INTO referendumvorlage (id, wahlvorschlagID, ordnungszahl, kurzname, frage, referendumvorlagenID)
VALUES ('00000000-0006-0001-0002-000000000003', '00000000-0000-0000-0006-000000000001', 3, 'kurzname3', 'frage3',
        '00000000-0006-0001-0001-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0006-0002-0002-000000000006', 'name1', 1, '00000000-0006-0001-0002-000000000003');


-- Wahlvorschläge für die OBW im BWB (wahlbezirkID 9081145c-dc16-4a9b-8255-5c49ddb98c15)
INSERT INTO wahlvorschlaege (id, wahlbezirkID, wahlID, stimmzettelgebietID, wahlvorschlaegelisteID)
VALUES ('00000000-0000-0000-0000-000000000005', '9081145c-dc16-4a9b-8255-5c49ddb98c15',
        'd8d2dd22-cbf6-488e-b9bc-b8c2b0ab31a1', '00000000-0000-0000-0000-000000000001',
        '00000001-0000-0000-0000-000000000002');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0002-000000000013', 1, 'Vorschlag für Referendum', 1,
        '00000000-0000-0000-0000-000000000005');

INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0002-000000000016', 'Stark', 1, 0, 11, 0,
        '00000000-0000-0000-0002-000000000013');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0002-000000000017', 'Lannister', 2, 0, 16, 0,
        '00000000-0000-0000-0002-000000000013');
INSERT INTO kandidat (id, name, listenposition, direktkandidat, tabellenSpalteInNiederschrift, einzelbewerber,
                      wahlvorschlagID)
VALUES ('00000000-0000-0000-0002-000000000018', 'Baratheon', 3, 1, 25, 1,
        '00000000-0000-0000-0002-000000000013');

INSERT INTO referendumvorlagen (id, wahlbezirkID, wahlID, stimmzettelgebietID)
VALUES ('00000000-0000-0000-0002-000000000001', '9081145c-dc16-4a9b-8255-5c49ddb98c15',
        'd8d2dd22-cbf6-488e-b9bc-b8c2b0ab31a1', '00000000-0000-0000-0000-000000000001');

INSERT INTO referendumvorlage (id, wahlvorschlagID, ordnungszahl, kurzname, frage, referendumvorlagenID)
VALUES ('00000000-0000-0002-0002-000000000001', '00000000-0000-0000-0002-000000000013', 1, 'kurzname1', 'frage1',
        '00000000-0000-0000-0002-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000001', 'name1', 1, '00000000-0000-0002-0002-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000002', 'name2', 2, '00000000-0000-0002-0002-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000003', 'name3', 3, '00000000-0000-0002-0002-000000000001');

INSERT INTO referendumvorlage (id, wahlvorschlagID, ordnungszahl, kurzname, frage, referendumvorlagenID)
VALUES ('00000000-0000-0002-0002-000000000002', '00000000-0000-0000-0002-000000000013', 2, 'kurzname2', 'frage2',
        '00000000-0000-0000-0002-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000004', 'name1', 1, '00000000-0000-0002-0002-000000000002');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000005', 'name2', 2, '00000000-0000-0002-0002-000000000002');


INSERT INTO referendumvorlage (id, wahlvorschlagID, ordnungszahl, kurzname, frage, referendumvorlagenID)
VALUES ('00000000-0000-0002-0002-000000000003', '00000000-0000-0000-0002-000000000013', 3, 'kurzname3', 'frage3',
        '00000000-0000-0000-0002-000000000001');
INSERT INTO referendumoption (id, name, position, referendumvorlageid)
VALUES ('00000000-0002-0002-0002-000000000006', 'name1', 1, '00000000-0000-0002-0002-000000000003');
