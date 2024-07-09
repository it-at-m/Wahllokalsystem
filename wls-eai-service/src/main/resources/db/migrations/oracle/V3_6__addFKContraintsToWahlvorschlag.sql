ALTER TABLE wahlvorschlaegeliste
    ADD FOREIGN KEY (wahlID) REFERENCES wahl (id);

ALTER TABLE wahlvorschlaege
    ADD FOREIGN KEY (wahlbezirkID) REFERENCES wahlbezirk (id);
ALTER TABLE wahlvorschlaege
    ADD FOREIGN KEY (wahlID) REFERENCES wahl (id);
ALTER TABLE wahlvorschlaege
    ADD FOREIGN KEY (stimmzettelgebietID) REFERENCES stimmzettelgebiet (id);

ALTER TABLE referendumvorlagen
    ADD FOREIGN KEY (wahlbezirkID) REFERENCES wahlbezirk (id);
ALTER TABLE referendumvorlagen
    ADD FOREIGN KEY (wahlID) REFERENCES wahl (id);
ALTER TABLE referendumvorlagen
    ADD FOREIGN KEY (stimmzettelgebietID) REFERENCES stimmzettelgebiet (id);