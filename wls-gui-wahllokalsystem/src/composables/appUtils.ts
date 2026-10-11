import { storeToRefs } from "pinia";

import { useStimmzettelerfassungTeamStatusDataService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelErfassungTeamStatusDataService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useDataSyncStore } from "@/stores/dataSyncStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useAppUtils() {
  const { currentUserWahlMetadata, currentUserTeamName } =
    storeToRefs(useUserStore());
  const { addNotification } = useUserNotificationService();

  async function initStimmzettelerfassungTeamStatus() {
    try {
      for (const metadata of currentUserWahlMetadata.value) {
        const teamStatusService = useStimmzettelerfassungTeamStatusDataService(
          metadata.wahlID,
          metadata.wahlbezirkID
        );
        await teamStatusService.initTeamStatus(currentUserTeamName.value);
        useDataSyncStore().registerSyncAdapter({
          getTasks: teamStatusService.getTasksToSync,
        });
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
