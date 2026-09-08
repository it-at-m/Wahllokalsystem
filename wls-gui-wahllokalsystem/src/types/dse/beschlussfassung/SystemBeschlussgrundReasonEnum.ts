export const SystemBeschlussgrundReasonEnum = {
  ZuVieleEinzelstimmenAberImGesamtstimmenlimit:
    "ZU_VIELE_EINZELSTIMMEN_ABER_IM_GESAMTSTIMMENLIMIT",
  KeineReststimmenvergabeMoeglich: "KEINE_RESTSTIMMENVERGABE_MOEGLICH",
  EinzelneStimmenUngueltig: "EINZELNE_STIMMEN_UNGUELTIG",
  ZuVieleEinzelstimmenOderListenkreuze:
    "ZU_VIELE_EINZELSTIMMEN_ODER_LISTENKREUZE",
} as const;
export type SystemBeschlussgrundReasonEnum =
  (typeof SystemBeschlussgrundReasonEnum)[keyof typeof SystemBeschlussgrundReasonEnum];
