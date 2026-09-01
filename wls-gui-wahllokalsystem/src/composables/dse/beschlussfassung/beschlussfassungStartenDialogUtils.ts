import { computed, readonly, ref } from "vue";

import { useDseWorkflowStatusService } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusService.ts";
import { useExperimentalStimmzettelService } from "@/composables/experimental/experimentalStimmzettelService.ts";
import router from "@/plugins/router.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";
import { DseStepsEnum } from "@/types/navigation/DseStepsEnum.ts";

export function useBeschlussfassungStartenDialogUtils(
  wahlId: string,
  wahlbezirkId: string
) {
  const { saveDseWorkflowStatus } = useDseWorkflowStatusService();
  const { getAnzahlStimmzettel } = useExperimentalStimmzettelService(
    wahlId,
    wahlbezirkId
  );

  const stimmzettelCount = ref<number | null>(null);
  const isAnzahlStimmzettelLoading = ref(false);

  const isConfirmButtonInLoadingState = computed(
    () => isAnzahlStimmzettelLoading.value || stimmzettelCount.value === null
  );

  async function updateWorkflowStatusAndNavigate() {
    await saveDseWorkflowStatus(wahlId, wahlbezirkId, {
      status: StimmzettelerfassungStatusEnum.SteAbgeschlossen,
    });

    await router.push({
      name: DseStepsEnum.DSE_BESCHLUSSFASSUNG,
      params: { wahlId: wahlId, wahlbezirkId: wahlbezirkId },
    });
  }

  async function loadAnzahlStimmzettel() {
    isAnzahlStimmzettelLoading.value = true;
    try {
      stimmzettelCount.value = null;
      stimmzettelCount.value = await getAnzahlStimmzettel();
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
