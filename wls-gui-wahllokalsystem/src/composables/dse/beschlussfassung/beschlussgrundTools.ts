import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";

import { useSystemBeschlussgrundReasonEnumTools } from "@/composables/dse/beschlussfassung/systemBeschlussgrundReasonEnumTools.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { WahlvorstandBeschlussvorschlaegeEnum } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussvorschlaegeEnum.ts";

const { mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText } =
  useSystemBeschlussgrundReasonEnumTools();

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

  function getBeschlussgrundEnumValueAsString(
    grund: string | SystemBeschlussgrundReasonEnum
  ): string {
    const systemGruende = Object.values(
      SystemBeschlussgrundReasonEnum
    ) as string[];

    const valueToCheck = String(grund);

    if (systemGruende.includes(valueToCheck)) {
      return mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText(
        valueToCheck as SystemBeschlussgrundReasonEnum
      );
    }

    return valueToCheck;
  }

  return {
    createBeschlussgrundWithText,
    getWahlvorstandBeschlussvorschlaege,
    sortWahlvorstandBeschlussgruende,
    sortSystemBeschlussgruende,
    getBeschlussgrundEnumValueAsString,
  };
}
