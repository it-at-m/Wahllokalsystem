import { createTestingPinia } from "@pinia/testing";
import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
import { useWahlvorstandTestDataFactory } from "@tests/utils/wahlvorstand/WahlvorstandTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick, ref } from "vue";

import { useTheBeschlussfassungBearbeitenDialogUtils } from "@/composables/dse/beschlussfassung/theBeschlussfassungBearbeitenDialogUtils.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit: vi
    .fn()
    .mockReturnValue({ andererGrund: "", beschlussgruende: [] }),
  isStimmzettelGueltigBasedOnVormerkungsgruenden: vi.fn().mockReturnValue(true),
  getBeschlussgrundEnumValueAsString: vi.fn((v: string) => `Mapped(${v})`),
}));

vi.mock(
  "@/composables/dse/beschlussfassung/theBeschlussFassenTabUtils.ts",
  () => {
    return {
      useTheBeschlussFassenTabUtils: () => ({
        createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit:
          mockDefinitions.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit,
        isStimmzettelGueltigBasedOnVormerkungsgruenden:
          mockDefinitions.isStimmzettelGueltigBasedOnVormerkungsgruenden,
      }),
    };
  }
);

vi.mock("@/composables/dse/beschlussfassung/beschlussgrundTools.ts", () => {
  return {
    useBeschlussgrundTools: () => ({
      getBeschlussgrundEnumValueAsString:
        mockDefinitions.getBeschlussgrundEnumValueAsString,
    }),
  };
});

