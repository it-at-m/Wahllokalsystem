CREATE TABLE message (
    oid varchar(36),
    wahlbezirkid varchar(1024) not null,
    nachricht varchar(1024) not null,
    empfangszeit TIMESTAMP not null,
    primary key (oid)
);