import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import { computed, onActivated, ref } from "vue";

import { useStimmzettelService } from "@/composables/dse/stimmzettelerfassung/stimmzettelService.ts";
import { useStimmzettelerfassungTeamStatusListState } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusListState.ts";
import { useStimmzettelerfassungStatusState } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusState.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";

const { getStimmzettel } = useStimmzettelService();

export function useBeschlussfassungViewUtils(
  wahlID: string,
  wahlbezirkID: string
) {
  const { workflowStatus } = useStimmzettelerfassungStatusState(
    wahlID,
    wahlbezirkID
  );
  const { teamstatusList, loadTeamStatusListe } =
    useStimmzettelerfassungTeamStatusListState(wahlID, wahlbezirkID);
  const isStimmzettelForBeschlussLoading = ref(false);
  const stimmzettelForBeschlussfassung = ref<Stimmzettel[]>([]);

  const completedStimmzettelForBeschlussfassung = computed(() =>
    stimmzettelForBeschlussfassung.value.filter(
      (stimmzettel) =>
        stimmzettel.gueltigkeit !==
        StimmzettelGueltigkeitEnum.BeschlussAusstehend
    )
  );

  const isBeschlussfassungBeendenButtonDisabled = computed(() => {
    return (
      workflowStatus.value?.status ===
        StimmzettelerfassungStatusEnum.BeAbgeschlossen ||
      stimmzettelForBeschlussfassung.value.length !==
        completedStimmzettelForBeschlussfassung.value.length
    );
  });

  onActivated(async () => {
    await Promise.allSettled([_loadStimmzettelAndFilterForBeschlussfassung()]);
  });

  async function _loadStimmzettelAndFilterForBeschlussfassung() {
    isStimmzettelForBeschlussLoading.value = true;
    try {
      await loadTeamStatusListe();
      const registeredTeams = computed(() =>
        teamstatusList.value.map((team) => team.teamID)
      );
      const stimmzettelOfAllTeams: Stimmzettel[] = [];

      for (const teamId of registeredTeams.value) {
        const stimmzettelforTeam = await getStimmzettel(
          wahlID,
          wahlbezirkID,
          teamId
        );
        stimmzettelOfAllTeams.push(...stimmzettelforTeam);
      }

      const onlyStimmzettelforBeschlussfassung = stimmzettelOfAllTeams.filter(
        (stimmzettel) =>
          stimmzettel.gueltigkeit ===
            StimmzettelGueltigkeitEnum.BeschlussAusstehend ||
          stimmzettel.beschlussfassung !== null
      );

      if (onlyStimmzettelforBeschlussfassung.length > 0) {
        stimmzettelForBeschlussfassung.value =
          onlyStimmzettelforBeschlussfassung;
      }
    } finally {
      isStimmzettelForBeschlussLoading.value = false;
    }
  }

  return {
    stimmzettelForBeschlussfassung,
    completedStimmzettelForBeschlussfassung,
    isStimmzettelForBeschlussLoading,
    isBeschlussfassungBeendenButtonDisabled,
  };
}
