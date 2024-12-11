CREATE TABLE Status
(
    wahlid                           VARCHAR(1024) NOT NULL,
    wahlbezirkid                     VARCHAR(1024) NOT NULL,

    schnellmeldungValidierungsstatus VARCHAR(255)  NOT NULL,
    schnellmeldungGedruckt           BOOLEAN       NOT NULL,
    schnellmeldungUebermittelt       BOOLEAN,
    schnellmeldungSendeuhrzeit       TIMESTAMP,

    niederschriftValidierungsstatus  VARCHAR(255)  NOT NULL,
    niederschriftGedruckt            BOOLEAN       NOT NULL,
    niederschriftUebermittelt        BOOLEAN,
    niederschriftSendeuhrzeit        TIMESTAMP,

    PRIMARY KEY (wahlid, wahlbezirkid)
);