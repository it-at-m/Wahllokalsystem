import type { StimmzettelerfassungStatus } from "@/types/dse/StimmzettelerfassungStatus.ts";
import type { Ref } from "vue";

import { computed } from "vue";

import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

export function useMonitoringViewButtonStateUtils(
  teamStatus: StimmzettelerfassungTeamStatusEnum | null,
  workflowStatus: Ref<StimmzettelerfassungStatus | null>
) {
  //ButtonDisabledStates
  const wiederOeffnenButtonIsDisabled = computed(
    () =>
      !(teamStatus === StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN) ||
      workflowStatus.value?.status ===
        StimmzettelerfassungStatusEnum.BeAbgeschlossen
  );

  return {
    wiederOeffnenButtonIsDisabled,
  };
}
