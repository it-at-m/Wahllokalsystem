package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Ereignisse {

  @Id
  @NotNull @Size(max = 1024) @ToString.Include
  private String wahlbezirkID;

  @NotNull @ToString.Include private boolean keineVorfaelle;

  @NotNull @ToString.Include private boolean keineVorkommnisse;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "Ereignis", joinColumns = @JoinColumn(name = "ereignisse_wahlbezirkID"))
  @NotNull private Set<Ereignis> ereignisse = new LinkedHashSet<>();
}
