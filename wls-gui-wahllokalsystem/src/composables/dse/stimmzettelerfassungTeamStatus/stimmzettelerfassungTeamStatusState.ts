import type { StimmzettelerfassungTeamStatus } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatus.ts";

import { ref } from "vue";

import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts";

export function useStimmzettelerfassungTeamStatusState(
  wahlID: string,
  wahlbezirkID: string,
  teamID: string
) {
  const {
    loadErfassungTeamStatus: _loadErfassungTeamStatus,
    postErfassungTeamStatus: _postErfassungTeamStatus,
    isSaving,
  } = useStimmzettelerfassungTeamStatusService();

  const teamStatus = ref<StimmzettelerfassungTeamStatus | null>(null);

  async function loadErfassungTeamStatus(sendNotification = true) {
    teamStatus.value = await _loadErfassungTeamStatus(
      wahlID,
      wahlbezirkID,
      teamID,
      sendNotification
    );
  }

  async function postErfassungTeamStatus(
    teamStatusToSend: StimmzettelerfassungTeamStatus,
    sendNotification = true
  ) {
    teamStatus.value = await _postErfassungTeamStatus(
      wahlID,
      wahlbezirkID,
      teamID,
      teamStatusToSend,
      sendNotification
    );
  }

  return {
    isSaving,
    teamStatus,
    loadErfassungTeamStatus,
    postErfassungTeamStatus,
  };
}
