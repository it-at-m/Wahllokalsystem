CREATE TABLE Status
(
    wahlid                           VARCHAR(1024) NOT NULL,
    wahlbezirkid                     VARCHAR(1024) NOT NULL,

    schnellmeldungValidierungsstatus VARCHAR(255)  NOT NULL,
    schnellmeldungGedruckt           NUMBER(1)     NOT NULL,
    schnellmeldungUebermittelt       NUMBER(1),
    schnellmeldungSendeuhrzeit       TIMESTAMP,

    niederschriftValidierungsstatus  VARCHAR(255)  NOT NULL,
    niederschriftGedruckt            NUMBER(1)     NOT NULL,
    niederschriftUebermittelt        NUMBER(1),
    niederschriftSendeuhrzeit        TIMESTAMP,

    PRIMARY KEY (wahlid, wahlbezirkid)
);