import type { ElectionWorkflowState } from "@/types/navigation/ElectionWorkflowState.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

const workflowStoreId = "workflow";

export const useWorkflowStore = defineStore(workflowStoreId, () => {
  const isWahlvorstandErfasst = ref(false);
  const isWahlumgebungErfasst = ref(false);

  const electionWorkflowsStates = ref<ElectionWorkflowState[]>([]);

  function getStatus(
    wahlID: string,
    wahlbezirkID: string
  ): ElectionWorkflowState | undefined {
    return electionWorkflowsStates.value.find(
      (statusEntry) =>
        statusEntry.bezirkUndWahlID.wahlID === wahlID &&
        statusEntry.bezirkUndWahlID.wahlbezirkID === wahlbezirkID
    );
  }

  function initStatus(wahlID: string, wahlbezirkID: string) {
    electionWorkflowsStates.value.push({
      bezirkUndWahlID: {
        wahlID,
        wahlbezirkID,
      },
      isSchnellmeldungDone: false,
      isNiederschriftDone: false,
      stepsDone: {},
    });
  }

  function isElectionFinished(wahlID: string, wahlbezirkID: string): boolean {
    const electionStatus = getStatus(wahlID, wahlbezirkID);
    return electionStatus ? electionStatus.isNiederschriftDone : false;
  }

  function isStepDone(
    wahlID: string,
    wahlbezirkID: string,
    step: string
  ): boolean {
    return getStatus(wahlID, wahlbezirkID)?.stepsDone[step] ?? false;
  }

  function setStepDone(
    wahlID: string,
    wahlbezirkID: string,
    step: string,
    isDone = true
  ) {
    const status = getStatus(wahlID, wahlbezirkID);
    if (status) {
      status.stepsDone[step] = isDone;
    }
  }

  return {
    electionWorkflowsStates,
    isWahlvorstandErfasst,
    isWahlumgebungErfasst,
    getStatus,
    initStatus,
    isElectionFinished,
    isStepDone,
    setStepDone,
  };
});
