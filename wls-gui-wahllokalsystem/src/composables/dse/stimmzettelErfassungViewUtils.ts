import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";
import type { Ref } from "vue";

import { computed, onActivated, readonly, ref } from "vue";

import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatusService.ts";
import { useStimmzettelErfassungViewButtonStateUtils } from "@/composables/dse/stimmzettelErfassungViewButtonStateUtils.ts";
import { useStimmzettelService } from "@/composables/dse/stimmzettelService.ts";
import { useStimmzettelUtils } from "@/composables/dse/stimmzettelUtils.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

const erfassungTeamStatusService = useStimmzettelerfassungTeamStatusService();

const { getStimmzettel, saveStimmzettel } = useStimmzettelService();
const { getEmptyStimmzettelWithStimmzettelkennung } = useStimmzettelUtils();

export function useStimmzettelErfassungViewUtils(
  wahlID: string,
  wahlbezirkID: string,
  teamID: string
) {
  const teamStatus = ref<StimmzettelerfassungTeamStatus | null>(null);
  const isStatusLoading = ref(false);
  const isStimmzettelLoading = ref(false);
  const savedStimmzettel: Ref<Stimmzettel[]> = ref([]);
  const activeStimmzettel: Ref<Stimmzettel | null> = ref(null);

  //DialogVisibilityState
  const isKennungsDialogVisible = ref(false);
  const isErfassungsDialogVisible = ref(false);

  const buttonUtils = useStimmzettelErfassungViewButtonStateUtils(teamStatus);

  //Hooks
  onActivated(async () => {
    await Promise.allSettled([_loadTeamStatus(), _loadStimmzettel()]);
  });

  //Public functions
  function startNewEmptyStimmzettelWithStimmzettelkennung(
    stimmzettelkennung: number
  ) {
    activeStimmzettel.value =
      getEmptyStimmzettelWithStimmzettelkennung(stimmzettelkennung);
  }

  async function sendStatusInBearbeitung() {
    await _postTeamStatus(StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG);
  }

  async function sendStatusUnterbrochen() {
    await _postTeamStatus(StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN);
  }

  async function saveNewStimmzettel(stimmzettel: Stimmzettel) {
    const newStimmzettelCollectionToSave = [
      ...savedStimmzettel.value,
      stimmzettel,
    ];
    await saveStimmzettel(
      wahlID,
      wahlbezirkID,
      teamID,
      newStimmzettelCollectionToSave
    );
    savedStimmzettel.value = newStimmzettelCollectionToSave;
  }

  //private functions
  async function _loadTeamStatus() {
    isStatusLoading.value = true;
    try {
      const loaded = await erfassungTeamStatusService.loadErfassungTeamStatus(
        wahlID,
        wahlbezirkID,
        teamID,
        false
      );
      if (loaded) {
        teamStatus.value = loaded;
      }
    } finally {
      isStatusLoading.value = false;
    }
  }

  async function _loadStimmzettel() {
    isStimmzettelLoading.value = true;
    try {
      savedStimmzettel.value = await getStimmzettel(
        wahlID,
        wahlbezirkID,
        teamID
      );
    } finally {
      isStimmzettelLoading.value = false;
    }
  }

  async function _postTeamStatus(
    statusToChange: StimmzettelerfassungTeamStatusEnum
  ) {
    const newStatus: StimmzettelerfassungTeamStatus = {
      status: statusToChange,
    };
    await erfassungTeamStatusService.postErfassungTeamStatus(
      wahlID,
      wahlbezirkID,
      teamID,
      newStatus,
      false
    );
    teamStatus.value = newStatus;
  }

  return {
    //Props
    activeStimmzettel,
    teamStatus: readonly(teamStatus),
    isErfassungsDialogVisible,
    isKennungsDialogVisible,
    isStatusLoading: readonly(isStatusLoading),
    isStimmzettelLoading: readonly(isStimmzettelLoading),
    savedStimmzettel: computed(() => savedStimmzettel.value),

    //actions
    sendStatusInBearbeitung,
    sendStatusUnterbrochen,
    saveNewStimmzettel,
    startNewEmptyStimmzettelWithStimmzettelkennung,

    //imported functions
    ...buttonUtils,
  };
}
