import type { ElectionWorkflowState } from "@/types/navigation/ElectionWorkflowState.ts";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

const workflowStoreId = "workflow";

export const useWorkflowStore = defineStore(workflowStoreId, () => {
  // bwb + uwb
  const isWahlvorstandErfasst = ref(false);
  const isWahlumgebungErfasst = ref(false);
  const isTestseiteGedruckt = ref(false);
  const isWahleroeffnungErfasst = ref(false);
  // bwb specific
  const isWahlbriefeErfassenErfasst = ref(false);
  const isWahlbriefeZulassenErfasst = ref(false);
  const isAnzahlWahlscheineErfasst = ref(false);
  const isWahlbriefzulassungErfasst = computed(
    () =>
      isWahleroeffnungErfasst.value &&
      isWahlumgebungErfasst.value &&
      isWahlbriefeErfassenErfasst.value &&
      isWahlbriefeZulassenErfasst.value
  );
  // uwb specific
  const isWaehlerverzeichnisErfasst = ref(false);
  const isStimmabgabeErfasst = ref(false);
  const isStimmabgabevermerkeErfasst = ref(false);
  const isWahlhandlungErfasst = computed(
    () =>
      isWahlumgebungErfasst.value &&
      isWaehlerverzeichnisErfasst.value &&
      isWahleroeffnungErfasst.value &&
      isStimmabgabeErfasst.value
  );

  const isMbwStapelAErfasst = ref(false);
  const isMbwStapelBErfasst = ref(false);

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
    isWahlbriefeErfassenErfasst,
    isWahlbriefeZulassenErfasst,
    isAnzahlWahlscheineErfasst,
    isWaehlerverzeichnisErfasst,
    isStimmabgabeErfasst,
    isStimmabgabevermerkeErfasst,
    isWahlbriefzulassungErfasst,
    isWahlhandlungErfasst,
    isMbwStapelAErfasst,
    isMbwStapelBErfasst,
    getElectionWorkflowState,
    initElectionWorkflowState,
    isElectionFinished,
    isStepDone,
    setStepDone,
  };
});
