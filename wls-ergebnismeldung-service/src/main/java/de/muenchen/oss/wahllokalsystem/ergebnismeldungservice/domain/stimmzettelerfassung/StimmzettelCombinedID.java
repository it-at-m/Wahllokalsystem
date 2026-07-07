package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung;

import lombok.Data;

@Data
public class StimmzettelCombinedID {
    private String wahlbezirkID;
    private String wahlID;
    private String teamID;
}
