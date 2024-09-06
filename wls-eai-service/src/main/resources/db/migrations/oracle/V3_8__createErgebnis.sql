CREATE TABLE ERGEBNIS (
    id                         VARCHAR2(36) NOT NULL,
    stimmenart                 VARCHAR2(36) NOT NULL,
    wahlvorschlagsordnungszahl NUMBER NOT NULL,
    ergebnis                   VARCHAR2(36) NOT NULL,
    kandidat                   VARCHAR2(36) NOT NULL,
    wahlvorschlagID            VARCHAR2(36) NOT NULL,

    PRIMARY KEY (id)
)