import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";

import { storeToRefs } from "pinia";

import { useSystemBeschlussgrundReasonEnumFormatter } from "@/composables/dse/beschlussfassung/useSystemBeschlussgrundReasonEnumFormatter.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { WahlvorstandBeschlussvorschlaegeEnum } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussvorschlaegeEnum.ts";

const { mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText } =
  useSystemBeschlussgrundReasonEnumFormatter();

export function useBeschlussgrundTools() {
  const commonWahlvorstandBeschlussvorschlaege = [
    WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
    WahlvorstandBeschlussvorschlaegeEnum.StimmzettelMitBesonderemZusatz,
    WahlvorstandBeschlussvorschlaegeEnum.NichtAmtlicherStimmzettel,
  ];
  const bwbWahlvorstandBeschlussvorschlaege = [
    WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagIdentischGekennzeichnet,
    WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagLeerUndGekennzeichnet,
    WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagUnterschiedlichGekennzeichnet,
  ];

  const beschlussGruende = {
    common: {
      gueltig: [
        WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleIstZweifelsfreiErkennbar,
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit,
        SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich,
        SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig,
      ],
      ungueltig: [
        WahlvorstandBeschlussvorschlaegeEnum.WaehlerwilleNichtZweifelsfreiErkennbar,
        SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
        WahlvorstandBeschlussvorschlaegeEnum.StimmzettelMitBesonderemZusatz,
        WahlvorstandBeschlussvorschlaegeEnum.NichtAmtlicherStimmzettel,
      ],
    },
    bwb: {
      gueltig: [
        WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagIdentischGekennzeichnet,
        WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagLeerUndGekennzeichnet,
      ],
      ungueltig: [
        WahlvorstandBeschlussvorschlaegeEnum.BriefwahlMehrereStimmzettelInUmschlagUnterschiedlichGekennzeichnet,
      ],
    },
  };

  const allBeschlussGruendeUngueltig = new Set<string>([
    ...beschlussGruende.common.ungueltig,
    ...beschlussGruende.bwb.ungueltig,
  ]);

  function getWahlvorstandBeschlussvorschlaege(isBWB: boolean) {
    if (isBWB) {
      return [
        ...commonWahlvorstandBeschlussvorschlaege,
        ...bwbWahlvorstandBeschlussvorschlaege,
      ];
    } else {
      return [...commonWahlvorstandBeschlussvorschlaege];
    }
  }

  function createBeschlussgrundWithText(
    text: string
  ): WahlvorstandBeschlussgrund {
    return {
      text,
    };
  }

  function sortWahlvorstandBeschlussgruende(
    gruende: WahlvorstandBeschlussgrund[]
  ) {
    return gruende.slice().sort((x, y) => x.text.localeCompare(y.text));
  }

  function sortSystemBeschlussgruende(gruende: SystemBeschlussgrund[]) {
    return gruende
      .slice()
      .sort((x, y) => String(x.reason).localeCompare(String(y.reason)));
  }

  function getBeschlussgrundEnumValueAsString(grund: string): string {
    const systemGrund = Object.values(SystemBeschlussgrundReasonEnum).find(
      (reason) => reason === grund
    );

    return systemGrund
      ? mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText(systemGrund)
      : grund;
  }

  function getBeschlussGruendeBasedOnGueltigkeit(isGueltig: boolean) {
    const { isBWB } = storeToRefs(useUserStore());

    return isGueltig
      ? isBWB.value
        ? [...beschlussGruende.common.gueltig, ...beschlussGruende.bwb.gueltig]
        : beschlussGruende.common.gueltig
      : isBWB.value
        ? [
            ...beschlussGruende.common.ungueltig,
            ...beschlussGruende.bwb.ungueltig,
          ]
        : beschlussGruende.common.ungueltig;
  }

  return {
    allBeschlussGruendeUngueltig,
    createBeschlussgrundWithText,
    getWahlvorstandBeschlussvorschlaege,
    sortWahlvorstandBeschlussgruende,
    sortSystemBeschlussgruende,
    getBeschlussgrundEnumValueAsString,
    getBeschlussGruendeBasedOnGueltigkeit,
  };
}
