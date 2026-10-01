import type { BeschlussfassungDialogDetails } from "@/types/dse/beschlussfassung/BeschlussfassungDialogDetails.ts";

export function useBeschlussfassungDialogDeailsTools() {
  function createEmptyBeschlussfassungDialogDetails(): BeschlussfassungDialogDetails {
    return {
      isStimmzettelGueltig: null,
      beschlussgruende: [],
      andererGrund: "",
      andererGrundChecked: false,
      beschlussText: "",
    };
  }

  return { createEmptyBeschlussfassungDialogDetails };
}
