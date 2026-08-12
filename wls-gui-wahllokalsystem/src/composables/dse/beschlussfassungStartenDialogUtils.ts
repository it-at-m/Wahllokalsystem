import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/StimmzettelerfassungTeamStatusEntry.ts";

import { useDseWorkflowStatusService } from "@/composables/dse/dseWorkflowStatusService.ts";
import { useStimmzettelService } from "@/composables/dse/stimmzettelService.ts";
import router from "@/plugins/router.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";
import { DseStepsEnum } from "@/types/navigation/DseStepsEnum.ts";

export function useBeschlussfassungStartenDialogUtils() {
  const { getStimmzettel } = useStimmzettelService();
  const { saveDseWorkflowStatus } = useDseWorkflowStatusService();

  async function loadStimmzettelCount(
    wahlId: string,
    wahlbezirkId: string,
    teamstatusList: StimmzettelerfassungTeamStatusEntry[]
  ) {
    const stimmzettel = await Promise.all(
      teamstatusList.map((team) =>
        getStimmzettel(wahlId, wahlbezirkId, team.teamID)
      )
    );
    return stimmzettel.reduce((sum, current) => sum + current.length, 0);
  }

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
    loadStimmzettelCount,
    updateWorkflowStatusAndNavigate,
  };
}
