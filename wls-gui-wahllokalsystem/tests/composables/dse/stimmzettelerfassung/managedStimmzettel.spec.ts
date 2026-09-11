import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";

import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
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
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { WahlvorstandBeschlussvorschlaegeEnum } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussvorschlaegeEnum.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";
import { KopfdatenStimmzettelgebietsartEnum } from "@/types/kopfdaten/KopfdatenStimmzettelgebietsartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  changeHistory: {
    registerKandidatEinzelstimmenAdded: vi.fn(),
    registerKandidatEinzelstimmenRemoved: vi.fn(),
    registerKandidatEinzelstimmenRangeAdded: vi.fn(),
    registerKandidatUngueltigeStimmenAdded: vi.fn(),
    registerKandidatUngueltigeStimmenRemoved: vi.fn(),
    registerKandidatStreichungSet: vi.fn(),
    registerKandidatStreichungUnset: vi.fn(),
    registerKandidatStreichungRangeUnset: vi.fn(),
    registerKandidatStreichungRangeSet: vi.fn(),
    registerWahlvorschlagSelected: vi.fn(),
    registerWahlvorschlagDeselected: vi.fn(),
    reset: vi.fn(),
  },
  reststimmeUtils: {
    deselectWahlvorschlag: vi.fn(),
    refreshWahlvorschlaegeVotes: vi.fn(),
    selectWahlvorschlag: vi.fn(),
  },
}));

vi.mock(
  import("@/composables/dse/stimmzettelerfassung/stimmzettelChangeHistory.ts"),
  async (importOriginal) => {
    const original = await importOriginal();

    return {
      useStimmzettelChangeHistory: () => ({
        ...original.useStimmzettelChangeHistory(),
        ...mockDefinitions.changeHistory,
      }),
    };
  }
);

vi.mock(
  import("@/composables/dse/stimmzettelerfassung/managedStimmzettel/managedStimmzettelReststimmeUtils.ts"),
  () => ({
    useManagedStimmzettelReststimmeUtils: () => ({
      hasSystemErrorToManyListenKreuze: ref(false),
      refreshWahlvorschlaegeVotes:
        mockDefinitions.reststimmeUtils.refreshWahlvorschlaegeVotes,
      resetError: vi.fn(),
      selectWahlvorschlag: mockDefinitions.reststimmeUtils.selectWahlvorschlag,
      deselectWahlvorschlag:
        mockDefinitions.reststimmeUtils.deselectWahlvorschlag,
    }),
  })
);

