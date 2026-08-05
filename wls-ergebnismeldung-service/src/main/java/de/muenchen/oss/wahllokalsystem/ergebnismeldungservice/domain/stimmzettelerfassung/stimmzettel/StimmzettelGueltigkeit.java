package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

public enum StimmzettelGueltigkeit {
  VALID,
  INVALID,
  BESCHLUSS_AUSSTEHEND,
  BWB_PSEUDO_STIMMZETTEL_LEERER_UMSCHLAG,
  LEER
}
