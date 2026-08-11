import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";
import type { Ref } from "vue";

import { computed } from "vue";

import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

export function useStimmzettelErfassungViewButtonStateUtils(
  teamStatus: Ref<StimmzettelerfassungTeamStatus | null>
) {
  //ButtonActiveStates
  const startenBtnActive = computed(
    () =>
      teamStatus.value?.status ==
        StimmzettelerfassungTeamStatusEnum.REGISTRIERT ||
      teamStatus.value?.status ==
        StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN
  );
  const beendenBtnActive = computed(
    () =>
      teamStatus.value?.status ==
      StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG
  );

  //ButtonDisabledStates
  const startenBtnIsDisabled = computed(
    () =>
      teamStatus.value?.status ==
      StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN
  );
  const beendenBtnIsDisabled = computed(
    () =>
      teamStatus.value?.status ==
      StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN
  );
  const unterbrechenBtnIsDisabled = computed(
    () =>
      teamStatus.value?.status !=
      StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG
  );

  return {
    beendenBtnActive,
    beendenBtnIsDisabled,
    startenBtnActive,
    startenBtnIsDisabled,
    unterbrechenBtnIsDisabled,
  };
}
