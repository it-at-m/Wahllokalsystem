import type { StimmzettelerfassungStatus } from "@/types/dse/StimmzettelerfassungStatus.ts";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";
import type { Ref } from "vue";

import { computed } from "vue";

import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

export function useMonitoringViewButtonStateUtils(
  teamStatus: Ref<StimmzettelerfassungTeamStatus | null>,
  workflowStatus: Ref<StimmzettelerfassungStatus | null>
) {
  //ButtonDisabledStates
  const wiederOeffnenButtonIsDisabled = computed(
    () =>
      !(
        teamStatus.value?.status ===
        StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN
      ) ||
      workflowStatus.value?.status ===
        StimmzettelerfassungStatusEnum.SteAbgeschlossen
  );

  return {
    wiederOeffnenButtonIsDisabled,
  };
}
