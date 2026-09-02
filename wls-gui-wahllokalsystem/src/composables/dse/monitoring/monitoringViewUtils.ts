import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";
import type { StimmzettelerfassungStatus } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatus.ts";

import { onActivated, ref } from "vue";

import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts";
import { useDseWorkflowStatusService } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusService.ts";

export function useMonitoringViewUtils(wahlID: string, wahlbezirkID: string) {
  const teamstatusList = ref<StimmzettelerfassungTeamStatusEntry[]>([]);
  const lastLoading = ref<Date>();
  const isAktualisierenLoading = ref(false);
  const isWorkflowStatusLoading = ref(false);
  const workflowStatus = ref<StimmzettelerfassungStatus | null>(null);

  const erfassungTeamStatusService = useStimmzettelerfassungTeamStatusService();
  const { loadDseWorkflowStatus } = useDseWorkflowStatusService();

  async function onMonitoringSynchronisierenClicked() {
    await _loadTeamStatusListe();
  }

  async function reloadWorkflowStatus() {
    await _loadWorkflowStatus();
  }

  onActivated(async () => {
    await Promise.allSettled([_loadTeamStatusListe(), _loadWorkflowStatus()]);
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

  async function _loadWorkflowStatus() {
    isWorkflowStatusLoading.value = true;
    try {
      workflowStatus.value = await loadDseWorkflowStatus(
        wahlID,
        wahlbezirkID,
        true
      );
    } finally {
      isWorkflowStatusLoading.value = false;
    }
  }

  return {
    teamstatusList,
    lastLoading,
    isAktualisierenLoading,
    isWorkflowStatusLoading,
    workflowStatus,
    onMonitoringSynchronisierenClicked,
    reloadWorkflowStatus,
  };
}
