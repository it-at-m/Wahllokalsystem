import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";
import type { StimmzettelerfassungStatus } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatus.ts";
import type { Ref } from "vue";

import { computed } from "vue";

import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";

export function useMonitoringViewBeschlussfassungButtonsUtils(
  isTeamStatusListLoading: Ref<boolean>,
  isWorkflowStatusLoading: Ref<boolean>,
  teamstatusList: Ref<StimmzettelerfassungTeamStatusEntry[]>,
  workflowStatus: Ref<StimmzettelerfassungStatus | null>
) {
  const isBeschlussfassungBtnActive = computed(() =>
    teamstatusList.value.every(
      (team) => team.status === StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN
    )
  );

  const isBeschlussfassungContinueBtnDisabled = computed(
    () =>
      !isBeschlussfassungBtnActive.value ||
      workflowStatus.value?.status !==
        StimmzettelerfassungStatusEnum.SteAbgeschlossen ||
      isTeamStatusListLoading.value ||
      isWorkflowStatusLoading.value
  );

  const isBeschlussfassungContinueBtnVisible = computed(
    () =>
      workflowStatus.value?.status ===
      StimmzettelerfassungStatusEnum.SteAbgeschlossen
  );

  const isBeschlussfassungStartenBtnDisabled = computed(
    () =>
      !isBeschlussfassungBtnActive.value ||
      (workflowStatus.value?.status !==
        StimmzettelerfassungStatusEnum.SteBearbeitung &&
        workflowStatus.value !== null) ||
      isTeamStatusListLoading.value ||
      isWorkflowStatusLoading.value
  );

  const isBeschlussfassungStartenBtnVisible = computed(
    () =>
      workflowStatus.value?.status ===
        StimmzettelerfassungStatusEnum.SteBearbeitung ||
      workflowStatus.value === null
  );

  return {
    isBeschlussfassungBtnActive,
    isBeschlussfassungContinueBtnDisabled,
    isBeschlussfassungContinueBtnVisible,
    isBeschlussfassungStartenBtnDisabled,
    isBeschlussfassungStartenBtnVisible,
  };
}
