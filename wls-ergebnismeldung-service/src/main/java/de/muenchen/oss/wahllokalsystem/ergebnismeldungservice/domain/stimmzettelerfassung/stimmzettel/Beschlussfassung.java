package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class Beschlussfassung {

  @NotNull private int pro;

  @NotNull private int contra;

  @NotNull private String text;
}
