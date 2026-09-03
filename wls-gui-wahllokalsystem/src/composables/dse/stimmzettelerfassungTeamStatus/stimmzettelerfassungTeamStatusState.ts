import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";

import { storeToRefs } from "pinia";
import { readonly, ref } from "vue";

import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useStimmzettelerfassungTeamStatusState() {
  const {
    loadErfassungTeamStatus,
    postErfassungTeamStatus,
    loadErfassungTeamStatusListe,
  } = useStimmzettelerfassungTeamStatusService();
  const {
    currentUserWahlMetadata,
    currentUserTeamName,
    currentUserHauptWahlID,
    currentUserWahlbezirkID,
  } = storeToRefs(useUserStore());
  const { addNotification } = useUserNotificationService();

  const teamstatusList = ref<StimmzettelerfassungTeamStatusEntry[]>([]);
  const isTeamStatusLoading = ref(false);
  const lastTeamstatusLoadingTime = ref<Date>();

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

  async function loadTeamStatusListe() {
    try {
      isTeamStatusLoading.value = true;
      const loaded = await loadErfassungTeamStatusListe(
        currentUserHauptWahlID.value,
        currentUserWahlbezirkID.value,
        true
      );
      if (loaded) {
        teamstatusList.value = loaded;
        lastTeamstatusLoadingTime.value = new Date();
      }
    } finally {
      isTeamStatusLoading.value = false;
    }
  }

  return {
    teamstatusList: teamstatusList,
    lastTeamstatusLoadingTime: readonly(lastTeamstatusLoadingTime),
    isTeamStatusLoading: readonly(isTeamStatusLoading),
    initStimmzettelerfassungTeamStatus,
    loadTeamStatusListe,
  };
}
