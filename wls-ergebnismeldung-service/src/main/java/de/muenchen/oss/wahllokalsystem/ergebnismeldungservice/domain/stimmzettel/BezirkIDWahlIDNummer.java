package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettel;

import lombok.Data;

@Data
public class BezirkIDWahlIDNummer {
  private String wahlbezirkID;
  private String wahlID;
  private long stimmzettelNummer;
}
