export const ZurueckweisungsgrundEnum = {
  Zugelassen: "ZUGELASSEN",
  ScheinUngueltig: "SCHEIN_UNGUELTIG",
  KeinOriginalSchein: "KEIN_ORIGINAL_SCHEIN",
  UnterschriftFehlt: "UNTERSCHRIFT_FEHLT",
  UmschlagFehlt: "UMSCHLAG_FEHLT",
  WahlbriefUndUmschlagOffen: "WAHLBRIEF_UND_UMSCHLAG_OFFEN",
  ScheineUngleichUmschlaege: "SCHEINE_UNGLEICH_UMSCHLAEGE",
  UmschlagNichtAmtlich: "UMSCHLAG_NICHT_AMTLICH",
  UmschlagGefaehrdetWahlgeheimnis: "UMSCHLAG_GEFAEHRDET_WAHLGEHEIMNIS",
  GegenstandImUmschlag: "GEGENSTAND_IM_UMSCHLAG",
  NichtWahlberechtigt: "NICHT_WAHLBERECHTIGT",
} as const;

export type ZurueckweisungsgrundEnum =
  (typeof ZurueckweisungsgrundEnum)[keyof typeof ZurueckweisungsgrundEnum];
