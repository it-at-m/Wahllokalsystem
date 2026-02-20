package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahltermindaten;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsyncProgress {

  public static final Logger LOG = LoggerFactory.getLogger(AsyncProgress.class);

  private LocalDate forWahltag;
  private String wahlNummer = "";
  private LocalDateTime lastStartTime;
  private LocalDateTime lastFinishTime = null;
  private int wahlvorschlaegeTotal = 0;
  private int wahlvorschlageFinished = 0;
  private String wahlvorschlaegeNext = "";
  private int referendumVorlagenTotal = 0;
  private int referendumVorlagenFinished = 0;
  private String referendumVorlagenNext = "";

  public synchronized void incWahlvorschlaegeFinished() {
    wahlvorschlageFinished++;
    LOG.info("#incWahlvorschlaegeFinished: {}", wahlvorschlageFinished);
  }

  public synchronized void incReferendumVorlagenFinished() {
    referendumVorlagenFinished++;
    LOG.info("#incReferendumVorlagenFinished: {}", referendumVorlagenFinished);
  }

  public void reset(LocalDate forWahltag, String wahlNummer) {
    this.forWahltag = forWahltag;
    this.wahlNummer = wahlNummer;
    lastStartTime = LocalDateTime.now();
    lastFinishTime = null;
    wahlvorschlageFinished = 0;
    wahlvorschlaegeTotal = 0;
    referendumVorlagenFinished = 0;
    referendumVorlagenTotal = 0;
  }
}
