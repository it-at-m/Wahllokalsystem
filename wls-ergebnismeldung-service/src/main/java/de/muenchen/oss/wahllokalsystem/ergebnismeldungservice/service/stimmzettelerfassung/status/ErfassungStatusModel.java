package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status;

public enum ErfassungStatusModel {
  STE_BEARBEITUNG,
  STE_ABGESCHLOSSEN,
  BE_ABGESCHLOSSEN;

  public static boolean isStimmzettelerfassungAbgeschlossen(ErfassungStatusModel status) {
    if (status == null) {
      return false;
    }
    return status == STE_ABGESCHLOSSEN || status == BE_ABGESCHLOSSEN;
  }

  public static boolean isBeschlussfassungAbgeschlossen(ErfassungStatusModel status) {
    if (status == null) {
      return false;
    }
    return status == BE_ABGESCHLOSSEN;
  }
}
