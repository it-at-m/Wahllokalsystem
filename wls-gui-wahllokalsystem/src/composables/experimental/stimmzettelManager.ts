import type { StimmzettelKandidat } from "@/types/experimental/StimmzettelKandidat.ts";
import type { StimmzettelWahlvorschlag } from "@/types/experimental/StimmzettelWahlvorschlag.ts";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { Ref } from "vue";

import { computed, ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";

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
  const COUNT_VOTES_GIVEN_BY_WAHLVORSCHLAG = 1;
  const logger = useLogging("useStimmzettelManager");
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
            logger.log(`wahlvorschlagID > ${current}`);
            const kandidaten = stimmzettelWahlvorschlaege.value.find(
              (wahlvorschlag) =>
                wahlvorschlag.identifikator === current.identifikator
            )?.kandidaten;
            if (!kandidaten) {
              logger.log(`wahlvorschlag hat keine Kandidaten`);
              return prev;
            } else {
              logger.log(
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
              logger.log(
                `countNonDiscardedKandidaten > ${countNonDiscardedKandidaten}`
              );
              return prev + countNonDiscardedKandidaten;
            }
          }, 0);
    },
    {
      onTrigger: (event) => {
        logger.log(
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
  const isAtLeastOneValidScoreGiven = computed(
    () => totalValidKandidatenScoresByVoter.value > 0
  );
  const isMaxVotesFulfilled = computed(
    () => totalKandidatenScoresByVoter.value <= maxTotalVotes
  );

  const isStimmzettelValid = computed(
    () =>
      isMaxVotesFulfilled.value &&
      isAtLeastOneScoreGiven.value &&
      isAtLeastOneValidScoreGiven.value
  );

  function setWahlvorschlaege(wahlvorschlaege: Wahlvorschlag[]) {
    logger.log(`set wahlvorschlaege > ${JSON.stringify(wahlvorschlaege)}`);
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

    _refreshWahlvorschlaegeVotes();
  }

  function deselectWahlvorschlag(wahlvorschlagId: string) {
    const wahlvorschlag = stimmzettelWahlvorschlaege.value.find(
      (wahlvorschlag) => wahlvorschlag.identifikator === wahlvorschlagId
    );
    if (wahlvorschlag) {
      wahlvorschlag.isSelected = false;

      //alle Wahlvorschlagsstimmen der Kandidaten entfernen
      wahlvorschlag.kandidaten.forEach(
        (kandidat) => (kandidat.votesByWahlvorschlag = 0)
      );
      _refreshWahlvorschlaegeVotes();
    }
  }

  function addKandidatVote(kandidatId: string, countVotes = 1) {
    const kandidat = stimmzettelKandidaten.value.find(
      (kandidat) => kandidat.identifikator === kandidatId
    );
    if (kandidat) {
      setKandidatVote(kandidatId, kandidat.votesByVoter + countVotes);
    }
  }

  function removeKandidatVote(kandidatId: string, countVotes = 1) {
    const kandidat = stimmzettelKandidaten.value.find(
      (kandidat) => kandidat.identifikator === kandidatId
    );
    if (kandidat && kandidat.votesByVoter > 0) {
      setKandidatVote(kandidatId, kandidat.votesByVoter - countVotes);
    }
  }
  function setKandidatVote(kandidatId: string, countVotes: number) {
    const kandidat = stimmzettelKandidaten.value.find(
      (kandidat) => kandidat.identifikator === kandidatId
    );
    if (kandidat) {
      const currentNumberOfVotes = kandidat.votesByVoter;
      if (currentNumberOfVotes != countVotes) {
        kandidat.votesByVoter = countVotes;
        _refreshWahlvorschlaegeVotes();
      }
    }
  }

  function _refreshWahlvorschlaegeVotes() {
    const wahlvorschlaegeSelected = stimmzettelWahlvorschlaege.value.filter(
      (wahlvorschlag) => wahlvorschlag.isSelected
    );
    logger.log(
      `wahlvorschlaegeSelected.length > ${wahlvorschlaegeSelected.length}`
    );
    if (wahlvorschlaegeSelected.length > 0) {
      const totalVotesByUser = stimmzettelWahlvorschlaege.value
        .flatMap((wahlvorschlag) => wahlvorschlag.kandidaten)
        .map((kandidat) => kandidat.votesByVoter)
        .reduce((prev, current) => prev + current, 0);
      const wahlvorschlagVotesToSpent = maxTotalVotes - totalVotesByUser;
      logger.log(`wahlvorschlagVotesToSpent > ${wahlvorschlagVotesToSpent}`);

      if (wahlvorschlaegeSelected.length === 1) {
        const wahlvorschlagToRefresh = selectedWahlvorschlaege.value[0];
        if (wahlvorschlagToRefresh) {
          let wahlvorschlagVotesSpend = 0;
          wahlvorschlagToRefresh.kandidaten.forEach((kandidat) => {
            logger.log(
              `onEach - kandidat.votesByVoter > ${kandidat.votesByVoter}, kandidat.votesByWahlvorschlag > ${kandidat.votesByWahlvorschlag}, wahlvorschlagVotesSpend > ${wahlvorschlagVotesSpend}`
            );
            if (kandidat.votesByVoter > 0 || kandidat.isDiscarded) {
              kandidat.votesByWahlvorschlag = 0;
            }
            if (kandidat.votesByVoter === 0 && !kandidat.isDiscarded) {
              if (wahlvorschlagVotesSpend < wahlvorschlagVotesToSpent) {
                kandidat.votesByWahlvorschlag =
                  COUNT_VOTES_GIVEN_BY_WAHLVORSCHLAG;
                wahlvorschlagVotesSpend += COUNT_VOTES_GIVEN_BY_WAHLVORSCHLAG;
              } else {
                kandidat.votesByWahlvorschlag = 0;
              }
            }
          });
        }
      } else {
        //clear current wahlvorschlaege votes
        wahlvorschlaegeSelected.forEach((wahlvorschlag) => {
          wahlvorschlag.kandidaten.forEach(
            (kandidat) => (kandidat.votesByWahlvorschlag = 0)
          );
        });

        //set new wahlvorschlag votes
        const kandidatenThatCouldGetWahlvorschlagVote = wahlvorschlaegeSelected
          .flatMap((wahlvorschlag) => wahlvorschlag.kandidaten)
          .filter(
            (kandidat) => !kandidat.isDiscarded && kandidat.votesByVoter === 0
          );
        const requiredVotesLeftToFulfilListenkreuze =
          kandidatenThatCouldGetWahlvorschlagVote.length *
          COUNT_VOTES_GIVEN_BY_WAHLVORSCHLAG;
        if (requiredVotesLeftToFulfilListenkreuze > wahlvorschlagVotesToSpent) {
          //reset all set wahlvorschlaege votes
          wahlvorschlaegeSelected.forEach((wahlvorschlag) =>
            wahlvorschlag.kandidaten.forEach(
              (kandidat) => (kandidat.votesByWahlvorschlag = 0)
            )
          );
        } else {
          kandidatenThatCouldGetWahlvorschlagVote.forEach(
            (kandidat) =>
              (kandidat.votesByWahlvorschlag =
                COUNT_VOTES_GIVEN_BY_WAHLVORSCHLAG)
          );
        }
      }
    }
  }

  function discardKandidat(kandidatId: string, newDiscardState = true) {
    const kandidat = stimmzettelKandidaten.value.find(
      (kandidat) => kandidat.identifikator === kandidatId
    );
    if (kandidat) {
      kandidat.isDiscarded = newDiscardState;
      _refreshWahlvorschlaegeVotes();
    }
  }
  function revokeDiscardedKandidat(kandidatId: string) {
    const kandidat = stimmzettelKandidaten.value.find(
      (kandidat) => kandidat.identifikator === kandidatId
    );
    if (kandidat) {
      kandidat.isDiscarded = false;
      _refreshWahlvorschlaegeVotes();
    }
  }

  return {
    addKandidatVote,
    removeKandidatVote,
    setKandidatVote,

    isAtLeastOneScoreGiven,
    isAtLeastOneValidScoreGiven,
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
