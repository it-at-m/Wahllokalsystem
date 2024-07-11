CREATE TABLE Handbuch
(
    wahltagID      VARCHAR(1024) not null,
    wahlbezirksart VARCHAR(255)  not null,
    handbuch       blob          not null,

    primary key (wahltagID, wahlbezirksart)
);
