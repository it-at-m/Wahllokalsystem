--- Stapel A
CREATE INDEX idx_stimmzettel_wahl_wahlbezirk_gueltigkeit_invalidevotes
    ON stimmzettel (wahlid, wahlbezirkid, gueltigkeit, invalidevotes);

CREATE INDEX idx_wahlvorschlag_stimmzettel_selected
    ON wahlvorschlag (
                      stimmzettel_wahlid,
                      stimmzettel_wahlbezirkid,
                      stimmzettel_teamid,
                      stimmzettel_stimmzettelkennung,
                      selected
        );

--- Foreign key: wahlvorschlag -> stimmzettel
CREATE INDEX idx_wahlvorschlag_stimmzettel
    ON wahlvorschlag (
                      stimmzettel_wahlid,
                      stimmzettel_wahlbezirkid,
                      stimmzettel_teamid,
                      stimmzettel_stimmzettelkennung
        );

--- Foreign key: kandidat -> wahlvorschlag
CREATE INDEX idx_kandidat_wahlvorschlag
    ON kandidat (
                 wahlvorschlag_id
        );

--- Stapel B
CREATE INDEX idx_kandidat_discarded_votesbyvoter
    ON kandidat (
                 discarded,
                 votesByVoter
        );

--- Stapel BC

--- Stapel D
CREATE INDEX idx_stimmzettel_wahl_wahlbezirk_gueltigkeit
    ON stimmzettel (wahlid, wahlbezirkid, gueltigkeit);