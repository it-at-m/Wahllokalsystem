import type { StimmzettelKandidat } from "@/types/experimental/StimmzettelKandidat.ts";
import type { StimmzettelWahlvorschlag } from "@/types/experimental/StimmzettelWahlvorschlag.ts";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
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
  // const selectedWahlvorschlaege: Ref<string[]> = ref([]);
  /**
   * deprecated: internal only
   */
  // const kandidatenVotes: Ref<Record<KandidatId, number>> = ref({});
  // const discardedKandidatenIds: Ref<string[]> = ref([]);
  /**
   * deprecated: old
   */
  // const managedWahlvorschlaege: Ref<Wahlvorschlag[]> = ref([]);

  const stimmzettelWahlvorschlaege: Ref<StimmzettelWahlvorschlag[]> = ref([]);
  const selectedWahlvorschlaege = computed(() =>
    stimmzettelWahlvorschlaege.value.filter(
      (wahlvorschlag) => wahlvorschlag.isSelected
    )
  );

  const stimmzettelKandidaten = computed(() =>
    stimmzettelWahlvorschlaege.value.flatMap(
      (wahlvorschlag) => wahlvorschlag.kandidaten
    )
  );
  const discardedKandidaten = computed(() =>
    stimmzettelKandidaten.value.filter((kandidat) => kandidat.isDiscarded)
  );

  const requiredVotesLeftToFulfilListenkreuze = computed(
    () => {
      return selectedWahlvorschlaege.value.length <= 1
        ? 0
        : selectedWahlvorschlaege.value.reduce((prev, current) => {
            console.log(`wahlvorschlagID > ${current}`);
            const kandidaten = stimmzettelWahlvorschlaege.value.find(
              (wahlvorschlag) =>
                wahlvorschlag.identifikator === current.identifikator
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
                  !discardedKandidaten.value.some(
                    (kid) => kid.identifikator === kandidat.identifikator
                  ) &&
                  stimmzettelKandidaten.value.find(
                    (sk) =>
                      sk.identifikator === kandidat.identifikator &&
                      sk.votesByVoter === 0
                  )
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

  const totalKandidatenScoresByVoter = computed(() =>
    stimmzettelKandidaten.value.reduce(
      (acc, curr) => acc + curr.votesByVoter,
      0
    )
  );
  const totalValidKandidatenScoresByVoter = computed(() =>
    stimmzettelKandidaten.value
      .filter((kandidat) => !kandidat.isDiscarded)
      .map((score) => Math.min(score.votesByVoter, maxValidVotesPerKandidat))
      .reduce((acc, curr) => acc + curr, 0)
  );
  const totalInvalidKandidatenScoresOfDiscardedKandidaten = computed(() =>
    stimmzettelKandidaten.value
      .filter((kandidat) => kandidat.isDiscarded)
      .map((score) => score.votesByVoter)
      .reduce((acc, curr) => acc + curr, 0)
  );
  const totalInvalidKandidatenScoresOfNonDiscardedKandidaten = computed(() =>
    stimmzettelKandidaten.value
      .filter((kandidat) => !kandidat.isDiscarded)
      .filter((score) => score.votesByVoter > maxValidVotesPerKandidat)
      .map((score) => score.votesByVoter - maxValidVotesPerKandidat)
      .reduce((acc, curr) => acc + curr, 0)
  );
  const totalInvalidKandidatenScores = computed(
    () =>
      totalInvalidKandidatenScoresOfDiscardedKandidaten.value +
      totalInvalidKandidatenScoresOfNonDiscardedKandidaten.value
  );

  const isAtLeastOneScoreGiven = computed(
    () => totalKandidatenScoresByVoter.value > 0
  );
  const isMaxVotesFulfilled = computed(
    () => totalKandidatenScoresByVoter.value <= maxTotalVotes
  );

  const isStimmzettelValid = computed(
    () => isMaxVotesFulfilled.value && isAtLeastOneScoreGiven.value
  );

  function setWahlvorschlaege(wahlvorschlaege: Wahlvorschlag[]) {
    console.log(`set wahlvorschlaege > ${JSON.stringify(wahlvorschlaege)}`);
    stimmzettelWahlvorschlaege.value = wahlvorschlaege.map(
      toStimmzettelWahlvorschlag
    );

    //TODO objekte aufräume welche sich auf IDs der Wahlvorschläge beziehen könnten und dann out of sync sein könnten
    //TODO ID in Wahlvorschlag und Kandidat sollten wir fix machen damit syntaktisch klar ist dass man sie nicht ändern kann
  }

  function selectWahlvorschlag(wahlvorschlagId: string) {
    const wahlvorschlag = stimmzettelWahlvorschlaege.value.find(
      (wahlvorschlag) => wahlvorschlag.identifikator === wahlvorschlagId
    );
    if (wahlvorschlag) {
      wahlvorschlag.isSelected = true;
    }
  }

  function deselectWahlvorschlag(wahlvorschlagId: string) {
    const wahlvorschlag = stimmzettelWahlvorschlaege.value.find(
      (wahlvorschlag) => wahlvorschlag.identifikator === wahlvorschlagId
    );
    if (wahlvorschlag) {
      wahlvorschlag.isSelected = false;
    }
  }

  function addKandidatVote(kandidatId: string, countVotes = 1) {
    const kandidat = stimmzettelKandidaten.value.find(
      (kandidat) => kandidat.identifikator === kandidatId
    );
    if (kandidat) {
      kandidat.votesByVoter = kandidat.votesByVoter + countVotes;
    }
  }

  function removeKandidatVote(kandidatId: string, countVotes = 1) {
    const kandidat = stimmzettelKandidaten.value.find(
      (kandidat) => kandidat.identifikator === kandidatId
    );
    if (kandidat && kandidat.votesByVoter > 0) {
      kandidat.votesByVoter = kandidat.votesByVoter - countVotes;
    }
  }
  function setKandidatVote(kandidatId: string, countVotes: number) {
    const kandidat = stimmzettelKandidaten.value.find(
      (kandidat) => kandidat.identifikator === kandidatId
    );
    if (kandidat) {
      kandidat.votesByVoter = countVotes;
    }
  }

  function discardKandidat(kandidatId: string) {
    const kandidat = stimmzettelKandidaten.value.find(
      (kandidat) => kandidat.identifikator === kandidatId
    );
    if (kandidat) {
      kandidat.isDiscarded = true;
    }
  }
  function revokeDiscardedKandidat(kandidatId: string) {
    const kandidat = stimmzettelKandidaten.value.find(
      (kandidat) => kandidat.identifikator === kandidatId
    );
    if (kandidat) {
      kandidat.isDiscarded = false;
    }
  }

  return {
    addKandidatVote,
    removeKandidatVote,
    setKandidatVote,

    isAtLeastOneScoreGiven,
    isStimmzettelValid,
    isMaxVotesFulfilled,

    stimmzettelWahlvorschlaege: computed(
      () => stimmzettelWahlvorschlaege.value
    ),
    stimmzettelKandidaten: computed(() => stimmzettelKandidaten.value),
    selectedWahlvorschlaege: computed(() => selectedWahlvorschlaege.value),
    discardedKandidaten: computed(() => discardedKandidaten.value),
    requiredVotesLeftToFulfilListenkreuze,
    totalKandidatenScores: totalKandidatenScoresByVoter,
    totalValidKandidatenScores: totalValidKandidatenScoresByVoter,
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

function toStimmzettelWahlvorschlag(
  wahlvorschlag: Wahlvorschlag
): StimmzettelWahlvorschlag {
  const result: StimmzettelWahlvorschlag = {
    erhaeltStimmen: wahlvorschlag.erhaeltStimmen,
    kurzname: wahlvorschlag.kurzname,
    ordnungszahl: wahlvorschlag.ordnungszahl,
    identifikator: wahlvorschlag.identifikator,
    isSelected: false,
    kandidaten: [],
  };

  result.kandidaten = wahlvorschlag.kandidaten
    ? wahlvorschlag.kandidaten.map((kandidat) =>
        toStimmzettelKandidat(kandidat, result)
      )
    : [];

  return result;
}

function toStimmzettelKandidat(
  kandidat: Kandidat,
  wahlvorschlag: StimmzettelWahlvorschlag
): StimmzettelKandidat {
  return {
    direktkandidat: kandidat.direktkandidat,
    einzelbewerber: kandidat.einzelbewerber,
    identifikator: kandidat.identifikator,
    isDiscarded: false,
    name: kandidat.name,
    listenposition: kandidat.listenposition,
    votesByVoter: 0,
    votesByWahlvorschlag: 0,
    wahlvorschlag: wahlvorschlag,
  };
}
