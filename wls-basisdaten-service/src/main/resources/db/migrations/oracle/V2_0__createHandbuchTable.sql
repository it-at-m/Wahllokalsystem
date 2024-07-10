CREATE TABLE Handbuch
(
    wahltagid      VARCHAR(1024) not null,
    wahlbezirksart VARCHAR(255)  not null,
    handbuch       blob          not null,

    primary key (wahltagid, wahlbezirksart)
);
