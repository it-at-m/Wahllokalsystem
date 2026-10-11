import type { BeschlussfassungDialogDetails } from "@/types/dse/beschlussfassung/BeschlussfassungDialogDetails.ts";

import { useBeschlussgrundTools } from "@/composables/dse/beschlussfassung/beschlussgrundTools.ts";

export function useBeschlussfassungDialogDetailsTools() {
  const { getBeschlussgrundEnumValueAsString } = useBeschlussgrundTools();

  function createEmptyBeschlussfassungDialogDetails(): BeschlussfassungDialogDetails {
    return {
      isStimmzettelGueltig: null,
      beschlussgruende: [],
      andererGrund: "",
      andererGrundChecked: false,
      beschlussText: "",
    };
  }

  function mergeGruendeAndReturnBeschlusstext(
    beschlussDetails: BeschlussfassungDialogDetails
  ) {
    const selectedGruende = beschlussDetails.beschlussgruende
      .filter((option) => option.selected)
      .map((option) => getBeschlussgrundEnumValueAsString(option.grund))
      .join(", ");
    const andereGruende = beschlussDetails.andererGrundChecked
      ? beschlussDetails.andererGrund
      : "";

    return [selectedGruende, andereGruende].filter(Boolean).join(", ");
  }

  return {
    createEmptyBeschlussfassungDialogDetails,
    mergeGruendeAndReturnBeschlusstext,
  };
}
