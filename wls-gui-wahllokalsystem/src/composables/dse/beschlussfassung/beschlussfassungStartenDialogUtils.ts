import { computed, readonly, ref } from "vue";

import { useStimmzettelService } from "@/composables/dse/stimmzettelerfassung/stimmzettelService.ts";
import { useDseWorkflowStatusService } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusService.ts";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import router from "@/plugins/router.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";

export function useBeschlussfassungStartenDialogUtils() {
  const { saveDseWorkflowStatus } = useDseWorkflowStatusService();
  const { getAnzahlStimmzettel } = useStimmzettelService();
  const { getNextRoute } = useNavigationService();
  const { setStepDone } = useWorkflowStore();

  const stimmzettelCount = ref<number | null>(null);
  const isAnzahlStimmzettelLoading = ref(false);

  const isConfirmButtonInLoadingState = computed(
    () => isAnzahlStimmzettelLoading.value || stimmzettelCount.value === null
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
    } finally {
      isAnzahlStimmzettelLoading.value = false;
    }
  }

  return {
    isAnzahlStimmzettelLoading: readonly(isAnzahlStimmzettelLoading),
    isConfirmButtonInLoadingState,
    stimmzettelCount: readonly(stimmzettelCount),

    loadAnzahlStimmzettel,
    updateWorkflowStatusAndNavigate,
  };
}
