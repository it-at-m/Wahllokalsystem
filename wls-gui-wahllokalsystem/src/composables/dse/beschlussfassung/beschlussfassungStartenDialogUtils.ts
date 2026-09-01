import { computed, readonly, ref } from "vue";

import { useDseWorkflowStatusService } from "@/composables/dse/dseWorkflowStatusService.ts";
import { useStimmzettelService } from "@/composables/dse/stimmzettelerfassung/stimmzettelService.ts";
import router from "@/plugins/router.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";
import { DseStepsEnum } from "@/types/navigation/DseStepsEnum.ts";

export function useBeschlussfassungStartenDialogUtils() {
  const { saveDseWorkflowStatus } = useDseWorkflowStatusService();
  const { getAnzahlStimmzettel } = useStimmzettelService();

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

    await router.push({
      name: DseStepsEnum.DSE_BESCHLUSSFASSUNG,
      params: { wahlId: wahlId, wahlbezirkId: wahlbezirkId },
    });
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
