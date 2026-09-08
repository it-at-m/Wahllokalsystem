export const WahlvorstandBeschlussvorschlaegeEnum = {
  WaehlerwilleIstZweifelsfreiErkennbar:
    "WAEHLERWILLE_IST_ZWEIFELSFREI_ERKENNBAR",
  WaehlerwilleNichtZweifelsfreiErkennbar:
    "Wählerwille ist nicht zweifelsfrei erkennbar",
  StimmzettelMitBesonderemZusatz:
    "Stimmzettel ist mit einem besonderen Merkmal, Zusatz oder Vorbehalt versehen",
  NichtAmtlicherStimmzettel:
    "Stimmzettel ist nicht amtlich hergestellt (zum Beispiel von einer anderen Gemeinde)",
  BriefwahlMehrereStimmzettelInUmschlagIdentischGekennzeichnet:
    "Mehrere gleich gekennzeichnete Stimmzettel im Umschlag",
  BriefwahlMehrereStimmzettelInUmschlagUnterschiedlichGekennzeichnet:
    "Mehrere unterschiedlich gekennzeichnete Stimmzettel im Umschlag",
  BriefwahlMehrereStimmzettelInUmschlagLeerUndGekennzeichnet:
    "Mehrere Stimmzettel im Umschlag, einer gekennzeichnet, die anderen leer",
} as const;
export type WahlvorstandBeschlussvorschlaegeEnum =
  (typeof WahlvorstandBeschlussvorschlaegeEnum)[keyof typeof WahlvorstandBeschlussvorschlaegeEnum];
