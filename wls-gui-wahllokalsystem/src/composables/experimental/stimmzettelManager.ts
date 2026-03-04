import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
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

export function useStimmzettelManager(
  maxValidVotesPerKandidat = 3,
  maxTotalVotes = 20
) {
  const selectedWahlvorschlaege: Ref<string[]> = ref([]);
  /**
   * deprecated: internal only
   */
  const kandidatenVotes: Ref<Record<KandidatId, number>> = ref({});
  const discardedKandidatenIds: Ref<string[]> = ref([]);
  const managedWahlvorschlaege: Ref<Wahlvorschlag[]> = ref([]);

  const kandidatenScores = computed(() =>
    Object.keys(kandidatenVotes.value).map((kandidatId) => ({
      kandidatId,
      votes: kandidatenVotes.value[kandidatId] ?? 0,
    }))
  );
  const requiredVotesLeftToFulfilListenkreuze = computed(
    () => {
      return selectedWahlvorschlaege.value.reduce((prev, current) => {
        console.log(`wahlvorschlagID > ${current}`);
        const kandidaten = managedWahlvorschlaege.value.find(
          (wahlvorschlag) => wahlvorschlag.identifikator === current
        )?.kandidaten;
        if (!kandidaten) {
          console.log(`wahlvorschlag hat keine Kandidaten`);
          return prev;
        } else {
          console.log(
            `wahlvorschlag hat kandidaten - count > ${kandidaten.length}`
          );
          const countNonDiscardedKandidaten = kandidaten.filter(
            (kandidat) =>
              !discardedKandidatenIds.value.some(
                (kid) => kid === kandidat.identifikator
              ) &&
              (kandidatenVotes.value[kandidat.identifikator] === undefined ||
                kandidatenVotes.value[kandidat.identifikator] === 0)
          ).length;
          console.log(
            `countNonDiscardedKandidaten > ${countNonDiscardedKandidaten}`
          );
          return prev + countNonDiscardedKandidaten;
        }
      }, 0);
    },
    {
      onTrigger: (event) => {
        console.log(
          `triggered ${event.type} ${event.newValue} ${event.oldValue}`
        );
      },
    }
  );

  const totalKandidatenScores = computed(() =>
    kandidatenScores.value.reduce((acc, curr) => acc + curr.votes, 0)
  );
  const totalValidKandidatenScores = computed(() =>
    kandidatenScores.value
      .filter(
        (score) => !discardedKandidatenIds.value.includes(score.kandidatId)
      )
      .map((score) => Math.min(score.votes, maxValidVotesPerKandidat))
      .reduce((acc, curr) => acc + curr, 0)
  );
  const totalInvalidKandidatenScoresOfDiscardedKandidaten = computed(() =>
    kandidatenScores.value
      .filter((score) =>
        discardedKandidatenIds.value.includes(score.kandidatId)
      )
      .map((score) => score.votes)
      .reduce((acc, curr) => acc + curr, 0)
  );
  const totalInvalidKandidatenScoresOfNonDiscardedKandidaten = computed(() =>
    kandidatenScores.value
      .filter(
        (score) => !discardedKandidatenIds.value.includes(score.kandidatId)
      )
      .filter((score) => score.votes > maxValidVotesPerKandidat)
      .map((score) => score.votes - maxValidVotesPerKandidat)
      .reduce((acc, curr) => acc + curr, 0)
  );
  const totalInvalidKandidatenScores = computed(
    () =>
      totalInvalidKandidatenScoresOfDiscardedKandidaten.value +
      totalInvalidKandidatenScoresOfNonDiscardedKandidaten.value
  );

  const isAtLeastOneScoreGiven = computed(
    () => totalKandidatenScores.value > 0
  );
  const isMaxVotesFulfilled = computed(
    () => totalKandidatenScores.value <= maxTotalVotes
  );

  const isStimmzettelValid = computed(
    () => isMaxVotesFulfilled.value && isAtLeastOneScoreGiven.value
  );

  function setWahlvorschlaege(wahlvorschlaege: Wahlvorschlag[]) {
    console.log(`set wahlvorschlaege > ${JSON.stringify(wahlvorschlaege)}`);
    managedWahlvorschlaege.value = wahlvorschlaege;
    //TODO objekte aufräume welche sich auf IDs der Wahlvorschläge beziehen könnten und dann out of sync sein könnten
    //TODO ID in Wahlvorschlag und Kandidat sollten wir fix machen damit syntaktisch klar ist dass man sie nicht ändern kann
  }

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
  function setKandidatVote(kandidatId: string, countVotes: number) {
    kandidatenVotes.value[kandidatId] = countVotes;
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
    setKandidatVote,

    isAtLeastOneScoreGiven,
    isStimmzettelValid,
    isMaxVotesFulfilled,

    selectedWahlvorschlaege: computed(() => selectedWahlvorschlaege.value),
    kandidatenVotes: computed(() => kandidatenVotes.value),
    discardedKandidatenIds: computed(() => discardedKandidatenIds.value),
    requiredVotesLeftToFulfilListenkreuze,
    kandidatenScores,
    totalKandidatenScores,
    totalValidKandidatenScores,
    totalInvalidKandidatenScores,

    discardKandidat,
    revokeDiscardedKandidat,

    selectWahlvorschlag,
    deselectWahlvorschlag,

    setWahlvorschlaege,
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
