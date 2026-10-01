import type { BeschlussfassungDialogDetails } from "@/types/dse/beschlussfassung/BeschlussfassungDialogDetails.ts";

export function useBeschlussfassungDialogDeailsTools() {
  function createEmptyBeschlussfassungDialogDetails(): BeschlussfassungDialogDetails {
    return {
      isGueltig: null,
      beschlussgruende: [],
      andererGrund: "",
      andererGrundChecked: false,
      beschlussText: "",
    };
  }

  return { createEmptyBeschlussfassungDialogDetails };
}
