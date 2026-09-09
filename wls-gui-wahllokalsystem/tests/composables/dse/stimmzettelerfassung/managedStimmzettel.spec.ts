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
      expect(wv.selected).toBe(true);
      expect(k1.reststimmen).toBe(1);
      expect(k2.reststimmen).toBe(1);
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
      expect(wv.selected).toBe(false);
      expect(k1.reststimmen).toBe(0);
      expect(k2.reststimmen).toBe(0);
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
});
