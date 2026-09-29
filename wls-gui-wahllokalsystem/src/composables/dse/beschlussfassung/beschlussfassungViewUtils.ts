import { computed, onActivated } from "vue";

import { useAllStimmzettelOfWahlbezirkState } from "@/composables/dse/allStimmzettelOfWahlbezirkState.ts";
import { useStimmzettelTools } from "@/composables/dse/stimmzettelerfassung/stimmzettelTools.ts";
import { useStimmzettelerfassungStatusState } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusState.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";

export function useBeschlussfassungViewUtils(
  wahlID: string,
  wahlbezirkID: string
) {
  const { workflowStatus } = useStimmzettelerfassungStatusState(
    wahlID,
    wahlbezirkID
  );
  const { isLoading, stimmzettelOfWahlbezirk, loadStimmzettelOfWahlbezirk } =
    useAllStimmzettelOfWahlbezirkState(wahlID, wahlbezirkID);
  const { isBeschlussRequired } = useStimmzettelTools();
  const { isElectionFinished } = useWorkflowStore();

  const stimmzettelForBeschlussfassung = computed(() =>
    stimmzettelOfWahlbezirk.value.filter(isBeschlussRequired)
  );

  const completedStimmzettelForBeschlussfassung = computed(() =>
    stimmzettelForBeschlussfassung.value.filter(
      (stimmzettel) =>
        stimmzettel.gueltigkeit !==
        StimmzettelGueltigkeitEnum.BeschlussAusstehend
    )
  );

  const isBeschlussfassungBeendenButtonDisabled = computed(() => {
    if (isLoading.value) return true;
    else {
      return (
        workflowStatus.value?.status ===
          StimmzettelerfassungStatusEnum.BeAbgeschlossen ||
        stimmzettelForBeschlussfassung.value.length !==
          completedStimmzettelForBeschlussfassung.value.length ||
        isElectionFinished(wahlID, wahlbezirkID)
      );
    }
  });

  const isBeschlussBearbeitenDisabled = computed(() =>
    isElectionFinished(wahlID, wahlbezirkID)
  );

  onActivated(async () => {
    await Promise.allSettled([loadStimmzettelOfWahlbezirk()]);
  });

  return {
    stimmzettelForBeschlussfassung,
    completedStimmzettelForBeschlussfassung,
    isStimmzettelForBeschlussLoading: isLoading,
    isBeschlussfassungBeendenButtonDisabled,
    isBeschlussBearbeitenDisabled,
  };
}
