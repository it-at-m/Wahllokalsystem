import { storeToRefs } from "pinia";

import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useAppUtils() {
  const { loadErfassungTeamStatus, postErfassungTeamStatus } =
    useStimmzettelerfassungTeamStatusService();
  const { currentUserWahlMetadata, currentUserTeamName } =
    storeToRefs(useUserStore());
  const { addNotification } = useUserNotificationService();

  async function initStimmzettelerfassungTeamStatus() {
    try {
      for (const metadata of currentUserWahlMetadata.value) {
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
            { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT },
            false
          );
        }
      }
    } catch (error) {
      addNotification(
        "Teamstatus konnte nicht initialisiert werden.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  return {
    initStimmzettelerfassungTeamStatus,
  };
}
