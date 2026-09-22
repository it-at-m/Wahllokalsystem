package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status;

public enum ErfassungStatusModel {
  STE_BEARBEITUNG,
  STE_ABGESCHLOSSEN,
  BE_ABGESCHLOSSEN;

  public boolean isStimmzettelerfassungAbgeschlossen() {
    return STE_ABGESCHLOSSEN.equals(this) || BE_ABGESCHLOSSEN.equals(this);
  }

  public boolean isBeschlussfassungAbgeschlossen() {
    return BE_ABGESCHLOSSEN.equals(this);
  }
}
