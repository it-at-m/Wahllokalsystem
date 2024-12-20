CREATE TABLE Begruendung
(
    wahlbezirkid        VARCHAR(1024) NOT NULL,
    wahlid              VARCHAR(1024) NOT NULL,
    stapelart           VARCHAR(1024) NOT NULL,
    grund1              VARCHAR(1024) NOT NULL,
    grund2              VARCHAR(1024) NOT NULL,
    nachzaehlung        BOOLEAN NOT NULL,
    unstimmigkeiten     BOOLEAN NOT NULL,
    PRIMARY KEY (wahlbezirkid, wahlid, stapelart)
);