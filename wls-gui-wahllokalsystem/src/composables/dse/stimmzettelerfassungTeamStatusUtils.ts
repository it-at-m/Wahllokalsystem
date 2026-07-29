import { storeToRefs } from "pinia";

import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatusService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

export function useStimmzettelerfassungTeamStatusUtils() {
  const { loadErfassungTeamStatus, postErfassungTeamStatus } =
    useStimmzettelerfassungTeamStatusService();
  const { currentUserWahlMetadata, currentUserTeamName } =
    storeToRefs(useUserStore());

  async function initStimmzettelerfassungTeamStatus() {
    currentUserWahlMetadata.value.map(async (metadata) => {
      const teamStatus = await loadErfassungTeamStatus(
        metadata.wahlID,
        metadata.wahlbezirkID,
        currentUserTeamName.value,
        false
      );
      if (!teamStatus) {
        await postErfassungTeamStatus(
          metadata.wahlID,
          metadata.wahlbezirkID,
          currentUserTeamName.value,
          { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT }
        );
      }
    });
  }

  return {
    initStimmzettelerfassungTeamStatus,
  };
}
