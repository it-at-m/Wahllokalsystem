import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { computed, onActivated } from "vue";

import { useAllStimmzettelOfWahlbezirkState } from "@/composables/dse/allStimmzettelOfWahlbezirkState.ts";
import { useStimmzettelService } from "@/composables/dse/stimmzettelerfassung/stimmzettelService.ts";
import { useStimmzettelTools } from "@/composables/dse/stimmzettelerfassung/stimmzettelTools.ts";
import { useStimmzettelerfassungStatusState } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusState.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";

const { getStimmzettel, saveStimmzettel } = useStimmzettelService();
const { isSamePersistedStimmzettel } = useStimmzettelTools();

export function useBeschlussfassungViewUtils(
  wahlID: string,
  wahlbezirkID: string
) {
  const { workflowStatus } = useStimmzettelerfassungStatusState(
    wahlID,
    wahlbezirkID
  );
  const { isLoading, stimmzettelOfWahlbezirk, loadStimmzettelOfWahlbezirk } =
    useAllStimmzettelOfWahlbezirkState(wahlID, wahlbezirkID);
  const { isBeschlussRequired } = useStimmzettelTools();

  const stimmzettelForBeschlussfassung = computed(() =>
    stimmzettelOfWahlbezirk.value.filter(isBeschlussRequired)
  );

  const completedStimmzettelForBeschlussfassung = computed(() =>
    stimmzettelForBeschlussfassung.value.filter(
      (stimmzettel) =>
        stimmzettel.gueltigkeit !==
        StimmzettelGueltigkeitEnum.BeschlussAusstehend
    )
  );

  const isBeschlussfassungBeendenButtonDisabled = computed(() => {
    if (isLoading.value) return true;
    else {
      return (
        workflowStatus.value?.status ===
          StimmzettelerfassungStatusEnum.BeAbgeschlossen ||
        stimmzettelForBeschlussfassung.value.length !==
          completedStimmzettelForBeschlussfassung.value.length
      );
    }
  });

  onActivated(async () => {
    await Promise.allSettled([loadStimmzettelOfWahlbezirk()]);
  });

  async function saveBeschlussStimmzettel(
    stimmzettelToSave: PersistedStimmzettel
  ) {
    const teamStimmzettelList = await getStimmzettel(
      wahlID,
      wahlbezirkID,
      stimmzettelToSave.teamID,
      false
    );

    const stimmzettelIndex = teamStimmzettelList.findIndex((s) =>
      isSamePersistedStimmzettel(s, stimmzettelToSave)
    );
    if (stimmzettelIndex >= 0) {
      teamStimmzettelList[stimmzettelIndex] = stimmzettelToSave;

      await saveStimmzettel(
        wahlID,
        wahlbezirkID,
        stimmzettelToSave.teamID,
        teamStimmzettelList
      );
    } else {
      throw new Error(
        `Fehler: Stimmzettel mit Kennung ${stimmzettelToSave.teamID} ${stimmzettelToSave.stimmzettelkennung} nicht gefunden.`
      );
    }
  }

  return {
    stimmzettelForBeschlussfassung,
    completedStimmzettelForBeschlussfassung,
    isStimmzettelForBeschlussLoading: isLoading,
    isBeschlussfassungBeendenButtonDisabled,
    saveBeschlussStimmzettel,
  };
}
