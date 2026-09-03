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
    it("should_addVotes_and_registerHistory_when_kandidatExists", () => {
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

      const countVotes = 3;
      managed.kandidatAddEinzelstimmenOrThrow(101, countVotes);

      expect(kandidat.einzelstimmen).toBe(5);
      expect(
        mockDefinitions.registerKandidatEinzelstimmenAdded
      ).toHaveBeenCalledExactlyOnceWith(kandidat, countVotes);
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
  });

  describe("kandidatenStreichungenInRangeOrThrow", () => {
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
  });

  describe("resetStimmzettel", () => {
    it("should_resetAssignedValues_when_called", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(1)
        .ungueltigeStimmen(1)
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
      managed.resetStimmzettel();

      expect(wv.selected).toBe(false);
      expect(kandidat.einzelstimmen).toBeNull();
      expect(kandidat.ungueltigeStimmen).toBeNull();
      expect(kandidat.reststimmen).toBeNull();
      expect(kandidat.durchgestrichen).toBe(false);
    });
  });
});
