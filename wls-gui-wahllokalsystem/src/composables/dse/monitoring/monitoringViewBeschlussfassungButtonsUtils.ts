import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";
import type { StimmzettelerfassungStatus } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatus.ts";
import type { Ref } from "vue";

import { computed } from "vue";

import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";

export function useMonitoringViewBeschlussfassungButtonsUtils(
  isTeamStatusListLoading: Ref<boolean>,
  isWorkflowStatusLoading: Ref<boolean>,
  teamstatusList: Ref<StimmzettelerfassungTeamStatusEntry[]>,
  workflowStatus: Ref<StimmzettelerfassungStatus | null>,
  wahlID: string,
  wahlbezirkID: string
) {
  const { isElectionFinished } = useWorkflowStore();

  const isBeschlussfassungBtnActive = computed(
    () =>
      teamstatusList.value.every(
        (team) =>
          team.status === StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN
      ) && teamstatusList.value.length > 0
  );

  const isMoveOnToBeschlussfassungDisabled = computed(() => {
    return (
      !isBeschlussfassungBtnActive.value ||
      isElectionFinished(wahlID, wahlbezirkID) ||
      workflowStatus.value?.status ===
        StimmzettelerfassungStatusEnum.BeAbgeschlossen ||
      isTeamStatusListLoading.value ||
      isWorkflowStatusLoading.value
    );
  });

  const isBeschlussfassungContinueBtnVisible = computed(
    () =>
      workflowStatus.value?.status ===
        StimmzettelerfassungStatusEnum.SteAbgeschlossen ||
      workflowStatus.value?.status ===
        StimmzettelerfassungStatusEnum.BeAbgeschlossen
  );

  const isBeschlussfassungStartenBtnVisible = computed(
    () =>
      workflowStatus.value?.status ===
        StimmzettelerfassungStatusEnum.SteBearbeitung ||
      workflowStatus.value === null
  );

  return {
    isBeschlussfassungBtnActive,
    isBeschlussfassungContinueBtnVisible,
    isBeschlussfassungStartenBtnVisible,
    isMoveOnToBeschlussfassungDisabled,
  };
}
