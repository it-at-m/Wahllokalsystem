import { computed } from "vue";

import { useDseWorkflowStatusService } from "@/composables/dse/dseWorkflowStatusService.ts";
import { useStimmzettelFetchService } from "@/composables/dse/stimmzettelFetchService.ts";
import router from "@/plugins/router.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";
import { DseStepsEnum } from "@/types/navigation/DseStepsEnum.ts";

export function useTheBeschlussfassungStartenDialogUtils() {
  const { saveDseWorkflowStatus } = useDseWorkflowStatusService();
  const {
    isLoadingAnzahlStimmzettel,
    lastLoadedAnzahlStimmzettel,
    loadAnzahlStimmzettel,
  } = useStimmzettelFetchService();

  const isConfirmButtonInLoadingState = computed(
    () =>
      isLoadingAnzahlStimmzettel.value ||
      lastLoadedAnzahlStimmzettel.value === undefined
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

  return {
    isConfirmButtonInLoadingState,
    isLoadingAnzahlStimmzettel,

    lastLoadedAnzahlStimmzettel,

    loadAnzahlStimmzettel,
    updateWorkflowStatusAndNavigate,
  };
}
