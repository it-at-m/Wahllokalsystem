import type { Ref } from "vue";

import { computed, ref } from "vue";

type StimmzettelManagerHash = string;
type KandidatId = string;

interface WahlIdAndWahlbezirkId {
  wahlId: string;
  wahlbezirkId: string;
}

const managers = new Map<
  StimmzettelManagerHash,
  ReturnType<typeof useStimmzettelManager>
>();

export function getStimmzettelManger(
  wahlIdAndWahlbezirkId: WahlIdAndWahlbezirkId
) {
  const manager = managers.get(hashObject(wahlIdAndWahlbezirkId));
  if (manager) {
    return manager;
  } else {
    const newManager = useStimmzettelManager();
    managers.set(hashObject(wahlIdAndWahlbezirkId), newManager);
    return newManager;
  }
}

export function useStimmzettelManager(maxValidVotesPerKandidat = 3) {
  const selectedWahlvorschlaege: Ref<string[]> = ref([]);
  /**
   * deprecated: internal only
   */
  const kandidatenVotes: Ref<Record<KandidatId, number>> = ref({});
  const discardedKandidatenIds: Ref<string[]> = ref([]);

  const kandidatenScores = computed(() =>
    Object.keys(kandidatenVotes.value).map((kandidatId) => ({
      kandidatId,
      votes: kandidatenVotes.value[kandidatId] ?? 0,
    }))
  );
  const requiredVotesLeftToFulfilListenkreuze = computed(() => {
    return 0;
  });

  const totalKandidatenScores = computed(() =>
    kandidatenScores.value.reduce((acc, curr) => acc + curr.votes, 0)
  );
  const totalValidKandidatenScores = computed(() =>
    kandidatenScores.value
      .map((score) => Math.min(score.votes, maxValidVotesPerKandidat))
      .reduce((acc, curr) => acc + curr, 0)
  );

  function selectWahlvorschlag(wahlvorschlagId: string) {
    if (!selectedWahlvorschlaege.value.some((id) => id === wahlvorschlagId)) {
      selectedWahlvorschlaege.value.push(wahlvorschlagId);
    }
  }

  function deselectWahlvorschlag(wahlvorschlagId: string) {
    selectedWahlvorschlaege.value = selectedWahlvorschlaege.value.filter(
      (id) => id !== wahlvorschlagId
    );
  }

  function addKandidatVote(kandidatId: string) {
    const currentVotes = kandidatenVotes.value[kandidatId] || 0;
    kandidatenVotes.value[kandidatId] = currentVotes + 1;
  }

  function removeKandidatVote(kandidatId: string) {
    const currentVotes = kandidatenVotes.value[kandidatId] || 0;
    if (currentVotes > 0) {
      kandidatenVotes.value[kandidatId] = currentVotes - 1;
    }
  }

  function discardKandidat(kandidatId: string) {
    if (!discardedKandidatenIds.value.some((id) => id === kandidatId)) {
      discardedKandidatenIds.value.push(kandidatId);
    }
  }
  function revokeDiscardedKandidat(kandidatId: string) {
    discardedKandidatenIds.value = discardedKandidatenIds.value.filter(
      (id) => id !== kandidatId
    );
  }

  return {
    addKandidatVote,
    removeKandidatVote,
    selectedWahlvorschlaege: computed(() => selectedWahlvorschlaege.value),
    kandidatenVotes: computed(() => kandidatenVotes.value),
    discardedKandidatenIds: computed(() => discardedKandidatenIds.value),
    requiredVotesLeftToFulfilListenkreuze,
    kandidatenScores,
    totalKandidatenScores,
    totalValidKandidatenScores,

    discardKandidat,
    revokeDiscardedKandidat,

    selectWahlvorschlag,
    deselectWahlvorschlag,
  };
}

function hashObject<T extends object>(obj: T): string {
  const sortedKeys = Object.keys(obj).sort() as (keyof T)[];
  const keyValuePairs = sortedKeys.map((key) => {
    const value = obj[key];
    return `${String(key)}:${serializeValue(value)}`;
  });
  return keyValuePairs.join("|");
}

function serializeValue(value: unknown): string {
  if (value === null) return "null";
  if (typeof value === "object") return hashObject(value as object);
  return String(value);
}
