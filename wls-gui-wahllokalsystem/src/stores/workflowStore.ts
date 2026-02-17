import type { ElectionWorkflowState } from "@/types/navigation/ElectionWorkflowState.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

const workflowStoreId = "workflow";

export const useWorkflowStore = defineStore(workflowStoreId, () => {
  const isWahlvorstandErfasst = ref(false);
  const isWahlumgebungErfasst = ref(false);
  const isTestseiteGedruckt = ref(false);
  const isWahleroeffnungErfasst = ref(false);

  const electionWorkflowsStates = ref<ElectionWorkflowState[]>([]);

  function getElectionWorkflowState(
    wahlID: string,
    wahlbezirkID: string
  ): ElectionWorkflowState | undefined {
    return electionWorkflowsStates.value.find(
      (statusEntry) =>
        statusEntry.bezirkUndWahlID.wahlID === wahlID &&
        statusEntry.bezirkUndWahlID.wahlbezirkID === wahlbezirkID
    );
  }

  function initElectionWorkflowState(wahlID: string, wahlbezirkID: string) {
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
    const electionStatus = getElectionWorkflowState(wahlID, wahlbezirkID);
    return electionStatus ? electionStatus.isNiederschriftDone : false;
  }

  function isStepDone(
    wahlID: string,
    wahlbezirkID: string,
    step: string
  ): boolean {
    return (
      getElectionWorkflowState(wahlID, wahlbezirkID)?.stepsDone[step] ?? false
    );
  }

  function setStepDone(
    wahlID: string,
    wahlbezirkID: string,
    step: string,
    isDone = true
  ) {
    const status = getElectionWorkflowState(wahlID, wahlbezirkID);
    if (status) {
      status.stepsDone[step] = isDone;
    }
  }

  return {
    electionWorkflowsStates,
    isWahlvorstandErfasst,
    isWahlumgebungErfasst,
    isTestseiteGedruckt,
    isWahleroeffnungErfasst,
    getElectionWorkflowState,
    initElectionWorkflowState,
    isElectionFinished,
    isStepDone,
    setStepDone,
  };
});
