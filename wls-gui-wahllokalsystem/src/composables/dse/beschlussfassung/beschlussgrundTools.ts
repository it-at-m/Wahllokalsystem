import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";

import { WahlvorstandBeschlussvorschlaegeEnum } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussvorschlaegeEnum.ts";

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

  return {
    createBeschlussgrundWithText,
    getWahlvorstandBeschlussvorschlaege,
  };
}
