import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { storeToRefs } from "pinia";
import { computed, readonly, ref } from "vue";

import { useStimmzettelService } from "@/composables/dse/stimmzettelerfassung/stimmzettelService.ts";
import { useStimmzettelerfassungTeamStatusListState } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusListState.ts";
import { useDseWorkflowStatusService } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusService.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import router from "@/plugins/router.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useBeschlussfassungStartenDialogUtils(
  wahlID: string,
  wahlbezirkID: string,
  closeDialog: () => void
) {
  const { addNotification } = useUserNotificationService();
  const { saveDseWorkflowStatus } = useDseWorkflowStatusService();
  const { getAnzahlStimmzettel, getStimmzettel } = useStimmzettelService();
  const { getNextRoute } = useNavigationService();
  const { setStepDone } = useWorkflowStore();
  const { getStimmzettelumschlaege } = useErgebnisService();
  const { wahlenActions } = useWahlenStore();
  const { currentUserTeamName } = storeToRefs(useUserStore());

  const stimmzettelerfassungTeamState =
    useStimmzettelerfassungTeamStatusListState(wahlID, wahlbezirkID);

  const stimmzettelCount = ref<number | null>(null);
  const stimmzettelumschlaegeCount = ref<number | null>(null);
  const isAnzahlStimmzettelLoading = ref(false);
  const isUpdatingForeignStimmzettel = ref(false);

  const isConfirmButtonInLoadingState = computed(
    () =>
      isAnzahlStimmzettelLoading.value ||
      stimmzettelCount.value === null ||
      stimmzettelumschlaegeCount.value === null ||
      isUpdatingForeignStimmzettel.value
  );

  async function updateWorkflowStatusAndNavigate(
    wahlId: string,
    wahlbezirkId: string
  ) {
    await saveDseWorkflowStatus(wahlId, wahlbezirkId, {
      status: StimmzettelerfassungStatusEnum.SteAbgeschlossen,
    });

    setStepDone(
      wahlId,
      wahlbezirkId,
      MbwStepsEnum.MBW_DSE_MONITORING_ERFASSUNGSSTATUS
    );

    await router.push(getNextRoute());
  }

  async function loadAnzahlStimmzettel(wahlId: string, wahlbezirkId: string) {
    isAnzahlStimmzettelLoading.value = true;
    try {
      stimmzettelCount.value = null;
      stimmzettelCount.value = await getAnzahlStimmzettel(wahlId, wahlbezirkId);
      stimmzettelumschlaegeCount.value = null;
      const wahl = wahlenActions.getWahlOrUndefinedById(wahlId);
      if (wahl) {
        const loadedStimmzettelumschlaege = await getStimmzettelumschlaege(
          wahl,
          wahlbezirkId,
          "",
          false
        );
        stimmzettelumschlaegeCount.value =
          loadedStimmzettelumschlaege?.anzahlWaehler || null;
      }
    } finally {
      isAnzahlStimmzettelLoading.value = false;
    }
  }

  async function continueProcessAfterForceRefreshOfForeignStimmzettel() {
    try {
      isUpdatingForeignStimmzettel.value = true;

      await _refreshForeignStimmzettel();

      await updateWorkflowStatusAndNavigate(wahlID, wahlbezirkID);
      closeDialog();
    } finally {
      isUpdatingForeignStimmzettel.value = false;
    }
  }

  async function _refreshForeignStimmzettel() {
    await stimmzettelerfassungTeamState.loadTeamStatusListe(true);
    const otherTeamsIDs = stimmzettelerfassungTeamState.teamstatusList.value
      .map((entry) => entry.teamID)
      .filter((teamID) => teamID !== currentUserTeamName.value);

    const refreshPromises: Promise<PersistedStimmzettel[]>[] = [];
    otherTeamsIDs.forEach((teamID) => {
      refreshPromises.push(
        getStimmzettel(wahlID, wahlbezirkID, teamID, false, true)
      );
    });
    try {
      await Promise.all(refreshPromises);
    } catch (error) {
      addNotification(
        "Stimmzettel konnten nicht synchronisiert werden. Bitte versuchen Sie es später erneut.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  return {
    isAnzahlStimmzettelLoading: readonly(isAnzahlStimmzettelLoading),
    isConfirmButtonInLoadingState,
    stimmzettelCount: readonly(stimmzettelCount),
    stimmzettelumschlaegeCount: readonly(stimmzettelumschlaegeCount),

    loadAnzahlStimmzettel,
    continueProcessAfterForceRefreshOfForeignStimmzettel,
    updateWorkflowStatusAndNavigate,
  };
}