describe("managedStimmzettel.ts", () => {
  const mockedWahlId = "wahl-1";
  const {
    prepareManagedStimmzettelStimmzettel,
    prepareManagedStimmzettelWahlvorschlag,
    prepareManagedStimmzettelKandidat,
  } = useManagedStimmzettelTestDataFactory();
  const {
    prepareStimmzettel,
    prepareStimmzettelWahlvorschlag,
    prepareStimmzettelKandidatOfWahlvorschlag,
    preparePersistedStimmzettel,
    preparePersistedStimmzettelWahlvorschlag,
  } = useStimmzettelTestDataFactory();

  const MAXIMAL_ERLAUBTE_STIMMEN_PRO_WAEHLER = 999;

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
        maximalErlaubteStimmenProWaehler: MAXIMAL_ERLAUBTE_STIMMEN_PRO_WAEHLER,
      },
    ];
  });

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("hasAnyValuesSet", () => {
    it("should_returnFalse_when_stimmzettelHasNoVotesOrStreichungSet", () => {
      const kandidatWithoutVotes = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .reststimmen(null)
        .ungueltigeStimmen(null)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidatWithoutVotes])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(managed.hasAnyValuesSet.value).toStrictEqual(false);
    });

    it("should_returnTrue_when_stimmzettelHasOneKandidatWithStreichung", () => {
      const kandidatWithoutAnyValues = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .reststimmen(null)
        .ungueltigeStimmen(null)
        .build();
      const kandidatWithStreichung = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(true)
        .reststimmen(null)
        .ungueltigeStimmen(null)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidatWithoutAnyValues, kandidatWithStreichung])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(managed.hasAnyValuesSet.value).toStrictEqual(true);
    });

    it("should_returnTrue_when_stimmzettelHasOneKandidatWithReststimme", () => {
      const kandidatWithoutAnyValues = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .reststimmen(null)
        .ungueltigeStimmen(null)
        .build();
      const kandidatWithReststimme = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .reststimmen(1)
        .ungueltigeStimmen(null)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidatWithoutAnyValues, kandidatWithReststimme])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(managed.hasAnyValuesSet.value).toStrictEqual(true);
    });

    it("should_returnTrue_when_stimmzettelHasOneKandidatWithEinzelstimme", () => {
      const kandidatWithoutAnyValues = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .reststimmen(null)
        .ungueltigeStimmen(null)
        .build();
      const kandidatWithEinzelstimme = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(101)
        .einzelstimmen(1)
        .durchgestrichen(false)
        .reststimmen(null)
        .ungueltigeStimmen(null)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidatWithoutAnyValues, kandidatWithEinzelstimme])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(managed.hasAnyValuesSet.value).toStrictEqual(true);
    });

    it("should_returnTrue_when_stimmzettelHasOneKandidatWithUngueltigeStimme", () => {
      const kandidatWithoutAnyValues = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .reststimmen(null)
        .ungueltigeStimmen(null)
        .build();
      const kandidatWithUngueltigeStimme = prepareManagedStimmzettelKandidat()
        .listenposition(2)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .reststimmen(null)
        .ungueltigeStimmen(1)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([
              kandidatWithoutAnyValues,
              kandidatWithUngueltigeStimme,
            ])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(managed.hasAnyValuesSet.value).toStrictEqual(true);
    });
  });

  describe("kandidatAddEinzelstimmenOrThrow", () => {
    it("should_addVotes_when_kandidatIsPresent", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .einzelstimmen(1)
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidat])
            .build(),
        ])
        .build();
      const votesToAdd = 2;

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      managed.kandidatAddEinzelstimmenOrThrow(101, votesToAdd);
      expect(kandidat.einzelstimmen).toBe(3);
      expect(
        mockDefinitions.changeHistory.registerKandidatEinzelstimmenAdded
      ).toHaveBeenCalledExactlyOnceWith(kandidat, votesToAdd);
    });

    it("should_throwError_when_kandidatNotFound", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();
      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      expect(() => managed.kandidatAddEinzelstimmenOrThrow(101, 1)).toThrow(
        ManagedStimmzettelError
      );
    });
  });

  describe("kandidatRemoveEinzelstimmenOrThrow", () => {
    it("should_removeVotes_when_kandidatIsPresent", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .einzelstimmen(3)
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidat])
            .build(),
        ])
        .build();
      const votesToRemove = 2;

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      managed.kandidatRemoveEinzelstimmenOrThrow(101, votesToRemove);
      expect(kandidat.einzelstimmen).toBe(1);
      expect(
        mockDefinitions.changeHistory.registerKandidatEinzelstimmenRemoved
      ).toHaveBeenCalledExactlyOnceWith(kandidat, votesToRemove);
    });

    it("should_throwError_when_kandidatToRemoveVotesHasNotEnoughEinzelstimmen", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .einzelstimmen(1)
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
      expect(() => managed.kandidatRemoveEinzelstimmenOrThrow(101, 2)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_throwError_when_kandidatNotFound", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();
      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      expect(() => managed.kandidatRemoveEinzelstimmenOrThrow(101, 1)).toThrow(
        ManagedStimmzettelError
      );
    });
  });

  describe("kandidatAddUngueltigeStimmenOrThrow", () => {
    it("should_addInvalidVotes_when_kandidatIsPresent", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .ungueltigeStimmen(1)
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidat])
            .build(),
        ])
        .build();
      const invalidVotesToAdd = 2;

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      managed.kandidatAddUngueltigeStimmenOrThrow(101, invalidVotesToAdd);
      expect(kandidat.ungueltigeStimmen).toBe(3);
      expect(
        mockDefinitions.changeHistory.registerKandidatUngueltigeStimmenAdded
      ).toHaveBeenCalledExactlyOnceWith(kandidat, invalidVotesToAdd);
    });

    it("should_throwError_when_kandidatNotFound", () => {
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
    it("should_removeInvalidVotes_when_kandidatIsPresent", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .ungueltigeStimmen(3)
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidat])
            .build(),
        ])
        .build();
      const invalidVotesToRemove = 2;

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      managed.kandidatRemoveUngueltigeStimmenOrThrow(101, invalidVotesToRemove);
      expect(kandidat.ungueltigeStimmen).toBe(1);
      expect(
        mockDefinitions.changeHistory.registerKandidatUngueltigeStimmenRemoved
      ).toHaveBeenCalledExactlyOnceWith(kandidat, invalidVotesToRemove);
    });

    it("should_throwError_when_kandidatToRemoveInvalidVotesHasNotEnoughUngueltigeStimmen", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .ungueltigeStimmen(1)
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
      expect(() =>
        managed.kandidatRemoveUngueltigeStimmenOrThrow(101, 2)
      ).toThrow(ManagedStimmzettelError);
    });

    it("should_throwError_when_kandidatNotFound", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();
      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      expect(() =>
        managed.kandidatRemoveUngueltigeStimmenOrThrow(101, 1)
      ).toThrow(ManagedStimmzettelError);
    });
  });

  describe("kandidatenAddStimmenInRangeOrThrow", () => {
    it("should_addVotesToKandidatenInRange_when_kandidatenArePresent", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(102)
        .einzelstimmen(null)
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
      const votesToAdd = 1;

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      managed.kandidatenAddStimmenInRangeOrThrow(101, 102, votesToAdd);
      expect(k1.einzelstimmen).toBe(1);
      expect(k2.einzelstimmen).toBe(1);
      expect(
        mockDefinitions.changeHistory.registerKandidatEinzelstimmenRangeAdded
      ).toHaveBeenCalledExactlyOnceWith([k1, k2], votesToAdd);
    });

    it("should_throwError_when_kandidatInRangeHasStreichung", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(true)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(102)
        .einzelstimmen(null)
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
        managed.kandidatenAddStimmenInRangeOrThrow(101, 102, 1)
      ).toThrow(ManagedStimmzettelError);
    });

    it("should_throwError_when_anyOrdnungszahlInRangeIsMissing", () => {
      const k1 = prepareManagedStimmzettelKandidat().ordnungszahl(101).build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([k1])
            .build(),
        ])
        .build();
      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      expect(() =>
        managed.kandidatenAddStimmenInRangeOrThrow(101, 102, 1)
      ).toThrow(ManagedStimmzettelError);
    });
  });

  describe("kandidatAddStreichungOrThrow", () => {
    it("should_setStreichung_when_KandidatIsPresent", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
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
        mockDefinitions.changeHistory.registerKandidatStreichungSet
      ).toHaveBeenCalledExactlyOnceWith(kandidat);
    });

    it("should_throwError_when_kandidatHasStreichung", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .durchgestrichen(true)
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
      expect(() => managed.kandidatAddStreichungOrThrow(101)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_throwError_when_kandidatNotFound", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();
      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      expect(() => managed.kandidatAddStreichungOrThrow(101)).toThrow(
        ManagedStimmzettelError
      );
    });
  });

  describe("kandidatRemoveStreichungOrThrow", () => {
    it("should_unsetStreichung_when_kandidatIsPresent", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .durchgestrichen(true)
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
      managed.kandidatRemoveStreichungOrThrow(101);
      expect(kandidat.durchgestrichen).toBe(false);
      expect(
        mockDefinitions.changeHistory.registerKandidatStreichungUnset
      ).toHaveBeenCalledExactlyOnceWith(kandidat);
    });

    it("should_throwError_when_kandidatHasNoStreichung", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
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
      expect(() => managed.kandidatRemoveStreichungOrThrow(101)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_throwError_when_kandidatNotFound", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();
      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      expect(() => managed.kandidatRemoveStreichungOrThrow(101)).toThrow(
        ManagedStimmzettelError
      );
    });
  });

  describe("kandidatenStreichungenInRangeOrThrow", () => {
    it("should_setStreichungenInRange_when_kandidatenArePresent", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
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
      managed.kandidatenStreichungenInRangeOrThrow(101, 102);
      expect(k1.durchgestrichen).toBe(true);
      expect(k2.durchgestrichen).toBe(true);
      expect(
        mockDefinitions.changeHistory.registerKandidatStreichungRangeSet
      ).toHaveBeenCalledExactlyOnceWith([k1, k2]);
    });

    it("should_throwErro_when_kandidatenInRangeHaveStreichung", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .durchgestrichen(true)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(102)
        .durchgestrichen(true)
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
        managed.kandidatenStreichungenInRangeOrThrow(101, 102)
      ).toThrow(ManagedStimmzettelError);
    });

    it("should_throwError_when_kandidatInRangeNotFound", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .durchgestrichen(false)
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([k1])
            .build(),
        ])
        .build();
      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      expect(() =>
        managed.kandidatenStreichungenInRangeOrThrow(101, 102)
      ).toThrow(ManagedStimmzettelError);
    });
  });

  describe("kandidatenRemoveStreichungenInRangeOrThrow", () => {
    it("should_unsetStreichungenInRange_when_kandidatenArePresent", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .durchgestrichen(true)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(102)
        .durchgestrichen(true)
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
      managed.kandidatenRemoveStreichungenInRangeOrThrow(101, 102);
      expect(k1.durchgestrichen).toBe(false);
      expect(k2.durchgestrichen).toBe(false);
      expect(
        mockDefinitions.changeHistory.registerKandidatStreichungRangeUnset
      ).toHaveBeenCalledExactlyOnceWith([k1, k2]);
    });

    it("should_throwError_whenRangeAlreadyHasNoStreichungen", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
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

    it("should_throwError_when_kandidatInRangeNotFound", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .durchgestrichen(false)
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([k1])
            .build(),
        ])
        .build();
      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      expect(() =>
        managed.kandidatenRemoveStreichungenInRangeOrThrow(101, 102)
      ).toThrow(ManagedStimmzettelError);
    });
  });

  describe("stimmzettel", () => {
    it("should_notHaveAnySystemBeschlussvorschlag_when_noDataIsChanged", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .reststimmen(null)
        .ungueltigeStimmen(null)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidat])
            .build(),
        ])
        .invalideVotes(0)
        .build();

      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

      expect(managed.stimmzettel.value.systemBeschlussvorschlag).toStrictEqual(
        []
      );
    });

    it.each([1, 10])(
      "should_setSystemBeschlussvorschlagEinzelneStimmenUngueltig_when_anyKandidatHasAtLeastOneInvalidVoteWith'%d'",
      (countInvalidVotes) => {
        const kandidatWithInvalidVotes = prepareManagedStimmzettelKandidat()
          .listenposition(1)
          .ordnungszahl(101)
          .einzelstimmen(null)
          .durchgestrichen(false)
          .reststimmen(null)
          .ungueltigeStimmen(countInvalidVotes)
          .build();

        const stimmzettel = prepareManagedStimmzettelStimmzettel()
          .wahlvorschlaege([
            prepareManagedStimmzettelWahlvorschlag()
              .ordnungszahl(1)
              .kandidaten([kandidatWithInvalidVotes])
              .build(),
          ])
          .invalideVotes(0)
          .build();

        const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

        expect(
          managed.stimmzettel.value.systemBeschlussvorschlag
        ).toStrictEqual([
          {
            reason: SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig,
          } as SystemBeschlussgrund,
        ]);
      }
    );

    it.each([1, 10])(
      "should_setSystemBeschlussvorschlagEinzelneStimmenUngueltig_when_stimmzettelHasAtLeastOneInvalidVoteWith'%d'",
      (countInvalidVotes) => {
        const stimmzettel = prepareManagedStimmzettelStimmzettel()
          .wahlvorschlaege([
            prepareManagedStimmzettelWahlvorschlag()
              .ordnungszahl(1)
              .kandidaten([])
              .build(),
          ])
          .invalideVotes(countInvalidVotes)
          .build();

        const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

        expect(
          managed.stimmzettel.value.systemBeschlussvorschlag
        ).toStrictEqual([
          {
            reason: SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig,
          } as SystemBeschlussgrund,
        ]);
      }
    );

    it.each([1, 10])(
      "should_setSystemBeschlussvorschlagEinzelneStimmenUngueltig_when_stimmzettelAndAnyKandidatHasAtLeastOneInvalidVoteWith'%d'",
      (countInvalidVotes) => {
        const kandidatWithInvalidVotes = prepareManagedStimmzettelKandidat()
          .listenposition(1)
          .ordnungszahl(101)
          .einzelstimmen(null)
          .durchgestrichen(false)
          .reststimmen(null)
          .ungueltigeStimmen(countInvalidVotes)
          .build();

        const stimmzettel = prepareManagedStimmzettelStimmzettel()
          .wahlvorschlaege([
            prepareManagedStimmzettelWahlvorschlag()
              .ordnungszahl(1)
              .kandidaten([kandidatWithInvalidVotes])
              .build(),
          ])
          .invalideVotes(countInvalidVotes)
          .build();

        const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);

        expect(
          managed.stimmzettel.value.systemBeschlussvorschlag
        ).toStrictEqual([
          {
            reason: SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig,
          } as SystemBeschlussgrund,
        ]);
      }
    );

    it.each([1, 10])(
      "should_setSystemBeschlussvorschlagZuVieleEinzelstimmenAberImGesamtstimmenlimit_when_stimmzettelAndAnyKandidatHas'%d'MoreEinzelstimmenThanAllowed",
      (numberOfVotesAboveLimit) => {
        const maxEinzelstimmenJeKandidat = 3;
        const kandidatWithToManyEinzelstimmen =
          prepareManagedStimmzettelKandidat()
            .listenposition(1)
            .ordnungszahl(101)
            .einzelstimmen(maxEinzelstimmenJeKandidat + numberOfVotesAboveLimit)
            .durchgestrichen(false)
            .reststimmen(null)
            .ungueltigeStimmen(null)
            .build();

        const stimmzettel = prepareManagedStimmzettelStimmzettel()
          .wahlvorschlaege([
            prepareManagedStimmzettelWahlvorschlag()
              .ordnungszahl(1)
              .kandidaten([kandidatWithToManyEinzelstimmen])
              .build(),
          ])
          .invalideVotes(null)
          .build();

        const managed = useManagedStimmzettel(
          ref(stimmzettel),
          mockedWahlId,
          maxEinzelstimmenJeKandidat
        );

        expect(
          managed.stimmzettel.value.systemBeschlussvorschlag
        ).toStrictEqual([
          {
            reason:
              SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit,
          } as SystemBeschlussgrund,
        ]);
      }
    );

    it.each([1, 10])(
      "should_setSystemBeschlussvorschlagZuVieleEinzelstimmenOderListenkreuze_when_stimmzettelAndAnyKandidatHas'%d'MoreEinzelstimmenThanAllowedAndMoreThanAllowedInTotal",
      (numberOfVotesAboveLimit) => {
        const maxEinzelstimmenJeKandidat = 3;
        const kandidatWithToManyEinzelstimmen =
          prepareManagedStimmzettelKandidat()
            .listenposition(1)
            .ordnungszahl(101)
            .einzelstimmen(
              MAXIMAL_ERLAUBTE_STIMMEN_PRO_WAEHLER + numberOfVotesAboveLimit
            )
            .durchgestrichen(false)
            .reststimmen(null)
            .ungueltigeStimmen(null)
            .build();

        const stimmzettel = prepareManagedStimmzettelStimmzettel()
          .wahlvorschlaege([
            prepareManagedStimmzettelWahlvorschlag()
              .ordnungszahl(1)
              .kandidaten([kandidatWithToManyEinzelstimmen])
              .build(),
          ])
          .invalideVotes(null)
          .build();

        const managed = useManagedStimmzettel(
          ref(stimmzettel),
          mockedWahlId,
          maxEinzelstimmenJeKandidat
        );

        expect(
          managed.stimmzettel.value.systemBeschlussvorschlag
        ).toStrictEqual([
          {
            reason:
              SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
          } as SystemBeschlussgrund,
        ]);
      }
    );
  });

  describe("wahlvorschlagAddVotesOrThrow", () => {
    it("should_selectWahlvorschlagAndAssignReststimmen_when_wahlvorschlagIsPresent", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .einzelstimmen(null)
        .ungueltigeStimmen(0)
        .reststimmen(null)
        .durchgestrichen(false)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
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
      expect(
        mockDefinitions.reststimmeUtils.selectWahlvorschlag.mock.calls
      ).toStrictEqual([[wv]]);
      expect(
        mockDefinitions.reststimmeUtils.refreshWahlvorschlaegeVotes
      ).toHaveBeenCalledOnce();
      expect(
        mockDefinitions.changeHistory.registerWahlvorschlagSelected
      ).toHaveBeenCalledExactlyOnceWith(wv);
    });

    it("should_throwError_when_wahlvorschlagAlreadySelected", () => {
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .ordnungszahl(1)
        .selected(true)
        .kandidaten([])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();
      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      expect(() => managed.wahlvorschlagAddVotesOrThrow(1)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_throwError_when_wahlvorschlagNotFound", () => {
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
    it("should_deselectWahlvorschlagAndClearReststimmen_when_wahlvorschlagIsPresent", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .reststimmen(1)
        .build();
      const k2 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(102)
        .reststimmen(1)
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
      expect(
        mockDefinitions.reststimmeUtils.deselectWahlvorschlag.mock.calls
      ).toStrictEqual([[wv]]);
      expect(
        mockDefinitions.reststimmeUtils.refreshWahlvorschlaegeVotes
      ).toHaveBeenCalledOnce();
      expect(
        mockDefinitions.changeHistory.registerWahlvorschlagDeselected
      ).toHaveBeenCalledExactlyOnceWith(wv);
    });

    it("should_throwError_when_wahlvorschlagAlreadyDeelected", () => {
      const wv = prepareManagedStimmzettelWahlvorschlag()
        .ordnungszahl(1)
        .selected(false)
        .kandidaten([])
        .build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([wv])
        .build();
      const managed = useManagedStimmzettel(ref(stimmzettel), mockedWahlId);
      expect(() => managed.wahlvorschlagRemoveVotesOrThrow(1)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_throwError_when_wahlvorschlagNotFound", () => {
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
    const initialEmptyDseWahlvorschlag = prepareStimmzettelWahlvorschlag()
      .wahlvorschlagID("1")
      .ordnungszahl(1)
      .kandidaten([])
      .selected(false)
      .ungueltigeStimmen(0)
      .gueltigeStimmen(0)
      .erhaeltStimmen(true)
      .kurzname("kurzname")
      .build();

    const initialEmptyDseKandidat = prepareStimmzettelKandidatOfWahlvorschlag(
      initialEmptyDseWahlvorschlag
    )
      .ordnungszahl(101)
      .einzelstimmen(null)
      .ungueltigeStimmen(null)
      .reststimmen(null)
      .durchgestrichen(false)
      .owningWahlvorschlag(initialEmptyDseWahlvorschlag)
      .build();

    const initialEmptyDseStimzettel = prepareStimmzettel()
      .wahlvorstandBeschlussvorschlag([])
      .systemBeschlussvorschlag([])
      .beschlussfassung(null)
      .gueltigkeit("VALID")
      .invalideVotes(0)
      .wahlvorschlaege([initialEmptyDseWahlvorschlag])
      .build();

    initialEmptyDseStimzettel.wahlvorschlaege[0].kandidaten = [
      initialEmptyDseKandidat,
    ];

    it("should_resetStimmzettelToEmpty_when_calledWithoutReference", () => {
      const managedStimmzettel = useManagedStimmzettel(
        // structuredClone creates deep copy, so mutations are only applied to copy,
        // not to "initialEmptyDseStimzettel"
        ref(structuredClone(initialEmptyDseStimzettel)),
        mockedWahlId
      );

      expect(managedStimmzettel.stimmzettel.value).toStrictEqual(
        initialEmptyDseStimzettel
      );

      managedStimmzettel.kandidatAddStreichungOrThrow(
        initialEmptyDseKandidat.ordnungszahl
      );
      managedStimmzettel.kandidatAddEinzelstimmenOrThrow(
        initialEmptyDseKandidat.ordnungszahl,
        5
      );
      managedStimmzettel.wahlvorschlagAddVotesOrThrow(
        initialEmptyDseWahlvorschlag.ordnungszahl
      );
      managedStimmzettel.stimmzettel.value.wahlvorstandBeschlussvorschlag = [
        {
          text: WahlvorstandBeschlussvorschlaegeEnum.StimmzettelMitBesonderemZusatz,
        },
      ];
      managedStimmzettel.stimmzettel.value.systemBeschlussvorschlag = [
        {
          reason:
            SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit,
        },
      ];

      expect(managedStimmzettel.stimmzettel.value).not.toStrictEqual(
        initialEmptyDseStimzettel
      );

      managedStimmzettel.resetStimmzettel();

      expect(managedStimmzettel.stimmzettel.value).toStrictEqual(
        initialEmptyDseStimzettel
      );
      expect(mockDefinitions.changeHistory.reset).toHaveBeenCalledTimes(1);

      const stimmzettelAfterReset = managedStimmzettel.stimmzettel.value;
      expect(
        stimmzettelAfterReset.wahlvorstandBeschlussvorschlag
      ).toStrictEqual([]);
      expect(stimmzettelAfterReset.systemBeschlussvorschlag).toStrictEqual([]);
      expect(stimmzettelAfterReset.gueltigkeit).toStrictEqual("VALID");
      expect(stimmzettelAfterReset.beschlussfassung).toBeNull();
      expect(stimmzettelAfterReset.invalideVotes).toBe(0);
      stimmzettelAfterReset.wahlvorschlaege.forEach((wahlvorschlag) => {
        expect(wahlvorschlag.selected).toBe(false);
        wahlvorschlag.kandidaten.forEach((k) => {
          expect(k.einzelstimmen).toBeNull();
          expect(k.ungueltigeStimmen).toBeNull();
          expect(k.reststimmen).toBeNull();
          expect(k.durchgestrichen).toBe(false);
        });
      });
    });

    it("should_resetStimmzettelToReference_when_calledWithReference", () => {
      const managedStimmzettel = useManagedStimmzettel(
        // structuredClone creates deep copy, so mutations are only applied to copy,
        // not to "initialEmptyDseStimzettel"
        ref(structuredClone(initialEmptyDseStimzettel)),
        mockedWahlId
      );

      const persistedKandidat1 = {
        kandidatId: initialEmptyDseKandidat.kandidatId,
        nennung: initialEmptyDseKandidat.nennung,
        isDiscarded: true,
        votesByVoter: 5,
        invalidVotes: 2,
        votesByWahlvorschlag: 1,
      };
      const stimmzettelToResetTo = preparePersistedStimmzettel()
        .stimmzettelkennung(1)
        .teamID("team-1")
        .gueltigkeit("INVALID")
        .invalideVotes(0)
        .wahlvorschlaege([
          preparePersistedStimmzettelWahlvorschlag()
            .wahlvorschlagID(initialEmptyDseWahlvorschlag.wahlvorschlagID)
            .selected(true)
            .kandidaten([persistedKandidat1])
            .build(),
        ])
        .wahlvorstandBeschlussvorschlag([
          {
            text: WahlvorstandBeschlussvorschlaegeEnum.StimmzettelMitBesonderemZusatz,
          },
        ])
        .beschlussfassung(null)
        .build();

      expect(managedStimmzettel.stimmzettel.value).toStrictEqual(
        initialEmptyDseStimzettel
      );

      managedStimmzettel.resetStimmzettel(stimmzettelToResetTo);

      expect(managedStimmzettel.stimmzettel.value).not.toStrictEqual(
        initialEmptyDseStimzettel
      );
      expect(mockDefinitions.changeHistory.reset).toHaveBeenCalledTimes(1);

      const stimmzettelAfterReset = managedStimmzettel.stimmzettel.value;
      expect(
        stimmzettelAfterReset.wahlvorstandBeschlussvorschlag
      ).toStrictEqual([
        {
          text: WahlvorstandBeschlussvorschlaegeEnum.StimmzettelMitBesonderemZusatz,
        },
      ]);
      expect(stimmzettelAfterReset.gueltigkeit).toStrictEqual("INVALID");
      expect(stimmzettelAfterReset.beschlussfassung).toBeNull();
      expect(stimmzettelAfterReset.invalideVotes).toBe(0);
      stimmzettelAfterReset.wahlvorschlaege.forEach((wahlvorschlag) => {
        expect(wahlvorschlag.selected).toBe(true);
        wahlvorschlag.kandidaten.forEach((k) => {
          expect(k.einzelstimmen).toBe(5);
          expect(k.ungueltigeStimmen).toBe(2);
          expect(k.reststimmen).toBe(1);
          expect(k.durchgestrichen).toBe(true);
        });
      });
    });
  });
});
