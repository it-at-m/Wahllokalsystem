package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Kopfdaten {

  @EmbeddedId @NotNull private BezirkUndWahlID bezirkUndWahlID;

  @NotNull private String gemeinde;

  @Enumerated(EnumType.STRING)
  @NotNull private Stimmzettelgebietsart stimmzettelgebietsart;

  @NotNull private String stimmzettelgebietsnummer;

  @NotNull private String stimmzettelgebietsname;

  @NotNull private String wahlname;

  @NotNull private String wahlbezirknummer;

  @Column(name = "max_stimmen_pro_waehler")
  @Min(1) @Max(999) private Integer maximalErlaubteStimmenProWaehler;
}
