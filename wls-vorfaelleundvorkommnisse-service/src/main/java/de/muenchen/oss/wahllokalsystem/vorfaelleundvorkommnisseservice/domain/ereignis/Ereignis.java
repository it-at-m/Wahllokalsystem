package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ereignis {

  @Size(max = 1024) @ToString.Include
  private String beschreibung;

  @ToString.Include private LocalDateTime uhrzeit;

  @Enumerated(EnumType.STRING)
  @NotNull @ToString.Include
  private Ereignisart ereignisart;
}
