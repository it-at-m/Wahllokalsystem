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
  const isAktualisiserenLoading = ref(false);
  const workflowStatus = ref<StimmzettelerfassungStatus | null>(null);

  const erfassungTeamStatusService = useStimmzettelerfassungTeamStatusService();
  const userNotificationService = useUserNotificationService();
  const { loadDseWorkflowStatus } = useDseWorkflowStatusService();

  async function onMonitoringSynchronisierenClicked(sendNotification = false) {
    await _loadTeamStatusListe(sendNotification);
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

  async function _loadTeamStatusListe(sendNotification: boolean) {
    try {
      isAktualisiserenLoading.value = true;
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
      isAktualisiserenLoading.value = false;
    }
  }

  return {
    teamstatusList,
    lastLoading,
    isAktualisiserenLoading,
    workflowStatus,
    onMonitoringSynchronisierenClicked,
  };
}
