CREATE TABLE Handbuch
(
    wahltagid      varchar(1024) not null,
    wahlbezirksart varchar(255)  not null,
    handbuch       blob          not null,
    
    primary key (wahltagid, wahlbezirksart)
);
