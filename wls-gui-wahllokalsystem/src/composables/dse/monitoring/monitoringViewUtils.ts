import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";

import { onActivated, ref } from "vue";

import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts";
import { useStimmzettelerfassungStatusState } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusState.ts";

export function useMonitoringViewUtils(wahlID: string, wahlbezirkID: string) {
  const stimmzettelerfassungState = useStimmzettelerfassungStatusState(
    wahlID,
    wahlbezirkID
  );
  const teamstatusList = ref<StimmzettelerfassungTeamStatusEntry[]>([]);
  const lastLoading = ref<Date>();
  const isAktualisierenLoading = ref(false);

  const erfassungTeamStatusService = useStimmzettelerfassungTeamStatusService();

  async function onMonitoringSynchronisierenClicked() {
    await _loadTeamStatusListe();
  }

  onActivated(async () => {
    await Promise.allSettled([
      _loadTeamStatusListe(),
      stimmzettelerfassungState.loadWorkflowStatus(),
    ]);
  });

  async function _loadTeamStatusListe() {
    try {
      isAktualisierenLoading.value = true;
      const loaded =
        await erfassungTeamStatusService.loadErfassungTeamStatusListe(
          wahlID,
          wahlbezirkID,
          true
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
    onMonitoringSynchronisierenClicked,

    ...stimmzettelerfassungState,
  };
}
