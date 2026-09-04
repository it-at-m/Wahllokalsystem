export const SystemBeschlussgrundReasonEnum = {
  WaehlerwilleIstZweifelsfreiErkennbar:
    "WAEHLERWILLE_IST_ZWEIFELSFREI_ERKENNBAR",
  ZuVieleEinzelstimmenAberImGesamtstimmenlimit:
    "ZU_VIELE_EINZELSTIMMEN_ABER_IM_GESAMTSTIMMENLIMIT",
  KeineReststimmenvergabeMoeglich: "KEINE_RESTSTIMMENVERGABE_MOEGLICH",
  EinzelneStimmenUngueltig: "EINZELNE_STIMMEN_UNGUELTIG",
  WaehlerwilleNichtZweifelsfreiErkennbar:
    "WAEHLERWILLE_NICHT_ZWEIFELSFREI_ERKENNBAR",
  ZuVieleEinzelstimmenOderListenkreuze:
    "ZU_VIELE_EINZELSTIMMEN_ODER_LISTENKREUZE",
  StimmzettelMitBesonderemZusatz: "STIMMZETTEL_MIT_BESONDEREM_ZUSATZ",
  NichtAmtlicherStimmzettel: "NICHT_AMTLICHER_STIMMZETTEL",
  BriefwahlMehrereStimmzettelInUmschlagIdentischGekennzeichnet:
    "BRIEFWAHL_MEHRERE_STIMMZETTEL_IN_UMSCHLAG_IDENTISCH_GEKENNZEICHNET",
  BriefwahlMehrereStimmzettelInUmschlagUnterschiedlichGekennzeichnet:
    "BRIEFWAHL_MEHRERE_STIMMZETTEL_IN_UMSCHLAG_UNTERSCHIEDLICH_GEKENNZEICHNET",
  BriefwahlMehrereStimmzettelInUmschlagLeerUndGekennzeichnet:
    "BRIEFWAHL_MEHRERE_STIMMZETTEL_IN_UMSCHLAG_LEER_UND_GEKENNZEICHNET",
} as const;
export type SystemBeschlussgrundReasonEnum =
  (typeof SystemBeschlussgrundReasonEnum)[keyof typeof SystemBeschlussgrundReasonEnum];
