ALTER TABLE wahlvorstand
    ADD FOREIGN KEY (wahlbezirkID) REFERENCES wahlbezirk (id)