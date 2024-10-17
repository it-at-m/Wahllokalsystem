CREATE TABLE Wlsuser
(
    id               VARCHAR(36),
    username         VARCHAR(255) NOT NULL UNIQUE,
    password         VARCHAR(255),
    email            VARCHAR(255),
    userEnabled      NUMBER(1),
    accountNonLocked NUMBER(1),
    wahltagID        VARCHAR(1000),
    wahltag          TIMESTAMP,
    wahlbezirkID     VARCHAR(1000),
    wahlbezirkNummer VARCHAR(255),
    wahlbezirksArt   VARCHAR(255),
    wbid_wahlnummer  VARCHAR(4000),
    pin              VARCHAR(255),

    PRIMARY KEY (id)
);

CREATE TABLE LoginAttempt
(
    id           VARCHAR(36),
    username     VARCHAR(255) NOT NULL UNIQUE,
    attempts     NUMBER(19, 0),
    lastModified TIMESTAMP,

    PRIMARY KEY (id)
);

CREATE TABLE Authority
(
    id        VARCHAR(36),
    authority VARCHAR(255) UNIQUE,

    PRIMARY KEY (id)
);

CREATE TABLE Permission
(
    id         VARCHAR(36),
    permission VARCHAR(255),

    PRIMARY KEY (id)
);

CREATE TABLE Secusers_Secauthorities
(
    user_oid      VARCHAR(36),
    authority_oid VARCHAR(36),

    FOREIGN KEY (user_oid) REFERENCES Wlsuser (id),
    FOREIGN KEY (authority_oid) REFERENCES Authority (id),

    PRIMARY KEY (authority_oid, user_oid)
);

CREATE TABLE Secauthorities_Secpermissions
(
    authority_oid  VARCHAR(36),
    permission_oid VARCHAR(36),

    FOREIGN KEY (authority_oid) REFERENCES Authority (id),
    FOREIGN KEY (permission_oid) REFERENCES Permission (id),

    PRIMARY KEY (authority_oid, permission_oid)
);
