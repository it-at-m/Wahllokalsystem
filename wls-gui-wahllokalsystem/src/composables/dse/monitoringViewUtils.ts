import type { StimmzettelerfassungStatus } from "@/types/dse/StimmzettelerfassungStatus.ts";
import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/StimmzettelerfassungTeamStatusEntry.ts";

import { onActivated, ref } from "vue";

import { useDseWorkflowStatusService } from "@/composables/dse/dseWorkflowStatusService.ts";
import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatusService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useMonitoringViewUtils(wahlID: string, wahlbezirkID: string) {
  const teamstatusList = ref<StimmzettelerfassungTeamStatusEntry[]>([]);
  const lastLoading = ref<Date>();
  const isAktualisierenLoading = ref(false);
  const workflowStatus = ref<StimmzettelerfassungStatus | null>(null);

  const erfassungTeamStatusService = useStimmzettelerfassungTeamStatusService();
  const userNotificationService = useUserNotificationService();
  const { loadDseWorkflowStatus } = useDseWorkflowStatusService();

  async function onMonitoringSynchronisierenClicked() {
    await _loadTeamStatusListe();
  }

  onActivated(async () => {
    try {
      await _loadTeamStatusListe(false);
    } catch {
      userNotificationService.addNotification(
        `Team-Status konnten nicht initialisiert werden.`,
        UserNotificationCategoryEnum.ERROR
      );
    }
    workflowStatus.value = await loadDseWorkflowStatus(
      wahlID,
      wahlbezirkID,
      false
    );
  });

  async function _loadTeamStatusListe(sendNotification = true) {
    try {
      isAktualisierenLoading.value = true;
      const loaded =
        await erfassungTeamStatusService.loadErfassungTeamStatusListe(
          wahlID,
          wahlbezirkID,
          sendNotification
        );
      if (loaded) {
        teamstatusList.value = loaded;
        lastLoading.value = new Date();
      }
    } finally {
      isAktualisierenLoading.value = false;
    }
  }

  return {
    teamstatusList,
    lastLoading,
    isAktualisierenLoading,
    workflowStatus,
    onMonitoringSynchronisierenClicked,
  };
}
