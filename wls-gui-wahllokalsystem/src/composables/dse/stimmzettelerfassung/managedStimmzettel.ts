import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { StimmenSummary } from "@/types/dse/stimmzettelerfassung/StimmenSummary.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Ref } from "vue";

import { computed } from "vue";

import { useEinzelstimmeTools } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/einzelstimmeTools.ts";
import { useKandidatTools } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/kandidatTools.ts";
import { useReststimmeTools } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/reststimmeTools.ts";
import { useUngueltigeStimmeTools } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/ungueltigeStimmeTools.ts";
import { useWahlvorschlagTools } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/wahlvorschlagTools.ts";
import { useStimmzettelChangeHistory } from "@/composables/dse/stimmzettelerfassung/stimmzettelChangeHistory.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

/**
 * Check UI/UX Adr to see the rules:
 * https://it-at-m.github.io/Wahllokalsystem/technik/adr/ui/adr010-dse-stimmvergabe-stimmen-ergaenzen.html
 *
 * @param stimmzettel
 * @param wahlID
 */
export function useManagedStimmzettel(
  stimmzettel: Ref<Stimmzettel>,
  wahlID: string
) {
  const changeHistory = useStimmzettelChangeHistory();
  const {
    kandidatenOfStimmzettel,
    getKandidatToAddVotesByOrdnungszahl,
    getKandidatToAddVotesForRangeByOrdnungszahl,
    getKandidatForStreichungByOrdnungszahl,
    getKandidatToRemoveStreichungByOrdnungszahl,
  } = useKandidatTools(stimmzettel);
  const { getWahlvorschlagByOrdnungszahl } = useWahlvorschlagTools(stimmzettel);
  const { addVotesToKandidat, removeVotesFromKandidat } =
    useEinzelstimmeTools();
  const { addInvalidVotesToKandidat, removeInvalidVotesFromKandidat } =
    useUngueltigeStimmeTools();

  const kandidatenWithValues = computed(() =>
    kandidatenOfStimmzettel.value.filter(_hasKandidatAnyStimmeOrStreichung)
  );

  const stimmenSummary = computed(() => {
    const summary: StimmenSummary = {
      einzelstimmen: 0,
      ungueltigeStimmen: 0,
      reststimmen: 0,
      streichungen: 0,
    };
    return kandidatenWithValues.value.reduce(
      (summary, kandidat) => _updateSummaryByKandidat(summary, kandidat),
      summary
    );
  });

  const wahlvorschlaegeWithListenkreuz = computed(() =>
    stimmzettel.value.wahlvorschlaege.filter(
      (wahlvorschlag) => wahlvorschlag.selected
    )
  );

  const {
    selectWahlvorschlag,
    deselectWahlvorschlag,
    updateReststimmenWhenVotesAdded,
    updateReststimmenWhenVotesRemoved,
  } = useReststimmeTools(wahlID, stimmenSummary, stimmzettel);

  function resetStimmzettel() {
    changeHistory.reset();
    stimmzettel.value.wahlvorschlaege.map((wahlvorschlag) => {
      wahlvorschlag.selected = false;
      wahlvorschlag.kandidaten.map((kandidat) => {
        kandidat.einzelstimmen = null;
        kandidat.ungueltigeStimmen = null;
        kandidat.reststimmen = null;
        kandidat.durchgestrichen = false;
      });
    });
  }

  /**
   *
   * @param ordnungszahl
   * @param votesToAdd
   *
   * @throws ManagedStimmzettelError when ordnungszahl does not describe a valid kandidat or rules deny action
   */
  function kandidatAddEinzelstimmenOrThrow(
    ordnungszahl: number,
    votesToAdd: number
  ) {
    _isNotSafeIntegerThrow(
      votesToAdd,
      "Die Anzahl der hinzuzufügenden Stimmen muss eine ganze Zahl größer 0 sein."
    );
    const kandidat = getKandidatToAddVotesByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }
    addVotesToKandidat(kandidat, votesToAdd);
    updateReststimmenWhenVotesAdded();
    changeHistory.registerKandidatEinzelstimmenAdded(kandidat, votesToAdd);
  }

  function kandidatRemoveEinzelstimmenOrThrow(
    ordnungszahl: number,
    votesToRemove: number
  ) {
    _isNotSafeIntegerThrow(
      votesToRemove,
      "Die Anzahl der zu entfernenden Stimmen muss eine ganze Zahl größer 0 sein."
    );
    const kandidat = getKandidatToAddVotesByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }
    if (!kandidat.einzelstimmen || kandidat.einzelstimmen < votesToRemove) {
      throw new ManagedStimmzettelError(
        `Von Kandidat*in mit Ordnungszahl ${ordnungszahl} können keine ${votesToRemove} Stimmen abgezogen werden.`
      );
    }
    removeVotesFromKandidat(kandidat, votesToRemove);
    updateReststimmenWhenVotesRemoved();
    changeHistory.registerKandidatEinzelstimmenRemoved(kandidat, votesToRemove);
  }

  function kandidatAddUngueltigeStimmenOrThrow(
    ordnungszahl: number,
    invalidVotesToAdd: number
  ) {
    _isNotSafeIntegerThrow(
      invalidVotesToAdd,
      "Die Anzahl der hinzuzufügenden ungültigen Stimmen muss eine ganze Zahl größer 0 sein."
    );
    const kandidat = getKandidatToAddVotesByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }
    addInvalidVotesToKandidat(kandidat, invalidVotesToAdd);
    changeHistory.registerKandidatUngueltigeStimmenAdded(
      kandidat,
      invalidVotesToAdd
    );
  }

  function kandidatRemoveUngueltigeStimmenOrThrow(
    ordnungszahl: number,
    invalidVotesToRemove: number
  ) {
    _isNotSafeIntegerThrow(
      invalidVotesToRemove,
      "Die Anzahl der zu entfernenden ungültigen Stimmen muss eine ganze Zahl größer 0 sein."
    );
    const kandidat = getKandidatToAddVotesByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }
    if (
      !kandidat.ungueltigeStimmen ||
      kandidat.ungueltigeStimmen < invalidVotesToRemove
    ) {
      throw new ManagedStimmzettelError(
        `Von Kandidat*in mit Ordnungszahl ${ordnungszahl} können keine ${invalidVotesToRemove} ungültigen Stimmen abgezogen werden.`
      );
    }
    removeInvalidVotesFromKandidat(kandidat, invalidVotesToRemove);
    changeHistory.registerKandidatUngueltigeStimmenRemoved(
      kandidat,
      invalidVotesToRemove
    );
  }

  function kandidatenAddStimmenInRangeOrThrow(
    lowerOrdnungszahl: number,
    upperOrdnungszahl: number,
    votesToAdd: number
  ) {
    _isNotSafeIntegerThrow(
      votesToAdd,
      "Die Anzahl der hinzuzufügenden Stimmen muss eine ganze Zahl größer 0 sein."
    );
    const kandidaten = _getKandidatenInRangeOrThrow(
      lowerOrdnungszahl,
      upperOrdnungszahl
    );
    if (kandidaten.filter((kandidat) => kandidat.durchgestrichen).length > 0) {
      throw new ManagedStimmzettelError(
        "Der Bereich enthält mindestens eine Streichung."
      );
    }
    kandidaten.map((kandidat) => addVotesToKandidat(kandidat, votesToAdd));
    updateReststimmenWhenVotesAdded();
    changeHistory.registerKandidatEinzelstimmenRangeAdded(
      kandidaten,
      votesToAdd
    );
  }

  function kandidatAddStreichungOrThrow(ordnungszahl: number) {
    const kandidat = getKandidatForStreichungByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }
    if (kandidat.durchgestrichen) {
      throw new ManagedStimmzettelError(`Kandidat*in ist bereits gestrichen.`);
    }
    kandidat.durchgestrichen = true;
    changeHistory.registerKandidatStreichungSet(kandidat);
  }

  function kandidatRemoveStreichungOrThrow(ordnungszahl: number) {
    const kandidat = getKandidatToRemoveStreichungByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }
    if (!kandidat.durchgestrichen) {
      throw new ManagedStimmzettelError(
        `Für Kandidat*in mit Ordnungszahl ${ordnungszahl} kann keine Streichung entfernt werden.`
      );
    }
    kandidat.durchgestrichen = false;
    changeHistory.registerKandidatStreichungUnset(kandidat);
  }

  function kandidatenStreichungenInRangeOrThrow(
    lowerOrdnungszahl: number,
    upperOrdnungszahl: number
  ) {
    const kandidaten = _getKandidatenInRangeOrThrow(
      lowerOrdnungszahl,
      upperOrdnungszahl
    );
    if (kandidaten.every((kandidat) => kandidat.durchgestrichen)) {
      throw new ManagedStimmzettelError(`Der Bereich ist bereits gestrichen.`);
    }
    kandidaten.map((kandidat) => (kandidat.durchgestrichen = true));
    changeHistory.registerKandidatStreichungRangeSet(kandidaten);
  }

  function kandidatenRemoveStreichungenInRangeOrThrow(
    lowerOrdnungszahl: number,
    upperOrdnungszahl: number
  ) {
    const kandidaten = _getKandidatenInRangeOrThrow(
      lowerOrdnungszahl,
      upperOrdnungszahl
    );
    if (kandidaten.every((kandidat) => !kandidat.durchgestrichen)) {
      throw new ManagedStimmzettelError(
        `Im Bereich sind bereits alle Streichungen entfernt.`
      );
    }
    kandidaten.map((kandidat) => (kandidat.durchgestrichen = false));
    changeHistory.registerKandidatStreichungRangeUnset(kandidaten);
  }

  function wahlvorschlagAddVotesOrThrow(wahlvorschlagOrdnungszahl: number) {
    const wahlvorschlag = getWahlvorschlagByOrdnungszahl(
      wahlvorschlagOrdnungszahl
    );
    if (!wahlvorschlag) {
      throw new ManagedStimmzettelError(
        `Wahlvorschlag mit Ordnungszahl ${wahlvorschlagOrdnungszahl} existiert nicht.`
      );
    }
    if (wahlvorschlag.selected) {
      throw new ManagedStimmzettelError(
        `Wahlvorschlag ist bereits ausgewählt.`
      );
    }
    selectWahlvorschlag(wahlvorschlag);
    changeHistory.registerWahlvorschlagSelected(wahlvorschlag);
  }

  function wahlvorschlagRemoveVotesOrThrow(wahlvorschlagOrdnungszahl: number) {
    const wahlvorschlag = getWahlvorschlagByOrdnungszahl(
      wahlvorschlagOrdnungszahl
    );
    if (!wahlvorschlag) {
      throw new ManagedStimmzettelError(
        `Wahlvorschlag mit Ordnungszahl ${wahlvorschlagOrdnungszahl} existiert nicht.`
      );
    }
    if (!wahlvorschlag.selected) {
      throw new ManagedStimmzettelError(`Wahlvorschlag ist bereits abgewählt.`);
    }
    deselectWahlvorschlag(wahlvorschlag);
    updateReststimmenWhenVotesRemoved();
    changeHistory.registerWahlvorschlagDeselected(wahlvorschlag);
  }

  function _hasKandidatAnyStimmeOrStreichung(kandidat: Kandidat) {
    return (
      kandidat.einzelstimmen !== null ||
      kandidat.ungueltigeStimmen ||
      kandidat.reststimmen ||
      kandidat.durchgestrichen
    );
  }

  function _updateSummaryByKandidat(
    stimmenSummary: StimmenSummary,
    kandidat: Kandidat
  ) {
    if (kandidat.ungueltigeStimmen) {
      stimmenSummary.ungueltigeStimmen += kandidat.ungueltigeStimmen;
    }
    if (kandidat.reststimmen) {
      stimmenSummary.reststimmen += kandidat.reststimmen;
    }
    if (kandidat.durchgestrichen) {
      stimmenSummary.streichungen += 1;
    }
    if (kandidat.einzelstimmen) {
      stimmenSummary.einzelstimmen += kandidat.einzelstimmen;
    }
    return stimmenSummary;
  }

  function _getKandidatenInRangeOrThrow(
    lowerOrdnungszahl: number,
    upperOrdnungszahl: number
  ): Kandidat[] {
    const kandidaten: Kandidat[] = [];
    for (
      let ordnungszahl = lowerOrdnungszahl;
      ordnungszahl <= upperOrdnungszahl;
      ordnungszahl++
    ) {
      const kandidatenByOrdnungszahl =
        getKandidatToAddVotesForRangeByOrdnungszahl(ordnungszahl);
      if (!kandidatenByOrdnungszahl) {
        throw new ManagedStimmzettelError(
          `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
        );
      }
      kandidatenByOrdnungszahl.map((kandidat) => kandidaten.push(kandidat));
    }
    return kandidaten;
  }

  function _isNotSafeIntegerThrow(value: number, errorMessage: string) {
    if (!Number.isSafeInteger(value) || value < 0) {
      throw new ManagedStimmzettelError(errorMessage);
    }
  }

  return {
    changeHistory,
    resetStimmzettel,
    kandidatAddEinzelstimmenOrThrow,
    kandidatRemoveEinzelstimmenOrThrow,
    kandidatAddUngueltigeStimmenOrThrow,
    kandidatRemoveUngueltigeStimmenOrThrow,
    kandidatenAddStimmenInRangeOrThrow,
    kandidatAddStreichungOrThrow,
    kandidatRemoveStreichungOrThrow,
    kandidatenStreichungenInRangeOrThrow,
    kandidatenRemoveStreichungenInRangeOrThrow,
    wahlvorschlagAddVotesOrThrow,
    wahlvorschlagRemoveVotesOrThrow,
    stimmzettel: computed(() => stimmzettel.value),
    stimmenSummary,
    wahlvorschlaegeWithListenkreuz,
  };
}
export type ManagedStimmzettel = ReturnType<typeof useManagedStimmzettel>;
