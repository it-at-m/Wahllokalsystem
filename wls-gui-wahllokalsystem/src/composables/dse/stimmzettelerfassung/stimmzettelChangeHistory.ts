import type { InputHistoryItem } from "@/types/dse/stimmzettelerfassung/InputHistoryItem.ts";
import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";

import { computed, nextTick, ref } from "vue";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { InputHistoryTypeEnum } from "@/types/dse/stimmzettelerfassung/InputHistoryTypeEnum.ts";

export function useStimmzettelChangeHistory() {
  const changeHistory = ref<InputHistoryItem[]>([]);
  const lastUsedWahlvorschlag = ref<Wahlvorschlag | null>(null);
  const lastUsedKandidat = ref<Kandidat | null>(null);

  const { createTextVotes, createTextInvalidVotes } = useTextFormatter();

  function registerKandidatEinzelstimmenAdded(
    kandidat: Kandidat,
    count: number
  ) {
    changeHistory.value.push({
      type: InputHistoryTypeEnum.ADD_USER_VOTE,
      text: [
        `${kandidat.ordnungszahl} + ${createTextVotes(count)}`,
        kandidat.name,
      ],
    });

    _updateLatestUsedData(kandidat);
  }

  function registerKandidatEinzelstimmenRemoved(
    kandidat: Kandidat,
    count: number
  ) {
    changeHistory.value.push({
      type: InputHistoryTypeEnum.REMOVE_USER_VOTE,
      text: [
        `${kandidat.ordnungszahl} - ${createTextVotes(count)}`,
        kandidat.name,
      ],
    });

    _updateLatestUsedData(kandidat);
  }

  function registerKandidatEinzelstimmenRangeAdded(
    kandidaten: Kandidat[],
    count: number
  ) {
    const firstKandidat = kandidaten[0];
    const lastKandidat = kandidaten[kandidaten.length - 1];
    changeHistory.value.push({
      type: InputHistoryTypeEnum.VOTE_RANGE,
      text: [
        `${firstKandidat.ordnungszahl}-${lastKandidat.ordnungszahl} + ${createTextVotes(count)}`,
      ],
    });

    _updateLatestUsedData(lastKandidat);
  }

  function registerKandidatUngueltigeStimmenAdded(
    kandidat: Kandidat,
    count: number
  ) {
    changeHistory.value.push({
      type: InputHistoryTypeEnum.ADD_USER_VOTE,
      text: [
        `${kandidat.ordnungszahl} + ${createTextInvalidVotes(count)}`,
        kandidat.name,
      ],
    });

    _updateLatestUsedData(kandidat);
  }

  function registerKandidatUngueltigeStimmenRemoved(
    kandidat: Kandidat,
    count: number
  ) {
    changeHistory.value.push({
      type: InputHistoryTypeEnum.REMOVE_USER_VOTE,
      text: [
        `${kandidat.ordnungszahl} - ${createTextInvalidVotes(count)}`,
        kandidat.name,
      ],
    });

    _updateLatestUsedData(kandidat);
  }

  function registerKandidatStreichungSet(kandidat: Kandidat) {
    changeHistory.value.push({
      type: InputHistoryTypeEnum.DISCARD_KANDIDAT,
      text: [`${kandidat.ordnungszahl}`, kandidat.name],
    });

    _updateLatestUsedData(kandidat);
  }

  function registerKandidatStreichungUnset(kandidat: Kandidat) {
    changeHistory.value.push({
      type: InputHistoryTypeEnum.REVOKE_DISCARDED_KANDIDAT,
      text: [`${kandidat.ordnungszahl}`, kandidat.name],
    });

    _updateLatestUsedData(kandidat);
  }

  function registerKandidatStreichungRangeSet(kandidaten: Kandidat[]) {
    const firstKandidat = kandidaten[0];
    const lastKandidat = kandidaten[kandidaten.length - 1];
    changeHistory.value.push({
      type: InputHistoryTypeEnum.DISCARD_RANGE,
      text: [`${firstKandidat.ordnungszahl}-${lastKandidat.ordnungszahl}`],
    });

    _updateLatestUsedData(lastKandidat);
  }

  function registerKandidatStreichungRangeUnset(kandidaten: Kandidat[]) {
    const firstKandidat = kandidaten[0];
    const lastKandidat = kandidaten[kandidaten.length - 1];

    changeHistory.value.push({
      type: InputHistoryTypeEnum.REVOKE_DISCARDED_KANDIDAT,
      text: [`${firstKandidat.ordnungszahl}-${lastKandidat.ordnungszahl}`],
    });

    _updateLatestUsedData(lastKandidat);
  }

  function registerWahlvorschlagSelected(wahlvorschlag: Wahlvorschlag) {
    changeHistory.value.push({
      type: InputHistoryTypeEnum.SET_WAHLVORSCHLAG,
      text: [`${wahlvorschlag.kurzname}`],
    });

    _updateLatestUsedData(wahlvorschlag);
  }

  function registerWahlvorschlagDeselected(wahlvorschlag: Wahlvorschlag) {
    changeHistory.value.push({
      type: InputHistoryTypeEnum.REVOKE_WAHLVORSCHLAG,
      text: [`${wahlvorschlag.kurzname}`],
    });

    _updateLatestUsedData(wahlvorschlag);
  }

  function reset() {
    changeHistory.value = [];
    lastUsedWahlvorschlag.value = null;
    lastUsedKandidat.value = null;
  }

  async function _updateLatestUsedData(
    latestUsedData: Kandidat | Wahlvorschlag
  ): Promise<void> {
    lastUsedKandidat.value = null;
    lastUsedWahlvorschlag.value = null;

    // Wait for the next tick so that the components using the refs forget their state,
    // this is important to ensure that an update occurs even with the same values
    await nextTick();

    if ("kandidatId" in latestUsedData) {
      lastUsedKandidat.value = latestUsedData;
      lastUsedWahlvorschlag.value = latestUsedData.owningWahlvorschlag;
    } else {
      lastUsedKandidat.value = null;
      lastUsedWahlvorschlag.value = latestUsedData;
    }
  }

  return {
    changeHistoryInReverseOrder: computed(() =>
      [...changeHistory.value].reverse()
    ),
    lastUsedKandidat,
    lastUsedWahlvorschlag,
    registerKandidatEinzelstimmenAdded,
    registerKandidatEinzelstimmenRemoved,
    registerKandidatEinzelstimmenRangeAdded,
    registerKandidatUngueltigeStimmenAdded,
    registerKandidatUngueltigeStimmenRemoved,
    registerKandidatStreichungSet,
    registerKandidatStreichungUnset,
    registerKandidatStreichungRangeSet,
    registerKandidatStreichungRangeUnset,
    registerWahlvorschlagSelected,
    registerWahlvorschlagDeselected,
    reset,
  };
}
