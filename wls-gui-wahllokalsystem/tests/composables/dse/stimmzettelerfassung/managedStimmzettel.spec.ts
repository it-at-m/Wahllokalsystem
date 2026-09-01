import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import {
  afterEach,
  beforeAll,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { ref } from "vue";

import { useManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";
import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";
import { KopfdatenStimmzettelgebietsartEnum } from "@/types/kopfdaten/KopfdatenStimmzettelgebietsartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  registerKandidatEinzelstimmenAdded: vi.fn(),
  registerKandidatEinzelstimmenRangeSet: vi.fn(),
  registerKandidatUngueltigeStimmenAdded: vi.fn(),
  registerKandidatStreichungSet: vi.fn(),
  registerKandidatStreichungRangeSet: vi.fn(),
  registerWahlvorschlagSelected: vi.fn(),
}));

vi.mock(
  import("@/composables/dse/stimmzettelerfassung/stimmzettelChangeHistory.ts"),
  async (importOriginal) => {
    const original = await importOriginal();

    return {
      useStimmzettelChangeHistory: () => ({
        ...original.useStimmzettelChangeHistory(),
        registerKandidatEinzelstimmenAdded:
          mockDefinitions.registerKandidatEinzelstimmenAdded,
        registerKandidatEinzelstimmenRangeSet:
          mockDefinitions.registerKandidatEinzelstimmenRangeSet,
        registerKandidatUngueltigeStimmenAdded:
          mockDefinitions.registerKandidatUngueltigeStimmenAdded,
        registerKandidatStreichungSet:
          mockDefinitions.registerKandidatStreichungSet,
        registerKandidatStreichungRangeSet:
          mockDefinitions.registerKandidatStreichungRangeSet,
        registerWahlvorschlagSelected:
          mockDefinitions.registerWahlvorschlagSelected,
      }),
    };
  }
);

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

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
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

      const countVotes = 1;
      managed.kandidatAddEinzelstimmenOrThrow(101, countVotes);

      expect(kandidatWithVotes.einzelstimmen).toBe(3);
      expect(kandidatWithoutVotes.einzelstimmen).toBeNull();
      expect(
        mockDefinitions.registerKandidatEinzelstimmenAdded
      ).toHaveBeenCalledExactlyOnceWith(kandidatWithVotes, countVotes);
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

      const countVotes = 1;
      managed.kandidatAddEinzelstimmenOrThrow(101, countVotes);

      expect(kandidatToUse.einzelstimmen).toBe(1);
      expect(discardedKandidat.einzelstimmen).toBeNull();
      expect(
        mockDefinitions.registerKandidatEinzelstimmenAdded
      ).toHaveBeenCalledExactlyOnceWith(kandidatToUse, countVotes);
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

      const countVotes = 1;
      managed.kandidatAddEinzelstimmenOrThrow(101, 1);

      expect(firstDiscarded.einzelstimmen).toBe(1);
      expect(secondDiscarded.einzelstimmen).toBeNull();
      expect(
        mockDefinitions.registerKandidatEinzelstimmenAdded
      ).toHaveBeenCalledExactlyOnceWith(firstDiscarded, countVotes);
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
      expect(
        mockDefinitions.registerKandidatEinzelstimmenAdded.mock.calls
      ).toStrictEqual([
        [kandidat, 2],
        [kandidat, 3],
      ]);
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

      const countVotes = 1;
      managed.kandidatAddEinzelstimmenOrThrow(201, countVotes);

      expect(kandidatInSecondWahlvorschlag.einzelstimmen).toBe(1);
      expect(kandidatInFirstWahlvorschlag.einzelstimmen).toBeNull();
      expect(
        mockDefinitions.registerKandidatEinzelstimmenAdded
      ).toHaveBeenCalledExactlyOnceWith(
        kandidatInSecondWahlvorschlag,
        countVotes
      );
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

  describe("kandidatAddUngueltigeStimmenOrThrow", () => {
    //TODO Issue
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

      const countVotes = 2;
      managed.kandidatenAddStimmenInRangeOrThrow(101, 103, countVotes);

      expect(k1.einzelstimmen).toBe(2);
      expect(k2.einzelstimmen).toBe(2);
      expect(k3.einzelstimmen).toBe(2);
      expect(
        mockDefinitions.registerKandidatEinzelstimmenRangeSet
      ).toHaveBeenCalledExactlyOnceWith([k1, k2, k3], countVotes);
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
        .gueltigeStimmen(0)
        .ungueltigeStimmen(0)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      const countVotes = 2;
      managed.kandidatenAddStimmenInRangeOrThrow(101, 102, countVotes);

      expect(k1.einzelstimmen).toBe(2);
      expect(k2.einzelstimmen).toBe(2);
      expect(managed.stimmenSummary.value.einzelstimmen).toBe(4);
      expect(wv.gueltigeStimmen).toBe(4);
      expect(
        mockDefinitions.registerKandidatEinzelstimmenRangeSet
      ).toHaveBeenCalledExactlyOnceWith([k1, k2], countVotes);
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
        .gueltigeStimmen(0)
        .ungueltigeStimmen(0)
        .kandidaten([kandidat])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      //TODO warum ist das in der Testsuit
      managed.kandidatAddEinzelstimmenOrThrow(101, 1);
      managed.kandidatAddEinzelstimmenOrThrow(101, 5);

      expect(kandidat.einzelstimmen).toBe(6);
      expect(managed.stimmenSummary.value.einzelstimmen).toBe(6);
      expect(wv.gueltigeStimmen).toBe(6);
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
      expect(
        mockDefinitions.registerKandidatStreichungSet
      ).toHaveBeenCalledExactlyOnceWith(kandidat);
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
      expect(
        mockDefinitions.registerKandidatStreichungSet
      ).toHaveBeenCalledExactlyOnceWith(kandidatWithoutVotes);
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
      expect(
        mockDefinitions.registerKandidatStreichungRangeSet
      ).toHaveBeenCalledExactlyOnceWith([k1, k2, k3]);
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
        .gueltigeStimmen(0)
        .ungueltigeStimmen(0)
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
      expect(
        mockDefinitions.registerWahlvorschlagSelected
      ).toHaveBeenCalledExactlyOnceWith(wv);
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
        .gueltigeStimmen(0)
        .ungueltigeStimmen(0)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      managed.wahlvorschlagAddVotesOrThrow(1);

      // Add a direct vote to k1 which would exceed remaining -> should remove last assigned reststimme (k2)
      managed.kandidatAddEinzelstimmenOrThrow(101, 1);

      expect(k1.einzelstimmen).toBe(1);
      expect(k1.reststimmen).toBe(1);
      expect(k2.reststimmen).toBe(0);
      expect(managed.stimmenSummary.value.einzelstimmen).toBe(1);
      expect(managed.stimmenSummary.value.reststimmen).toBe(1);
      expect(
        mockDefinitions.registerWahlvorschlagSelected
      ).toHaveBeenCalledExactlyOnceWith(wv);
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
        .gueltigeStimmen(0)
        .ungueltigeStimmen(0)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      managed.wahlvorschlagAddVotesOrThrow(1);

      // Add range votes to both candidates -> should clear both reststimmen and convert to direct votes
      managed.kandidatenAddStimmenInRangeOrThrow(101, 102, 1);

      expect(k1.einzelstimmen).toBe(1);
      expect(k2.einzelstimmen).toBe(1);
      expect(k1.reststimmen).toBe(0);
      expect(k2.reststimmen).toBe(0);
      expect(wv.gueltigeStimmen).toBe(2);
      expect(managed.stimmenSummary.value.einzelstimmen).toBe(2);
      expect(managed.stimmenSummary.value.reststimmen).toBe(0);
      expect(
        mockDefinitions.registerWahlvorschlagSelected
      ).toHaveBeenCalledExactlyOnceWith(wv);
    });
  });
});
