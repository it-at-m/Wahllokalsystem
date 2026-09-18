import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { DseStimmzettel } from "@/types/dse/stimmzettelerfassung/DseStimmzettel.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
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
import { nextTick, ref } from "vue";

import { useBearbeitenDialogStimmzettelUtils } from "@/composables/dse/stimmzettelerfassung/bearbeitenDialogStimmzettelUtils.ts";
import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";
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
  mapPersistedStimmzettelValuesToExistingDseStimmzettel: vi.fn(),
  resetError: vi.fn(),
  resetDseStimmzettel: vi.fn(),
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
  import("@/composables/dse/stimmzettelerfassung/bearbeitenDialogStimmzettel/bearbeitenDialogStimmzettelReststimmeUtils.ts"),
  () => ({
    useBearbeitenDialogStimmzettelReststimmeUtils: () => ({
      hasSystemErrorToManyListenKreuze: ref(false),
      refreshWahlvorschlaegeVotes:
        mockDefinitions.reststimmeUtils.refreshWahlvorschlaegeVotes,
      resetError: mockDefinitions.resetError,
      selectWahlvorschlag: mockDefinitions.reststimmeUtils.selectWahlvorschlag,
      deselectWahlvorschlag:
        mockDefinitions.reststimmeUtils.deselectWahlvorschlag,
    }),
  })
);

vi.mock(
  import("@/composables/dse/stimmzettelerfassung/stimmzettelMapper.ts"),
  async (importOriginal) => {
    const original = await importOriginal();
    return {
      useStimmzettelMapper: () => ({
        ...original.useStimmzettelMapper(),
        mapPersistedStimmzettelValuesToExistingDseStimmzettel:
          mockDefinitions.mapPersistedStimmzettelValuesToExistingDseStimmzettel,
      }),
    };
  }
);

vi.mock(
  import("@/composables/dse/stimmzettelerfassung/stimmzettelTools.ts"),
  async (importOriginal) => {
    const original = await importOriginal();
    return {
      useStimmzettelTools: () => ({
        ...original.useStimmzettelTools(),
        resetDseStimmzettel: mockDefinitions.resetDseStimmzettel,
      }),
    };
  }
);

