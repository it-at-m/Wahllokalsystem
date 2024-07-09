-- Referendum (VE) am Wahltermin der auch die EU-Wahl hat
INSERT INTO wahlvorschlaege (id, wahlbezirkID, wahlID, stimmzettelgebietID, wahlvorschlaegelisteID)
VALUES ('00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000005',
        '00000001-0000-0000-0000-000000000002');

INSERT INTO wahlvorschlag (id, ordnungszahl, kurzname, erhaeltStimmen, wahlvorschlaegeID)
VALUES ('00000000-0000-0000-0001-000000000013', 1, 'Vorschlag für Refendum', true,
        '00000000-0000-0000-0000-000000000005');


INSERT INTO referendumvorlagen (id, wahlbezirkID, wahlID, stimmzettelgebietID)
VALUES ('00000000-0000-0000-0001-000000000001', '00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000005');

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