describe("theBeschlussfassungBearbeitenDialogUtils.ts", () => {
  const {
    preparePersistedStimmzettel,
    preparePersistedStimmzettelBeschlussfassung,
  } = usePersistedStimmzettelTestDataFactory();
  const { createWahlvorstand } = useWahlvorstandTestDataFactory();

  let store: ReturnType<typeof useWahlvorstandStore>;

  beforeEach(() => {
    const pinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    store = useWahlvorstandStore(pinia);
    store.wahlvorstand = createWahlvorstand(10);

    vi.clearAllMocks();
  });

  it("should_initializeDefaults_when_stimmzettelIsUndefined", () => {
    const stimmzettel = ref(undefined);

    const unitUnderTest =
      useTheBeschlussfassungBearbeitenDialogUtils(stimmzettel);

    expect(unitUnderTest.abstimmungsergebnis.value).toStrictEqual({
      stimmenDafuer: null,
      stimmenDagegen: null,
      hasWahlvorsteherVotedDafuer: false,
      abstimmungIsUnentschieden: false,
      abstimmungIsUngueltig: false,
    });
    expect(unitUnderTest.beschlussDetails.value).toStrictEqual({
      isGueltig: null,
      beschlussgruende: [],
      andererGrund: "",
      andererGrundChecked: false,
      beschlussText: "",
    });
    expect(
      unitUnderTest.stimmzettelGueltigkeitAusBeschluss.value
    ).toStrictEqual(StimmzettelGueltigkeitEnum.BeschlussAusstehend);
  });

  it("should_populateFromBeschlussfassung_when_present", async () => {
    store.lastSavedAnwesendeWahlvorstandsmitgliederAnzahl = 4;

    const beschlussfassung = preparePersistedStimmzettelBeschlussfassung()
      .text("Beschlusstext")
      .pro(2)
      .contra(2)
      .build();
    const stimmzettelRef = ref(
      preparePersistedStimmzettel()
        .beschlussfassung(beschlussfassung)
        .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
        .build()
    );

    mockDefinitions.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit.mockReturnValue(
      {
        andererGrund: "Anderer Grund",
        beschlussgruende: [
          { grund: "A", selected: true },
          { grund: "B", selected: false },
        ],
      }
    );

    const unitUnderTest =
      useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);

    await nextTick();

    expect(unitUnderTest.abstimmungsergebnis.value).toStrictEqual({
      stimmenDafuer: 2,
      stimmenDagegen: 2,
      hasWahlvorsteherVotedDafuer: true,
      abstimmungIsUnentschieden: true,
      abstimmungIsUngueltig: false,
    });
    expect(unitUnderTest.beschlussDetails.value.isGueltig).toStrictEqual(true);
    expect(unitUnderTest.beschlussDetails.value.beschlussText).toStrictEqual(
      "Beschlusstext"
    );
    expect(
      unitUnderTest.stimmzettelGueltigkeitAusBeschluss.value
    ).toStrictEqual(StimmzettelGueltigkeitEnum.Valid);

    expect(unitUnderTest.beschlussDetails.value.andererGrund).toStrictEqual(
      "Anderer Grund"
    );
    expect(
      unitUnderTest.beschlussDetails.value.andererGrundChecked
    ).toStrictEqual(true);
    expect(unitUnderTest.beschlussDetails.value.beschlussgruende).toStrictEqual(
      [
        { grund: "A", selected: true },
        { grund: "B", selected: false },
      ]
    );
  });

  it("should_buildBeschlussTextFromSelectedGruendeAndAndererGrund_when_noBeschlussfassung", async () => {
    const stimmzettelRef = ref(
      preparePersistedStimmzettel().beschlussfassung(null).build()
    );

    mockDefinitions.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit.mockReturnValue(
      {
        andererGrund: "Freitext",
        beschlussgruende: [
          { grund: "X", selected: true },
          { grund: "Y", selected: false },
        ],
      }
    );

    const unitUnderTest =
      useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);

    await nextTick();

    // Da kein vorgegebener Beschlusstext vorhanden ist, wird er aus Gründen zusammengebaut
    expect(unitUnderTest.beschlussDetails.value.beschlussText).toStrictEqual(
      "Mapped(X), Freitext"
    );
    expect(
      mockDefinitions.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit
    ).toHaveBeenCalled();
  });

  it("should_updateGruende_when_isGueltigChanges", async () => {
    const stimmzettelRef = ref(
      preparePersistedStimmzettel().beschlussfassung(null).build()
    );

    mockDefinitions.isStimmzettelGueltigBasedOnVormerkungsgruenden.mockReturnValue(
      false
    );
    // Erste Rückgabe
    mockDefinitions.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit.mockReturnValueOnce(
      {
        andererGrund: "A1",
        beschlussgruende: [{ grund: "G1", selected: true }],
      }
    );

    const unitUnderTest =
      useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);
    await nextTick();
    expect(unitUnderTest.beschlussDetails.value.andererGrund).toStrictEqual(
      "A1"
    );

    // Zweite Rückgabe nach Änderung von isGueltig
    mockDefinitions.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit.mockReturnValueOnce(
      {
        andererGrund: "A2",
        beschlussgruende: [
          { grund: "G2", selected: false },
          { grund: "G3", selected: true },
        ],
      }
    );

    unitUnderTest.beschlussDetails.value.isGueltig = true;
    await nextTick();

    expect(unitUnderTest.beschlussDetails.value.andererGrund).toStrictEqual(
      "A2"
    );
    expect(unitUnderTest.beschlussDetails.value.beschlussgruende).toStrictEqual(
      [
        { grund: "G2", selected: false },
        { grund: "G3", selected: true },
      ]
    );
  });

  describe("abstimmungsergebnis.ungueltig", () => {
    it("should_markUngueltig_when_totalStimmenLowerThan3", async () => {
      const stimmzettelRef = ref(
        preparePersistedStimmzettel().beschlussfassung(null).build()
      );
      const unitUnderTest =
        useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);

      unitUnderTest.abstimmungsergebnis.value.stimmenDafuer = 1;
      unitUnderTest.abstimmungsergebnis.value.stimmenDagegen = 1;
      await nextTick();

      expect(
        unitUnderTest.abstimmungsergebnis.value.abstimmungIsUngueltig
      ).toStrictEqual(true);
    });

    it("should_markUngueltig_when_totalStimmenHigherThanAnwesende", async () => {
      const stimmzettelRef = ref(
        preparePersistedStimmzettel().beschlussfassung(null).build()
      );
      const unitUnderTest =
        useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);

      // 10 anwesend aus beforeEach; total = 11
      unitUnderTest.abstimmungsergebnis.value.stimmenDafuer = 6;
      unitUnderTest.abstimmungsergebnis.value.stimmenDagegen = 5;
      await nextTick();

      expect(
        unitUnderTest.abstimmungsergebnis.value.abstimmungIsUngueltig
      ).toStrictEqual(true);
    });

    it("should_markUngueltig_when_stimmenDagegenHigherThanStimmenDafuer", async () => {
      const stimmzettelRef = ref(
        preparePersistedStimmzettel().beschlussfassung(null).build()
      );
      const unitUnderTest =
        useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);

      unitUnderTest.abstimmungsergebnis.value.stimmenDafuer = 3;
      unitUnderTest.abstimmungsergebnis.value.stimmenDagegen = 4;
      await nextTick();

      expect(
        unitUnderTest.abstimmungsergebnis.value.abstimmungIsUngueltig
      ).toStrictEqual(true);
    });

    it("should_markUngueltig_when_anyStimmeIsNegative", async () => {
      const stimmzettelRef = ref(
        preparePersistedStimmzettel().beschlussfassung(null).build()
      );
      const unitUnderTest =
        useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);

      unitUnderTest.abstimmungsergebnis.value.stimmenDafuer = -1;
      unitUnderTest.abstimmungsergebnis.value.stimmenDagegen = -1;
      await nextTick();

      expect(
        unitUnderTest.abstimmungsergebnis.value.abstimmungIsUngueltig
      ).toStrictEqual(true);
    });
  });

  describe("abstimmungsergebnis.unentschieden", () => {
    it("should_markUnentschieden_when_stimmenAreEqualAndNotUngueltig", async () => {
      store.lastSavedAnwesendeWahlvorstandsmitgliederAnzahl = 6;

      const stimmzettelRef = ref(
        preparePersistedStimmzettel().beschlussfassung(null).build()
      );
      const unitUnderTest =
        useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);

      unitUnderTest.abstimmungsergebnis.value.stimmenDafuer = 3;
      unitUnderTest.abstimmungsergebnis.value.stimmenDagegen = 3;
      await nextTick();

      expect(
        unitUnderTest.abstimmungsergebnis.value.abstimmungIsUnentschieden
      ).toStrictEqual(true);
    });
  });

  describe("isBeschlussSpeichernButtonDisabled", () => {
    it("should_beTrue_when_beschlussAusstehendAndNoValuesSelectedEvenIfVotesAreValid", async () => {
      const stimmzettelRef = ref(
        preparePersistedStimmzettel().beschlussfassung(null).build()
      );

      mockDefinitions.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit.mockReturnValue(
        {
          andererGrund: "",
          beschlussgruende: [{ grund: "A", selected: false }],
        }
      );

      const unitUnderTest =
        useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);
      await nextTick();

      unitUnderTest.abstimmungsergebnis.value.stimmenDafuer = 3;
      unitUnderTest.abstimmungsergebnis.value.stimmenDagegen = 2;
      await nextTick();

      expect(unitUnderTest.isBeschlussSpeichernButtonDisabled.value).toBe(true);
    });

    it("should_beFalse_when_beschlussAusstehendAndValuesSelectedAndVotesAreValid", async () => {
      store.lastSavedAnwesendeWahlvorstandsmitgliederAnzahl = 5;

      const stimmzettelRef = ref(
        preparePersistedStimmzettel().beschlussfassung(null).build()
      );

      mockDefinitions.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit.mockReturnValue(
        {
          andererGrund: "",
          beschlussgruende: [{ grund: "A", selected: true }],
        }
      );

      const unitUnderTest =
        useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);
      await nextTick();

      unitUnderTest.abstimmungsergebnis.value.stimmenDafuer = 3;
      unitUnderTest.abstimmungsergebnis.value.stimmenDagegen = 2;
      await nextTick();

      expect(unitUnderTest.isBeschlussSpeichernButtonDisabled.value).toBe(
        false
      );
    });

    it("should_beTrue_when_beschlussAusstehendAndVotesAreUngueltig", async () => {
      const stimmzettelRef = ref(
        preparePersistedStimmzettel().beschlussfassung(null).build()
      );

      mockDefinitions.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit.mockReturnValue(
        {
          andererGrund: "",
          beschlussgruende: [{ grund: "A", selected: true }],
        }
      );

      const unitUnderTest =
        useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);
      await nextTick();

      unitUnderTest.abstimmungsergebnis.value.stimmenDafuer = 1;
      unitUnderTest.abstimmungsergebnis.value.stimmenDagegen = 1; // total < 3 => ungueltig
      await nextTick();

      expect(unitUnderTest.isBeschlussSpeichernButtonDisabled.value).toBe(true);
    });

    it("should_beTrue_when_beschlussAusstehendAndVotesAreUnentschiedenWithoutWahlvorsteherVote", async () => {
      const stimmzettelRef = ref(
        preparePersistedStimmzettel().beschlussfassung(null).build()
      );

      mockDefinitions.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit.mockReturnValue(
        {
          andererGrund: "",
          beschlussgruende: [{ grund: "A", selected: true }],
        }
      );

      const unitUnderTest =
        useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);
      await nextTick();

      unitUnderTest.abstimmungsergebnis.value.stimmenDafuer = 3;
      unitUnderTest.abstimmungsergebnis.value.stimmenDagegen = 3;
      unitUnderTest.abstimmungsergebnis.value.hasWahlvorsteherVotedDafuer = false;
      await nextTick();

      expect(unitUnderTest.isBeschlussSpeichernButtonDisabled.value).toBe(true);
    });

    it("should_beFalse_when_beschlussAusstehendAndVotesAreUnentschiedenWithWahlvorsteherVote", async () => {
      store.lastSavedAnwesendeWahlvorstandsmitgliederAnzahl = 10;

      const stimmzettelRef = ref(
        preparePersistedStimmzettel().beschlussfassung(null).build()
      );

      mockDefinitions.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit.mockReturnValue(
        {
          andererGrund: "",
          beschlussgruende: [{ grund: "A", selected: true }],
        }
      );

      const unitUnderTest =
        useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);
      await nextTick();

      unitUnderTest.abstimmungsergebnis.value.stimmenDafuer = 5;
      unitUnderTest.abstimmungsergebnis.value.stimmenDagegen = 5;
      unitUnderTest.abstimmungsergebnis.value.hasWahlvorsteherVotedDafuer = true;
      await nextTick();

      expect(unitUnderTest.isBeschlussSpeichernButtonDisabled.value).toBe(
        false
      );
    });

    it("should_beTrue_whenAnyVoteIsNull", async () => {
      const stimmzettelRef = ref(
        preparePersistedStimmzettel().beschlussfassung(null).build()
      );

      mockDefinitions.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit.mockReturnValue(
        {
          andererGrund: "",
          beschlussgruende: [{ grund: "A", selected: true }],
        }
      );

      const unitUnderTest =
        useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);
      await nextTick();

      unitUnderTest.abstimmungsergebnis.value.stimmenDafuer = 3;
      unitUnderTest.abstimmungsergebnis.value.stimmenDagegen = null;
      await nextTick();

      expect(unitUnderTest.isBeschlussSpeichernButtonDisabled.value).toBe(true);
    });

    it("should_beFalseAndIgnoreThatNoValuesAreSelected_when_beschlussIsAlreadyGefasst", async () => {
      store.lastSavedAnwesendeWahlvorstandsmitgliederAnzahl = 7;

      const beschlussfassung = preparePersistedStimmzettelBeschlussfassung()
        .pro(4)
        .contra(3)
        .build();
      const stimmzettelRef = ref(
        preparePersistedStimmzettel()
          .beschlussfassung(beschlussfassung)
          .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
          .build()
      );

      mockDefinitions.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit.mockReturnValue(
        {
          andererGrund: "",
          beschlussgruende: [{ grund: "A", selected: false }],
        }
      );

      const unitUnderTest =
        useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);
      await nextTick();

      expect(unitUnderTest.isBeschlussGefasst.value).toBe(true);
      expect(unitUnderTest.isBeschlussSpeichernButtonDisabled.value).toBe(
        false
      );
    });

    it("should_beTrue_when_beschlussIsAlreadyGefasstButAbstimmungIsUnentschiedenWithoutWahlvorsteherVote", async () => {
      const beschlussfassung = preparePersistedStimmzettelBeschlussfassung()
        .pro(5)
        .contra(5)
        .build();
      const stimmzettelRef = ref(
        preparePersistedStimmzettel()
          .beschlussfassung(beschlussfassung)
          .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
          .build()
      );

      mockDefinitions.createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit.mockReturnValue(
        {
          andererGrund: "",
          beschlussgruende: [{ grund: "A", selected: false }],
        }
      );

      const unitUnderTest =
        useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);
      await nextTick();

      unitUnderTest.abstimmungsergebnis.value.hasWahlvorsteherVotedDafuer = false;
      await nextTick();

      expect(unitUnderTest.isBeschlussGefasst.value).toBe(true);
      expect(unitUnderTest.isBeschlussSpeichernButtonDisabled.value).toBe(true);
    });

    it("should_beTrue_when_beschlussIsAlreadyGefasstButVotesAreUngueltig", async () => {
      const beschlussfassung = preparePersistedStimmzettelBeschlussfassung()
        .pro(0)
        .contra(0)
        .build();
      const stimmzettelRef = ref(
        preparePersistedStimmzettel()
          .beschlussfassung(beschlussfassung)
          .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
          .build()
      );

      const unitUnderTest =
        useTheBeschlussfassungBearbeitenDialogUtils(stimmzettelRef);
      await nextTick();

      expect(unitUnderTest.isBeschlussGefasst.value).toBe(true);
      expect(unitUnderTest.isBeschlussSpeichernButtonDisabled.value).toBe(true);
    });
  });
});
