import type { InputHistoryItem } from "@/types/dse/stimmzettelerfassung/InputHistoryItem.ts";
import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";

import { computed, nextTick, ref } from "vue";

import { InputHistoryTypeEnum } from "@/types/dse/stimmzettelerfassung/InputHistoryTypeEnum.ts";

export function useStimmzettelChangeHistory() {
  const changeHistory = ref<InputHistoryItem[]>([]);
  const lastUsedWahlvorschlag = ref<Wahlvorschlag | null>(null);
  const lastUsedKandidat = ref<Kandidat | null>(null);

  function registerKandidatEinzelstimmenAdded(
    kandidat: Kandidat,
    count: number
  ) {
    changeHistory.value.push({
      type: InputHistoryTypeEnum.ADD_USER_VOTE,
      text: [
        `${kandidat.ordnungszahl}${" + " + count + (count > 1 ? " Stimmen" : " Stimme")}`,
        kandidat.name,
      ],
    });

    _updateLatestUsedData(kandidat);
  }

  function registerKandidatEinzelstimmenRangeSet(
    kandidaten: Kandidat[],
    count: number
  ) {
    const firstKandidat = kandidaten[0];
    const lastKandidat = kandidaten[kandidaten.length - 1];
    changeHistory.value.push({
      type: InputHistoryTypeEnum.VOTE_RANGE,
      text: [
        `${firstKandidat.ordnungszahl}-${lastKandidat.ordnungszahl}${" + " + count + (count > 1 ? " Stimmen" : " Stimme")}`,
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
        `${kandidat.ordnungszahl}${" + " + count + " ungültige " + (count > 1 ? "Stimmen" : "Stimme")}`,
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

  function registerKandidatStreichungRangeSet(kandidaten: Kandidat[]) {
    const firstKandidat = kandidaten[0];
    const lastKandidat = kandidaten[kandidaten.length - 1];
    changeHistory.value.push({
      type: InputHistoryTypeEnum.DISCARD_RANGE,
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

  async function _updateLatestUsedData(kandidat: Kandidat): Promise<void>;
  async function _updateLatestUsedData(
    wahlvorschlag: Wahlvorschlag
  ): Promise<void>;
  async function _updateLatestUsedData(
    latestUsedData: Kandidat | Wahlvorschlag
  ): Promise<void> {
    lastUsedKandidat.value = null;
    lastUsedWahlvorschlag.value = null;

    //Auf nächsten Tick warten, damit die Komponenten, welche die refs verwenden, ihren Zustand vergessen,
    //das ist wichtig, damit auch bei gleichen Werten ggf. eine Aktualisierung erfolgt
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
    registerKandidatEinzelstimmenRangeSet,
    registerKandidatUngueltigeStimmenAdded,
    registerKandidatStreichungSet,
    registerKandidatStreichungRangeSet,
    registerWahlvorschlagSelected,
  };
}
