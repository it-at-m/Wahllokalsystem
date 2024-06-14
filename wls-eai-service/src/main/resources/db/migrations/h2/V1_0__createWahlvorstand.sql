CREATE TABLE wahlvorstand
(
    id           VARCHAR2(36) NOT NULL,
    wahlbezirkID VARCHAR2(36) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE wahlvorstandsmitglied
(
    id                   VARCHAR2(36)  NOT NULL,
    vorname              VARCHAR2(255) NOT NULL,
    nachname             VARCHAR2(255) NOT NULL,
    funktion             VARCHAR2(512) NOT NULL,
    anwesend             boolean       NOT NULL,
    wahlvorstandID       VARCHAR2(36)  NOT NULL,
    anwesenheitUpdatedOn TIMESTAMP,

    FOREIGN KEY (wahlvorstandID) REFERENCES wahlvorstand (id),

    PRIMARY KEY (id)
)