describe("bearbeitenDialogStimmzettelUtils.ts", () => {
  const mockedWahlId = "wahl-1";
  const {
    prepareDseStimmzettel,
    prepareDseWahlvorschlag,
    prepareDseKandidat,
    prepareDseKandidatOfDseWahlvorschlag,
  } = useStimmzettelTestDataFactory();

  const {
    preparePersistedStimmzettelKandidat,
    preparePersistedStimmzettel,
    preparePersistedStimmzettelWahlvorschlag,
  } = usePersistedStimmzettelTestDataFactory();

  const MAXIMAL_ERLAUBTE_STIMMEN_PRO_WAEHLER = 999;

  let mockedStimmzettelWithoutWahlvorschlaege: DseStimmzettel;
  let stimmzettelWithoutValuesSet: DseStimmzettel;

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

    mockedStimmzettelWithoutWahlvorschlaege = prepareDseStimmzettel()
      .wahlvorschlaege([])
      .build();

    const kandidatWithoutVotes = prepareDseKandidat()
      .listenposition(1)
      .ordnungszahl(101)
      .einzelstimmen(null)
      .durchgestrichen(false)
      .reststimmen(null)
      .ungueltigeStimmen(null)
      .build();

    stimmzettelWithoutValuesSet = prepareDseStimmzettel()
      .wahlvorschlaege([
        prepareDseWahlvorschlag()
          .ordnungszahl(1)
          .kandidaten([kandidatWithoutVotes])
          .build(),
      ])
      .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
      .invalideVotes(0)
      .wahlvorstandBeschlussvorschlag([])
      .systemBeschlussvorschlag([])
      .beschlussfassung(null)
      .build();
  });

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("hasAnyValuesSet", () => {
    it("should_returnFalse_when_stimmzettelHasNoVotesOrStreichungSetAndGueltigkeitIsValid", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettelWithoutValuesSet),
        mockedWahlId
      );

      expect(managed.hasAnyValuesSet.value).toStrictEqual(false);
    });

    it("should_returnTrue_when_stimmzettelHasNoVotesOrStreichungSetAndGueltigkeitIsNotValid", () => {
      stimmzettelWithoutValuesSet.gueltigkeit =
        StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettelWithoutValuesSet),
        mockedWahlId
      );

      expect(managed.hasAnyValuesSet.value).toStrictEqual(true);
    });

    it("should_returnTrue_when_stimmzettelHasOneKandidatWithStreichung", () => {
      stimmzettelWithoutValuesSet.wahlvorschlaege[0].kandidaten[0].durchgestrichen = true;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettelWithoutValuesSet),
        mockedWahlId
      );

      expect(managed.hasAnyValuesSet.value).toStrictEqual(true);
    });

    it("should_returnTrue_when_stimmzettelHasOneKandidatWithReststimme", () => {
      stimmzettelWithoutValuesSet.wahlvorschlaege[0].kandidaten[0].reststimmen = 1;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettelWithoutValuesSet),
        mockedWahlId
      );

      expect(managed.hasAnyValuesSet.value).toStrictEqual(true);
    });

    it("should_returnTrue_when_stimmzettelHasOneKandidatWithEinzelstimme", () => {
      stimmzettelWithoutValuesSet.wahlvorschlaege[0].kandidaten[0].einzelstimmen = 1;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettelWithoutValuesSet),
        mockedWahlId
      );

      expect(managed.hasAnyValuesSet.value).toStrictEqual(true);
    });

    it("should_returnTrue_when_stimmzettelHasOneKandidatWithUngueltigeStimme", () => {
      stimmzettelWithoutValuesSet.wahlvorschlaege[0].kandidaten[0].ungueltigeStimmen = 1;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettelWithoutValuesSet),
        mockedWahlId
      );

      expect(managed.hasAnyValuesSet.value).toStrictEqual(true);
    });
  });

  describe("kandidatAddEinzelstimmenOrThrow", () => {
    it("should_addVotes_when_kandidatIsPresent", () => {
      const kandidat = prepareDseKandidat()
        .ordnungszahl(101)
        .einzelstimmen(1)
        .durchgestrichen(false)
        .build();
      const stimmzettel = prepareDseStimmzettel()
        .wahlvorschlaege([kandidat.owningWahlvorschlag])
        .build();
      const votesToAdd = 2;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      managed.kandidatAddEinzelstimmenOrThrow(101, votesToAdd);
      expect(kandidat.einzelstimmen).toBe(3);
      expect(
        mockDefinitions.changeHistory.registerKandidatEinzelstimmenAdded
      ).toHaveBeenCalledExactlyOnceWith(kandidat, votesToAdd);
    });

    it("should_throwError_when_kandidatNotFound", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(mockedStimmzettelWithoutWahlvorschlaege),
        mockedWahlId
      );
      expect(() => managed.kandidatAddEinzelstimmenOrThrow(101, 1)).toThrow(
        ManagedStimmzettelError
      );
    });
  });

  describe("kandidatRemoveEinzelstimmenOrThrow", () => {
    const kandidat = prepareDseKandidat()
      .ordnungszahl(101)
      .einzelstimmen(3)
      .ungueltigeStimmen(null)
      .build();
    const stimmzettel = prepareDseStimmzettel()
      .wahlvorschlaege([kandidat.owningWahlvorschlag])
      .build();

    it("should_removeVotes_when_kandidatIsPresent", () => {
      const votesToRemove = 2;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      managed.kandidatRemoveEinzelstimmenOrThrow(101, votesToRemove);
      expect(kandidat.einzelstimmen).toBe(1);
      expect(
        mockDefinitions.changeHistory.registerKandidatEinzelstimmenRemoved
      ).toHaveBeenCalledExactlyOnceWith(kandidat, votesToRemove);
    });

    it("should_throwError_when_kandidatToRemoveVotesHasNotEnoughEinzelstimmen", () => {
      stimmzettel.wahlvorschlaege[0].kandidaten[0].einzelstimmen = 1;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      expect(() => managed.kandidatRemoveEinzelstimmenOrThrow(101, 2)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_throwError_when_kandidatToRemoveVotesHasNotEnoughSumOfEinzelstimmenAndUngueltigeStimmen", () => {
      stimmzettel.wahlvorschlaege[0].kandidaten[0].einzelstimmen = 1;
      stimmzettel.wahlvorschlaege[0].kandidaten[0].ungueltigeStimmen = 1;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      expect(() => managed.kandidatRemoveEinzelstimmenOrThrow(101, 3)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_throwError_when_kandidatNotFound", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(mockedStimmzettelWithoutWahlvorschlaege),
        mockedWahlId
      );
      expect(() => managed.kandidatRemoveEinzelstimmenOrThrow(101, 1)).toThrow(
        ManagedStimmzettelError
      );
    });
  });

  describe("kandidatAddUngueltigeStimmenOrThrow", () => {
    it("should_addInvalidVotes_when_kandidatIsPresent", () => {
      const kandidat = prepareDseKandidat()
        .ordnungszahl(101)
        .ungueltigeStimmen(1)
        .build();
      const stimmzettel = prepareDseStimmzettel()
        .wahlvorschlaege([
          prepareDseWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidat])
            .build(),
        ])
        .build();
      const invalidVotesToAdd = 2;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      managed.kandidatAddUngueltigeStimmenOrThrow(101, invalidVotesToAdd);
      expect(kandidat.ungueltigeStimmen).toBe(3);
      expect(
        mockDefinitions.changeHistory.registerKandidatUngueltigeStimmenAdded
      ).toHaveBeenCalledExactlyOnceWith(kandidat, invalidVotesToAdd);
    });

    it("should_throwError_when_kandidatNotFound", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(mockedStimmzettelWithoutWahlvorschlaege),
        mockedWahlId
      );
      expect(() => managed.kandidatAddUngueltigeStimmenOrThrow(101, 1)).toThrow(
        ManagedStimmzettelError
      );
    });
  });

  describe("kandidatRemoveUngueltigeStimmenOrThrow", () => {
    const kandidat = prepareDseKandidat()
      .ordnungszahl(101)
      .ungueltigeStimmen(3)
      .build();
    const stimmzettel = prepareDseStimmzettel()
      .wahlvorschlaege([
        prepareDseWahlvorschlag()
          .ordnungszahl(1)
          .kandidaten([kandidat])
          .build(),
      ])
      .build();

    it("should_removeInvalidVotes_when_kandidatIsPresent", () => {
      const invalidVotesToRemove = 2;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      managed.kandidatRemoveUngueltigeStimmenOrThrow(101, invalidVotesToRemove);
      expect(kandidat.ungueltigeStimmen).toBe(1);
      expect(
        mockDefinitions.changeHistory.registerKandidatUngueltigeStimmenRemoved
      ).toHaveBeenCalledExactlyOnceWith(kandidat, invalidVotesToRemove);
    });

    it("should_throwError_when_kandidatToRemoveInvalidVotesHasNotEnoughUngueltigeStimmen", () => {
      stimmzettel.wahlvorschlaege[0].kandidaten[0].ungueltigeStimmen = 1;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      expect(() =>
        managed.kandidatRemoveUngueltigeStimmenOrThrow(101, 2)
      ).toThrow(ManagedStimmzettelError);
    });

    it("should_throwError_when_kandidatNotFound", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(mockedStimmzettelWithoutWahlvorschlaege),
        mockedWahlId
      );
      expect(() =>
        managed.kandidatRemoveUngueltigeStimmenOrThrow(101, 1)
      ).toThrow(ManagedStimmzettelError);
    });
  });

  describe("kandidatenAddStimmenInRangeOrThrow", () => {
    const k1 = prepareDseKandidat()
      .ordnungszahl(101)
      .einzelstimmen(null)
      .durchgestrichen(false)
      .build();
    const k2 = prepareDseKandidat()
      .ordnungszahl(102)
      .einzelstimmen(null)
      .durchgestrichen(false)
      .build();
    const stimmzettel = prepareDseStimmzettel()
      .wahlvorschlaege([
        prepareDseWahlvorschlag().ordnungszahl(1).kandidaten([k1, k2]).build(),
      ])
      .build();

    it("should_addVotesToKandidatenInRange_when_kandidatenArePresent", () => {
      const votesToAdd = 1;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      managed.kandidatenAddStimmenInRangeOrThrow(101, 102, votesToAdd);
      expect(k1.einzelstimmen).toBe(1);
      expect(k2.einzelstimmen).toBe(1);
      expect(
        mockDefinitions.changeHistory.registerKandidatEinzelstimmenRangeAdded
      ).toHaveBeenCalledExactlyOnceWith([k1, k2], votesToAdd);
    });

    it("should_throwError_when_kandidatInRangeHasStreichung", () => {
      stimmzettel.wahlvorschlaege[0].kandidaten[1].durchgestrichen = true;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      expect(() =>
        managed.kandidatenAddStimmenInRangeOrThrow(101, 102, 1)
      ).toThrow(ManagedStimmzettelError);
    });

    it("should_throwError_when_anyOrdnungszahlInRangeIsMissing", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      expect(() =>
        managed.kandidatenAddStimmenInRangeOrThrow(101, 103, 1)
      ).toThrow(ManagedStimmzettelError);
    });
  });

  describe("kandidatAddStreichungOrThrow", () => {
    const kandidat = prepareDseKandidat()
      .ordnungszahl(101)
      .durchgestrichen(false)
      .build();
    const stimmzettel = prepareDseStimmzettel()
      .wahlvorschlaege([
        prepareDseWahlvorschlag()
          .ordnungszahl(1)
          .kandidaten([kandidat])
          .build(),
      ])
      .build();

    it("should_setStreichung_when_KandidatIsPresent", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      managed.kandidatAddStreichungOrThrow(101);
      expect(kandidat.durchgestrichen).toBe(true);
      expect(
        mockDefinitions.changeHistory.registerKandidatStreichungSet
      ).toHaveBeenCalledExactlyOnceWith(kandidat);
    });

    it("should_throwError_when_kandidatHasStreichung", () => {
      stimmzettel.wahlvorschlaege[0].kandidaten[0].durchgestrichen = true;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      expect(() => managed.kandidatAddStreichungOrThrow(101)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_throwError_when_kandidatNotFound", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(mockedStimmzettelWithoutWahlvorschlaege),
        mockedWahlId
      );
      expect(() => managed.kandidatAddStreichungOrThrow(101)).toThrow(
        ManagedStimmzettelError
      );
    });
  });

  describe("kandidatRemoveStreichungOrThrow", () => {
    const kandidat = prepareDseKandidat()
      .ordnungszahl(101)
      .durchgestrichen(true)
      .build();
    const stimmzettel = prepareDseStimmzettel()
      .wahlvorschlaege([
        prepareDseWahlvorschlag()
          .ordnungszahl(1)
          .kandidaten([kandidat])
          .build(),
      ])
      .build();

    it("should_unsetStreichung_when_kandidatIsPresent", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      managed.kandidatRemoveStreichungOrThrow(101);
      expect(kandidat.durchgestrichen).toBe(false);
      expect(
        mockDefinitions.changeHistory.registerKandidatStreichungUnset
      ).toHaveBeenCalledExactlyOnceWith(kandidat);
    });

    it("should_throwError_when_kandidatHasNoStreichung", () => {
      stimmzettel.wahlvorschlaege[0].kandidaten[0].durchgestrichen = false;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      expect(() => managed.kandidatRemoveStreichungOrThrow(101)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_throwError_when_kandidatNotFound", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(mockedStimmzettelWithoutWahlvorschlaege),
        mockedWahlId
      );
      expect(() => managed.kandidatRemoveStreichungOrThrow(101)).toThrow(
        ManagedStimmzettelError
      );
    });
  });

  describe("kandidatenStreichungenInRangeOrThrow", () => {
    const k1 = prepareDseKandidat()
      .ordnungszahl(101)
      .durchgestrichen(false)
      .build();
    const k2 = prepareDseKandidat()
      .ordnungszahl(102)
      .durchgestrichen(false)
      .build();
    const stimmzettel = prepareDseStimmzettel()
      .wahlvorschlaege([
        prepareDseWahlvorschlag().ordnungszahl(1).kandidaten([k1, k2]).build(),
      ])
      .build();

    it("should_setStreichungenInRange_when_kandidatenArePresent", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      managed.kandidatenStreichungenInRangeOrThrow(101, 102);
      expect(k1.durchgestrichen).toBe(true);
      expect(k1.einzelstimmen).toBe(null);
      expect(k2.durchgestrichen).toBe(true);
      expect(k2.einzelstimmen).toBe(null);
      expect(
        mockDefinitions.changeHistory.registerKandidatStreichungRangeSet
      ).toHaveBeenCalledExactlyOnceWith([k1, k2]);
    });

    it("should_throwError_when_kandidatenInRangeHaveStreichung", () => {
      stimmzettel.wahlvorschlaege[0].kandidaten[0].durchgestrichen = true;
      stimmzettel.wahlvorschlaege[0].kandidaten[1].durchgestrichen = true;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      expect(() =>
        managed.kandidatenStreichungenInRangeOrThrow(101, 102)
      ).toThrow(ManagedStimmzettelError);
    });

    it("should_throwError_when_kandidatInRangeNotFound", () => {
      const k1 = prepareDseKandidat()
        .ordnungszahl(101)
        .durchgestrichen(false)
        .build();
      const stimmzettel = prepareDseStimmzettel()
        .wahlvorschlaege([
          prepareDseWahlvorschlag().ordnungszahl(1).kandidaten([k1]).build(),
        ])
        .build();
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      expect(() =>
        managed.kandidatenStreichungenInRangeOrThrow(101, 102)
      ).toThrow(ManagedStimmzettelError);
    });
  });

  describe("kandidatenRemoveStreichungenInRangeOrThrow", () => {
    const k1 = prepareDseKandidat()
      .ordnungszahl(101)
      .durchgestrichen(true)
      .build();
    const k2 = prepareDseKandidat()
      .ordnungszahl(102)
      .durchgestrichen(true)
      .build();
    const stimmzettel = prepareDseStimmzettel()
      .wahlvorschlaege([
        prepareDseWahlvorschlag().ordnungszahl(1).kandidaten([k1, k2]).build(),
      ])
      .build();

    it("should_unsetStreichungenInRange_when_kandidatenArePresent", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      managed.kandidatenRemoveStreichungenInRangeOrThrow(101, 102);
      expect(k1.durchgestrichen).toBe(false);
      expect(k2.durchgestrichen).toBe(false);
      expect(
        mockDefinitions.changeHistory.registerKandidatStreichungRangeUnset
      ).toHaveBeenCalledExactlyOnceWith([k1, k2]);
    });

    it("should_throwError_whenRangeAlreadyHasNoStreichungen", () => {
      stimmzettel.wahlvorschlaege[0].kandidaten[0].durchgestrichen = false;
      stimmzettel.wahlvorschlaege[0].kandidaten[1].durchgestrichen = false;

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      expect(() =>
        managed.kandidatenRemoveStreichungenInRangeOrThrow(101, 102)
      ).toThrow(ManagedStimmzettelError);
    });

    it("should_throwError_when_kandidatInRangeNotFound", () => {
      const k1 = prepareDseKandidat()
        .ordnungszahl(101)
        .durchgestrichen(false)
        .build();
      const stimmzettel = prepareDseStimmzettel()
        .wahlvorschlaege([
          prepareDseWahlvorschlag().ordnungszahl(1).kandidaten([k1]).build(),
        ])
        .build();
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      expect(() =>
        managed.kandidatenRemoveStreichungenInRangeOrThrow(101, 102)
      ).toThrow(ManagedStimmzettelError);
    });
  });

  describe("stimmzettel", () => {
    it("should_notHaveAnySystemBeschlussvorschlag_when_noDataIsChanged", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettelWithoutValuesSet),
        mockedWahlId
      );

      expect(managed.stimmzettel.value.systemBeschlussvorschlag).toStrictEqual(
        []
      );
    });

    it.each([1, 10])(
      "should_setSystemBeschlussvorschlagEinzelneStimmenUngueltig_when_anyKandidatHasAtLeastOneInvalidVoteWith'%d'",
      (countInvalidVotes) => {
        stimmzettelWithoutValuesSet.wahlvorschlaege[0].kandidaten[0].ungueltigeStimmen =
          countInvalidVotes;

        const managed = useBearbeitenDialogStimmzettelUtils(
          ref(stimmzettelWithoutValuesSet),
          mockedWahlId
        );

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
        stimmzettelWithoutValuesSet.invalideVotes = countInvalidVotes;

        const managed = useBearbeitenDialogStimmzettelUtils(
          ref(stimmzettelWithoutValuesSet),
          mockedWahlId
        );

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
        stimmzettelWithoutValuesSet.invalideVotes = countInvalidVotes;
        stimmzettelWithoutValuesSet.wahlvorschlaege[0].kandidaten[0].ungueltigeStimmen =
          countInvalidVotes;

        const managed = useBearbeitenDialogStimmzettelUtils(
          ref(stimmzettelWithoutValuesSet),
          mockedWahlId
        );

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
        stimmzettelWithoutValuesSet.wahlvorschlaege[0].kandidaten[0].einzelstimmen =
          maxEinzelstimmenJeKandidat + numberOfVotesAboveLimit;

        const managed = useBearbeitenDialogStimmzettelUtils(
          ref(stimmzettelWithoutValuesSet),
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
        stimmzettelWithoutValuesSet.wahlvorschlaege[0].kandidaten[0].einzelstimmen =
          MAXIMAL_ERLAUBTE_STIMMEN_PRO_WAEHLER + numberOfVotesAboveLimit;

        const managed = useBearbeitenDialogStimmzettelUtils(
          ref(stimmzettelWithoutValuesSet),
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
      const k1 = prepareDseKandidat()
        .ordnungszahl(101)
        .einzelstimmen(null)
        .ungueltigeStimmen(0)
        .reststimmen(null)
        .durchgestrichen(false)
        .build();
      const k2 = prepareDseKandidat()
        .ordnungszahl(102)
        .einzelstimmen(null)
        .ungueltigeStimmen(0)
        .reststimmen(null)
        .durchgestrichen(false)
        .build();
      const wv = prepareDseWahlvorschlag()
        .ordnungszahl(1)
        .selected(false)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareDseStimmzettel().wahlvorschlaege([wv]).build();

      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
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
      const wv = prepareDseWahlvorschlag()
        .ordnungszahl(1)
        .selected(true)
        .kandidaten([])
        .build();
      const stimmzettel = prepareDseStimmzettel().wahlvorschlaege([wv]).build();
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      expect(() => managed.wahlvorschlagAddVotesOrThrow(1)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_throwError_when_wahlvorschlagNotFound", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(mockedStimmzettelWithoutWahlvorschlaege),
        mockedWahlId
      );
      expect(() => managed.wahlvorschlagAddVotesOrThrow(1)).toThrow(
        ManagedStimmzettelError
      );
    });
  });

  describe("wahlvorschlagRemoveVotesOrThrow", () => {
    it("should_deselectWahlvorschlagAndClearReststimmen_when_wahlvorschlagIsPresent", () => {
      const k1 = prepareDseKandidat().ordnungszahl(101).reststimmen(1).build();
      const k2 = prepareDseKandidat().ordnungszahl(102).reststimmen(1).build();
      const wv = prepareDseWahlvorschlag()
        .ordnungszahl(1)
        .selected(true)
        .kandidaten([k1, k2])
        .build();
      const stimmzettel = prepareDseStimmzettel().wahlvorschlaege([wv]).build();
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
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

    it("should_throwError_when_wahlvorschlagAlreadyDeselected", () => {
      const wv = prepareDseWahlvorschlag()
        .ordnungszahl(1)
        .selected(false)
        .kandidaten([])
        .build();
      const stimmzettel = prepareDseStimmzettel().wahlvorschlaege([wv]).build();
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(stimmzettel),
        mockedWahlId
      );
      expect(() => managed.wahlvorschlagRemoveVotesOrThrow(1)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_throwError_when_wahlvorschlagNotFound", () => {
      const managed = useBearbeitenDialogStimmzettelUtils(
        ref(mockedStimmzettelWithoutWahlvorschlaege),
        mockedWahlId
      );
      expect(() => managed.wahlvorschlagRemoveVotesOrThrow(1)).toThrow(
        ManagedStimmzettelError
      );
    });
  });

  describe("resetStimmzettelAndHistory", () => {
    const initialEmptyDseWahlvorschlag = prepareDseWahlvorschlag()
      .wahlvorschlagID("1")
      .ordnungszahl(1)
      .kandidaten([])
      .selected(false)
      .ungueltigeStimmen(0)
      .gueltigeStimmen(0)
      .erhaeltStimmen(true)
      .kurzname("kurzname")
      .build();

    const initialEmptyDseKandidat = prepareDseKandidatOfDseWahlvorschlag(
      initialEmptyDseWahlvorschlag
    )
      .ordnungszahl(101)
      .einzelstimmen(null)
      .ungueltigeStimmen(null)
      .reststimmen(null)
      .durchgestrichen(false)
      .owningWahlvorschlag(initialEmptyDseWahlvorschlag)
      .build();

    const initialEmptyDseStimzettel = prepareDseStimmzettel()
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

    it("should_resetStimmzettelChangeHistoryAndReststimmenError_when_CalledWithReference", async () => {
      const managedStimmzettel = useBearbeitenDialogStimmzettelUtils(
        // structuredClone creates deep copy, so mutations are only applied to copy,
        // not to "initialEmptyDseStimzettel"
        ref(structuredClone(initialEmptyDseStimzettel)),
        mockedWahlId
      );

      const stimmzettelToResetTo = preparePersistedStimmzettel()
        .invalideVotes(3)
        .wahlvorstandBeschlussvorschlag([])
        .systemBeschlussvorschlag([])
        .beschlussfassung(null)
        .wahlvorschlaege([
          preparePersistedStimmzettelWahlvorschlag()
            .wahlvorschlagID(initialEmptyDseWahlvorschlag.wahlvorschlagID)
            .selected(true)
            .kandidaten([
              preparePersistedStimmzettelKandidat()
                .kandidatId(initialEmptyDseKandidat.kandidatId)
                .nennung(initialEmptyDseKandidat.nennung)
                .votesByVoter(5)
                .invalidVotes(1)
                .votesByWahlvorschlag(2)
                .isDiscarded(true)
                .build(),
            ])
            .build(),
        ])
        .build();

      const expectedMappedDseStimmzettelAfterReset = structuredClone(
        initialEmptyDseStimzettel
      );
      expectedMappedDseStimmzettelAfterReset.invalideVotes = 3;
      expectedMappedDseStimmzettelAfterReset.wahlvorschlaege[0].selected = true;
      const expectedK =
        expectedMappedDseStimmzettelAfterReset.wahlvorschlaege[0].kandidaten[0];
      expectedK.einzelstimmen = 5;
      expectedK.ungueltigeStimmen = 1;
      expectedK.reststimmen = 2;
      expectedK.durchgestrichen = true;
      expectedMappedDseStimmzettelAfterReset.systemBeschlussvorschlag = [
        { reason: SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig },
        {
          reason:
            SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit,
        },
      ];
      expectedMappedDseStimmzettelAfterReset.wahlvorstandBeschlussvorschlag =
        [];
      expectedMappedDseStimmzettelAfterReset.beschlussfassung = null;
      expectedMappedDseStimmzettelAfterReset.gueltigkeit =
        StimmzettelGueltigkeitEnum.BeschlussAusstehend;

      // mock the mapper to apply the persisted values onto the existing DSE object
      mockDefinitions.mapPersistedStimmzettelValuesToExistingDseStimmzettel.mockImplementation(
        (target: DseStimmzettel, source: PersistedStimmzettel) => {
          const srcWv = source.wahlvorschlaege[0];
          // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
          const tgtWv = target.wahlvorschlaege.find(
            (w) => w.wahlvorschlagID === srcWv.wahlvorschlagID
          )!;
          tgtWv.selected = srcWv.selected;
          const srcK = srcWv.kandidaten[0];
          // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
          const tgtK = tgtWv.kandidaten.find(
            (k) =>
              k.kandidatId === srcK.kandidatId && k.nennung === srcK.nennung
          )!;
          tgtK.einzelstimmen = srcK.votesByVoter ?? null;
          tgtK.ungueltigeStimmen = srcK.invalidVotes ?? null;
          tgtK.reststimmen = srcK.votesByWahlvorschlag ?? null;
          tgtK.durchgestrichen = srcK.isDiscarded ?? false;
          if (target.invalideVotes !== source.invalideVotes) {
            target.invalideVotes = source.invalideVotes;
          }
        }
      );

      expect(managedStimmzettel.stimmzettel.value).toStrictEqual(
        initialEmptyDseStimzettel
      );

      // mutate managedStimmzettel so reset has an effect
      managedStimmzettel.kandidatAddStreichungOrThrow(
        initialEmptyDseKandidat.ordnungszahl
      );
      managedStimmzettel.kandidatAddEinzelstimmenOrThrow(
        initialEmptyDseKandidat.ordnungszahl,
        2
      );
      managedStimmzettel.wahlvorschlagAddVotesOrThrow(
        initialEmptyDseWahlvorschlag.ordnungszahl
      );
      managedStimmzettel.stimmzettel.value.invalideVotes = 3;

      expect(managedStimmzettel.stimmzettel.value).not.toStrictEqual(
        initialEmptyDseStimzettel
      );

      // clear refreshWahlvorschlaegeVotes, because it`s not only called on reset but additionally after every
      // manipulation. Flush pending watchers first to avoid counting their calls after clear.
      await nextTick();
      mockDefinitions.reststimmeUtils.refreshWahlvorschlaegeVotes.mockClear();

      managedStimmzettel.resetStimmzettelAndHistory(stimmzettelToResetTo);
      await nextTick();

      const stimmzettelAfterReset = managedStimmzettel.stimmzettel.value;
      expect(stimmzettelAfterReset).toStrictEqual(
        expectedMappedDseStimmzettelAfterReset
      );
      expect(mockDefinitions.changeHistory.reset).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.resetError).toHaveBeenCalledTimes(1);
      expect(
        mockDefinitions.reststimmeUtils.refreshWahlvorschlaegeVotes
      ).toHaveBeenCalledTimes(1);
    });

    it("should_resetStimmzettelChangeHistoryAndReststimmenError_when_calledWithoutReference", () => {
      const managedStimmzettel = useBearbeitenDialogStimmzettelUtils(
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
        2
      );
      managedStimmzettel.wahlvorschlagAddVotesOrThrow(
        initialEmptyDseWahlvorschlag.ordnungszahl
      );

      expect(managedStimmzettel.stimmzettel.value).not.toStrictEqual(
        initialEmptyDseStimzettel
      );

      // clear refreshWahlvorschlaegeVotes, because it`s not only called on reset but additionally after every
      // manipulation
      mockDefinitions.reststimmeUtils.refreshWahlvorschlaegeVotes.mockClear();
      mockDefinitions.resetDseStimmzettel.mockReturnValue(
        structuredClone(initialEmptyDseStimzettel)
      );

      managedStimmzettel.resetStimmzettelAndHistory();

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
      expect(mockDefinitions.changeHistory.reset).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.resetError).toHaveBeenCalledTimes(1);
      expect(
        mockDefinitions.reststimmeUtils.refreshWahlvorschlaegeVotes
      ).toHaveBeenCalledTimes(1);
    });
  });
});
