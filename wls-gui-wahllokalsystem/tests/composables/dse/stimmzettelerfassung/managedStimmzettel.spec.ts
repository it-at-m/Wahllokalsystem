import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeAll, beforeEach, describe, expect, it } from "vitest";
import { ref } from "vue";

import { useManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";
import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";
import { KopfdatenStimmzettelgebietsartEnum } from "@/types/kopfdaten/KopfdatenStimmzettelgebietsartEnum.ts";

describe("managedStimmzettel.ts", () => {
  const mockedWahlId = "wahl-1";
  const {
    prepareManagedStimmzettelStimmzettel,
    prepareManagedStimmzettelWahlvorschlag,
    prepareManagedStimmzettelKandidat,
  } = useManagedStimmzettelTestDataFactory();

  beforeAll(() => {
    setActivePinia(createPinia());
  });

  beforeEach(() => {
    const kdStore = useKopfdatenStore();
    kdStore.kopfdaten = [
      {
        wahlID: mockedWahlId,
        wahlbezirkID: "wb-1",
        gemeinde: "",
        stimmzettelgebietsart: KopfdatenStimmzettelgebietsartEnum.Sb,
        stimmzettelgebietsnummer: "",
        stimmzettelgebietsname: "",
        wahlname: "",
        wahlbezirknummer: "",
        maximalErlaubteStimmenProWaehler: 999,
      },
    ];
  });

  describe("kandidatAddEinzelstimmenOrThrow", () => {
    it("should_useKandidatWithUserVotes_when_multipleCandidatesShareListenposition", () => {
      const kandidatWithoutVotes = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();
      const kandidatWithVotes = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(2)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidatWithoutVotes, kandidatWithVotes])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.kandidatAddEinzelstimmenOrThrow(101, 1);

      expect(kandidatWithVotes.einzelstimmen).toBe(3);
      expect(kandidatWithoutVotes.einzelstimmen).toBeNull();
    });

    it("should_useFirstNotDiscardedCandidate_when_noUserVotesPresent", () => {
      const discardedKandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(true)
        .build();
      const kandidatToUse = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([discardedKandidat, kandidatToUse])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.kandidatAddEinzelstimmenOrThrow(101, 1);

      expect(kandidatToUse.einzelstimmen).toBe(1);
      expect(discardedKandidat.einzelstimmen).toBeNull();
    });

    it("should_useFirstCandidate_when_allCandidatesAreDiscarded", () => {
      const firstDiscarded = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(true)
        .build();
      const secondDiscarded = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(true)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([firstDiscarded, secondDiscarded])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.kandidatAddEinzelstimmenOrThrow(101, 1);

      expect(firstDiscarded.einzelstimmen).toBe(1);
      expect(secondDiscarded.einzelstimmen).toBeNull();
    });

    it("should_throwManagedStimmzettelError_when_noKandidatForGivenOrdnungszahl", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() => managed.kandidatAddEinzelstimmenOrThrow(101, 1)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_throwManagedStimmzettelError_when_votesToAddIsNaN", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() =>
        managed.kandidatAddEinzelstimmenOrThrow(101, Number.NaN)
      ).toThrow(ManagedStimmzettelError);
    });

    it("should_throwManagedStimmzettelError_when_votesToAddIsNegativeInfinity", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() =>
        managed.kandidatAddEinzelstimmenOrThrow(101, Number.NEGATIVE_INFINITY)
      ).toThrow(ManagedStimmzettelError);
    });

    it("should_throwManagedStimmzettelError_when_votesToAddIsPositiveInfinity", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() =>
        managed.kandidatAddEinzelstimmenOrThrow(101, Number.POSITIVE_INFINITY)
      ).toThrow(ManagedStimmzettelError);
    });

    it("should_throwManagedStimmzettelError_when_votesToAddIsFractionalValue", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() => managed.kandidatAddEinzelstimmenOrThrow(101, 0.1)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_incrementVotes_when_addVotesMultipleTimes", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(1)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidat])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.kandidatAddEinzelstimmenOrThrow(101, 2);
      managed.kandidatAddEinzelstimmenOrThrow(101, 3);

      expect(kandidat.einzelstimmen).toBe(6);
    });

    it("should_useKandidatFromSecondWahlvorschlag_when_ordnungszahlPointsToIt", () => {
      const kandidatInFirstWahlvorschlag = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();
      const kandidatInSecondWahlvorschlag = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(201)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidatInFirstWahlvorschlag])
            .build(),
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(2)
            .kandidaten([kandidatInSecondWahlvorschlag])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.kandidatAddEinzelstimmenOrThrow(201, 1);

      expect(kandidatInSecondWahlvorschlag.einzelstimmen).toBe(1);
      expect(kandidatInFirstWahlvorschlag.einzelstimmen).toBeNull();
    });

    it("should_throwManagedStimmzettelError_when_listenpositionHasNoCandidate", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidat])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() => managed.kandidatAddEinzelstimmenOrThrow(199, 1)).toThrow(
        ManagedStimmzettelError
      );
    });
  });

  describe("kandidatRemoveEinzelstimmenOrThrow", () => {
    it("should_throwManagedStimmzettelError_when_noKandidatForGivenOrdnungszahl", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() => managed.kandidatRemoveEinzelstimmenOrThrow(101, 1)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_throwManagedStimmzettelError_when_votesToRemoveIsNaNOrInfiniteOrFractional", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() =>
        managed.kandidatRemoveEinzelstimmenOrThrow(101, Number.NaN)
      ).toThrow(ManagedStimmzettelError);
      expect(() =>
        managed.kandidatRemoveEinzelstimmenOrThrow(
          101,
          Number.NEGATIVE_INFINITY
        )
      ).toThrow(ManagedStimmzettelError);
      expect(() =>
        managed.kandidatRemoveEinzelstimmenOrThrow(
          101,
          Number.POSITIVE_INFINITY
        )
      ).toThrow(ManagedStimmzettelError);
      expect(() =>
        managed.kandidatRemoveEinzelstimmenOrThrow(101, 0.1)
      ).toThrow(ManagedStimmzettelError);
    });

    it("should_throwManagedStimmzettelError_when_votesToRemoveExceedsExistingEinzelstimmen", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(2)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidat])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() => managed.kandidatRemoveEinzelstimmenOrThrow(101, 3)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_decrementEinzelstimmenAndUpdateSummary_when_validRemoval", () => {
      const kdStore = useKopfdatenStore();
      kdStore.kopfdaten = [
        {
          wahlID: mockedWahlId,
          wahlbezirkID: "wb-1",
          gemeinde: "",
          stimmzettelgebietsart: KopfdatenStimmzettelgebietsartEnum.Sb,
          stimmzettelgebietsnummer: "",
          stimmzettelgebietsname: "",
          wahlname: "",
          wahlbezirknummer: "",
          maximalErlaubteStimmenProWaehler: 2,
        },
      ];

      const kandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(2)
        .ungueltigeStimmen(0)
        .reststimmen(0)
        .durchgestrichen(false)
        .build();
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .ordnungszahl(1)
        .selected(true)
        .kandidaten([kandidat])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.kandidatRemoveEinzelstimmenOrThrow(101, 1);

      expect(kandidat.einzelstimmen).toBe(1);
      expect(managed.stimmenSummary.value.einzelstimmen).toBe(1);
    });

    it("should_reassignReststimmen_when_votesRemovedAndRemainingVotesBecomePositive", () => {
      const kdStore = useKopfdatenStore();
      kdStore.kopfdaten = [
        {
          wahlID: mockedWahlId,
          wahlbezirkID: "wb-1",
          gemeinde: "",
          stimmzettelgebietsart: KopfdatenStimmzettelgebietsartEnum.Sb,
          stimmzettelgebietsnummer: "",
          stimmzettelgebietsname: "",
          wahlname: "",
          wahlbezirknummer: "",
          maximalErlaubteStimmenProWaehler: 2,
        },
      ];

      const k1 = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(1)
        .ungueltigeStimmen(0)
        .reststimmen(0)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(102)
        .einzelstimmen(1)
        .ungueltigeStimmen(0)
        .reststimmen(0)
        .durchgestrichen(false)
        .build();
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .ordnungszahl(1)
        .selected(true)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.kandidatRemoveEinzelstimmenOrThrow(101, 1);

      expect(k1.einzelstimmen).toBe(0);
      expect(k1.reststimmen).toBe(1);
      expect(managed.stimmenSummary.value.reststimmen).toBe(1);
    });
  });

  describe("kandidatAddUngueltigeStimmenOrThrow", () => {
    it("should_throwManagedStimmzettelError_when_noKandidatForGivenOrdnungszahl", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() => managed.kandidatAddUngueltigeStimmenOrThrow(101, 1)).toThrow(
        ManagedStimmzettelError
      );
    });
  });

  describe("kandidatRemoveUngueltigeStimmenOrThrow", () => {
    it("should_throwManagedStimmzettelError_when_noKandidatForGivenOrdnungszahl", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() =>
        managed.kandidatRemoveUngueltigeStimmenOrThrow(101, 1)
      ).toThrow(ManagedStimmzettelError);
    });

    it("should_decrementUngueltigeStimmen_when_validRemoval", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(0)
        .ungueltigeStimmen(3)
        .reststimmen(0)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidat])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.kandidatRemoveUngueltigeStimmenOrThrow(101, 2);

      expect(kandidat.ungueltigeStimmen).toBe(1);
      expect(managed.stimmenSummary.value.ungueltigeStimmen).toBe(1);
    });
  });

  describe("kandidatenAddStimmenInRangeOrThrow", () => {
    it("should_addVotesToAllCandidatesInRange_when_rangeIsComplete", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(102)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();
      const k3 = prepareManagedStimmzettelKandidat()
        .listenposition(3)
        .ordnungszahl(103)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([k1, k2, k3])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.kandidatenAddStimmenInRangeOrThrow(101, 103, 2);

      expect(k1.einzelstimmen).toBe(2);
      expect(k2.einzelstimmen).toBe(2);
      expect(k3.einzelstimmen).toBe(2);
    });

    it("should_throwManagedStimmzettelError_when_anyOrdnungszahlInRangeIsMissing", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();
      const k3 = prepareManagedStimmzettelKandidat()
        .listenposition(3)
        .ordnungszahl(103)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([k1, k3])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() =>
        managed.kandidatenAddStimmenInRangeOrThrow(101, 103, 1)
      ).toThrow(ManagedStimmzettelError);
    });

    it("should_throwManagedStimmzettelError_when_rangeContainsAnyStreichung", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(102)
        .einzelstimmen(null)
        .durchgestrichen(true)
        .build();
      const k3 = prepareManagedStimmzettelKandidat()
        .listenposition(3)
        .ordnungszahl(103)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([k1, k2, k3])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() =>
        managed.kandidatenAddStimmenInRangeOrThrow(101, 103, 1)
      ).toThrow(ManagedStimmzettelError);
    });

    it("should_addAllVotesAcrossRange_evenIfRequestedExceedsRemainingVotes", () => {
      const kdStore = useKopfdatenStore();
      kdStore.kopfdaten = [
        {
          wahlID: mockedWahlId,
          wahlbezirkID: "wb-1",
          gemeinde: "",
          stimmzettelgebietsart: KopfdatenStimmzettelgebietsartEnum.Sb,
          stimmzettelgebietsnummer: "",
          stimmzettelgebietsname: "",
          wahlname: "",
          wahlbezirknummer: "",
          maximalErlaubteStimmenProWaehler: 3,
        },
      ];

      const k1 = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(0)
        .ungueltigeStimmen(0)
        .reststimmen(0)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(102)
        .einzelstimmen(0)
        .ungueltigeStimmen(0)
        .reststimmen(0)
        .durchgestrichen(false)
        .build();
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .ordnungszahl(1)
        .selected(false)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.kandidatenAddStimmenInRangeOrThrow(101, 102, 2);

      expect(k1.einzelstimmen).toBe(2);
      expect(k2.einzelstimmen).toBe(2);
      expect(managed.stimmenSummary.value.einzelstimmen).toBe(4);
    });

    it("should_addAllVotesToSingleKandidat_evenIfRequestedExceedsRemainingVotes", () => {
      const kdStore = useKopfdatenStore();
      kdStore.kopfdaten = [
        {
          wahlID: mockedWahlId,
          wahlbezirkID: "wb-1",
          gemeinde: "",
          stimmzettelgebietsart: KopfdatenStimmzettelgebietsartEnum.Sb,
          stimmzettelgebietsnummer: "",
          stimmzettelgebietsname: "",
          wahlname: "",
          wahlbezirknummer: "",
          maximalErlaubteStimmenProWaehler: 3,
        },
      ];

      const kandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(0)
        .ungueltigeStimmen(0)
        .reststimmen(0)
        .durchgestrichen(false)
        .build();
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .ordnungszahl(1)
        .selected(false)
        .kandidaten([kandidat])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      managed.kandidatAddEinzelstimmenOrThrow(101, 1);
      managed.kandidatAddEinzelstimmenOrThrow(101, 5);

      expect(kandidat.einzelstimmen).toBe(6);
      expect(managed.stimmenSummary.value.einzelstimmen).toBe(6);
    });
  });

  describe("kandidatAddStreichungOrThrow", () => {
    it("should_setDurchgestrichenToTrue_when_kandidatExistsForOrdnungszahl", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidat])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.kandidatAddStreichungOrThrow(101);

      expect(kandidat.durchgestrichen).toBe(true);
    });

    it("should_throwManagedStimmzettelError_when_noKandidatForGivenOrdnungszahl", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() => managed.kandidatAddStreichungOrThrow(101)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_preferKandidatWithoutEinzelstimmen_when_multipleCandidatesShareListenposition", () => {
      const kandidatWithVotes = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(1)
        .durchgestrichen(false)
        .build();
      const kandidatWithoutVotes = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidatWithVotes, kandidatWithoutVotes])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      managed.kandidatAddStreichungOrThrow(101);

      expect(kandidatWithoutVotes.durchgestrichen).toBe(true);
      expect(kandidatWithVotes.durchgestrichen).toBe(false);
    });
  });

  describe("kandidatenStreichungenInRangeOrThrow", () => {
    it("should_setDurchgestrichenTrueForAllKandidatenInRange_when_rangeIsComplete", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(102)
        .durchgestrichen(false)
        .build();
      const k3 = prepareManagedStimmzettelKandidat()
        .listenposition(3)
        .ordnungszahl(103)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([k1, k2, k3])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.kandidatenStreichungenInRangeOrThrow(101, 103);

      expect(k1.durchgestrichen).toBe(true);
      expect(k2.durchgestrichen).toBe(true);
      expect(k3.durchgestrichen).toBe(true);
    });

    it("should_throwManagedStimmzettelError_when_anyOrdnungszahlInRangeIsMissing", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .durchgestrichen(false)
        .build();
      const k3 = prepareManagedStimmzettelKandidat()
        .listenposition(3)
        .ordnungszahl(103)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([k1, k3])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() =>
        managed.kandidatenStreichungenInRangeOrThrow(101, 103)
      ).toThrow(ManagedStimmzettelError);
    });
  });

  describe("kandidatenRemoveStreichungenInRangeOrThrow", () => {
    it("should_unsetDurchgestrichenForAllKandidatenInRange_when_anyIsDiscarded", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .durchgestrichen(true)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(102)
        .durchgestrichen(false)
        .build();
      const k3 = prepareManagedStimmzettelKandidat()
        .listenposition(3)
        .ordnungszahl(103)
        .durchgestrichen(true)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([k1, k2, k3])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.kandidatenRemoveStreichungenInRangeOrThrow(101, 103);

      expect(k1.durchgestrichen).toBe(false);
      expect(k2.durchgestrichen).toBe(false);
      expect(k3.durchgestrichen).toBe(false);
    });

    it("should_throwManagedStimmzettelError_when_allCandidatesInRangeAlreadyHaveNoStreichung", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(102)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([k1, k2])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() =>
        managed.kandidatenRemoveStreichungenInRangeOrThrow(101, 102)
      ).toThrow(ManagedStimmzettelError);
    });
  });

  describe("wahlvorschlagAddVotesOrThrow", () => {
    it("should_throwManagedStimmzettelError_when_wahlvorschlagNotFound", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() => managed.wahlvorschlagAddVotesOrThrow(1)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_assignReststimmen_toFirstEligibleCandidates_when_wahlvorschlagSelected", () => {
      const kdStore = useKopfdatenStore();
      kdStore.kopfdaten = [
        {
          wahlID: mockedWahlId,
          wahlbezirkID: "wb-1",
          gemeinde: "",
          stimmzettelgebietsart: KopfdatenStimmzettelgebietsartEnum.Sb,
          stimmzettelgebietsnummer: "",
          stimmzettelgebietsname: "",
          wahlname: "",
          wahlbezirknummer: "",
          maximalErlaubteStimmenProWaehler: 2,
        },
      ];

      const k1 = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .ungueltigeStimmen(0)
        .reststimmen(null)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(102)
        .einzelstimmen(null)
        .ungueltigeStimmen(0)
        .reststimmen(null)
        .durchgestrichen(false)
        .build();
      const k3 = prepareManagedStimmzettelKandidat()
        .listenposition(3)
        .ordnungszahl(103)
        .einzelstimmen(null)
        .ungueltigeStimmen(0)
        .reststimmen(null)
        .durchgestrichen(false)
        .build();
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .ordnungszahl(1)
        .selected(false)
        .kandidaten([k1, k2, k3])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      managed.wahlvorschlagAddVotesOrThrow(1);

      expect(k1.reststimmen).toBe(1);
      expect(k2.reststimmen).toBe(1);
      expect(k3.reststimmen).toBeNull();
      expect(wv.selected).toBe(true);
    });

    it("should_removeLastAssignedReststimmen_when_directVotesExceedRemaining_onSingleCandidate", () => {
      const kdStore = useKopfdatenStore();
      kdStore.kopfdaten = [
        {
          wahlID: mockedWahlId,
          wahlbezirkID: "wb-1",
          gemeinde: "",
          stimmzettelgebietsart: KopfdatenStimmzettelgebietsartEnum.Sb,
          stimmzettelgebietsnummer: "",
          stimmzettelgebietsname: "",
          wahlname: "",
          wahlbezirknummer: "",
          maximalErlaubteStimmenProWaehler: 2,
        },
      ];

      const k1 = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .ungueltigeStimmen(0)
        .reststimmen(null)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(102)
        .einzelstimmen(null)
        .ungueltigeStimmen(0)
        .reststimmen(null)
        .durchgestrichen(false)
        .build();
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .ordnungszahl(1)
        .selected(false)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      managed.wahlvorschlagAddVotesOrThrow(1);

      managed.kandidatAddEinzelstimmenOrThrow(101, 1);

      expect(k1.einzelstimmen).toBe(1);
      expect(k1.reststimmen).toBe(1);
      expect(k2.reststimmen).toBe(0);
      expect(managed.stimmenSummary.value.einzelstimmen).toBe(1);
      expect(managed.stimmenSummary.value.reststimmen).toBe(1);
    });

    it("should_removeAllAssignedReststimmen_when_rangeVotesExceedRemaining_onTwoCandidates", () => {
      const kdStore = useKopfdatenStore();
      kdStore.kopfdaten = [
        {
          wahlID: mockedWahlId,
          wahlbezirkID: "wb-1",
          gemeinde: "",
          stimmzettelgebietsart: KopfdatenStimmzettelgebietsartEnum.Sb,
          stimmzettelgebietsnummer: "",
          stimmzettelgebietsname: "",
          wahlname: "",
          wahlbezirknummer: "",
          maximalErlaubteStimmenProWaehler: 2,
        },
      ];

      const k1 = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .ungueltigeStimmen(0)
        .reststimmen(null)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(102)
        .einzelstimmen(null)
        .ungueltigeStimmen(0)
        .reststimmen(null)
        .durchgestrichen(false)
        .build();
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .ordnungszahl(1)
        .selected(false)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      managed.wahlvorschlagAddVotesOrThrow(1);

      managed.kandidatenAddStimmenInRangeOrThrow(101, 102, 1);

      expect(k1.einzelstimmen).toBe(1);
      expect(k2.einzelstimmen).toBe(1);
      expect(k1.reststimmen).toBe(0);
      expect(k2.reststimmen).toBe(0);
      expect(managed.stimmenSummary.value.einzelstimmen).toBe(2);
      expect(managed.stimmenSummary.value.reststimmen).toBe(0);
    });
  });

  describe("wahlvorschlagRemoveVotesOrThrow", () => {
    it("should_throwManagedStimmzettelError_when_wahlvorschlagNotFound", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(() => managed.wahlvorschlagRemoveVotesOrThrow(1)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_clearAllReststimmenAndDeselect_when_wahlvorschlagWasSelected", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .reststimmen(1)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(102)
        .reststimmen(1)
        .durchgestrichen(false)
        .build();
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .ordnungszahl(1)
        .selected(true)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.wahlvorschlagRemoveVotesOrThrow(1);

      expect(k1.reststimmen).toBe(0);
      expect(k2.reststimmen).toBe(0);
      expect(wv.selected).toBe(false);
      expect(managed.stimmenSummary.value.reststimmen).toBe(0);
    });
  });

  describe("resetStimmzettel", () => {
    it("should_clearHistoryAndResetAllKandidatenAndWahlvorschlaege_when_called", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(2)
        .ungueltigeStimmen(3)
        .reststimmen(1)
        .durchgestrichen(true)
        .build();

      const wv = prepareManagedStimmzettelWahlvorschlag()
        .ordnungszahl(1)
        .selected(true)
        .kandidaten([kandidat])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.kandidatAddUngueltigeStimmenOrThrow(101, 1);

      expect(managed.changeHistoryInReverseOrder.value.length).toBeGreaterThan(
        0
      );

      managed.resetStimmzettel();

      expect(kandidat.einzelstimmen).toBeNull();
      expect(kandidat.ungueltigeStimmen).toBeNull();
      expect(kandidat.reststimmen).toBeNull();
      expect(kandidat.durchgestrichen).toBe(false);

      expect(wv.selected).toBe(false);

      expect(managed.changeHistoryInReverseOrder.value.length).toBe(0);

      expect(managed.stimmenSummary.value.einzelstimmen).toBe(0);
      expect(managed.stimmenSummary.value.ungueltigeStimmen).toBe(0);
      expect(managed.stimmenSummary.value.reststimmen).toBe(0);
      expect(managed.stimmenSummary.value.streichungen).toBe(0);
    });

    it("should_resetAssignedReststimmenToNullAndDeselect_when_wahlvorschlagWasSelected", () => {
      const kdStore = useKopfdatenStore();
      kdStore.kopfdaten = [
        {
          wahlID: mockedWahlId,
          wahlbezirkID: "wb-1",
          gemeinde: "",
          stimmzettelgebietsart: KopfdatenStimmzettelgebietsartEnum.Sb,
          stimmzettelgebietsnummer: "",
          stimmzettelgebietsname: "",
          wahlname: "",
          wahlbezirknummer: "",
          maximalErlaubteStimmenProWaehler: 2,
        },
      ];

      const k1 = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .ungueltigeStimmen(0)
        .reststimmen(null)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(102)
        .einzelstimmen(null)
        .ungueltigeStimmen(0)
        .reststimmen(null)
        .durchgestrichen(false)
        .build();
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .ordnungszahl(1)
        .selected(false)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      managed.wahlvorschlagAddVotesOrThrow(1);

      expect(wv.selected).toBe(true);
      expect(k1.reststimmen).toBe(1);
      expect(k2.reststimmen).toBe(1);

      managed.resetStimmzettel();

      expect(wv.selected).toBe(false);
      expect(k1.reststimmen).toBeNull();
      expect(k2.reststimmen).toBeNull();
    });
  });
});
