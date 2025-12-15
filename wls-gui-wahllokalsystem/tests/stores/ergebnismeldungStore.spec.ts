import type { BezirkUndWahlIDStapelArt } from "@/types/ergebnismeldung/common/BezirkUndWahlIDStapelArt.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useBegruendungTestDataFactory } from "@tests/utils/ergebnismeldung/common/begruendungTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisse: vi.fn(),
  postErgebnisse: vi.fn(),
  getBegruendungStimmzettelumschlaege: vi.fn(),
  postBegruendung: vi.fn(),
}));

vi.mock("@/composables/ergebnismeldung/common/ergebnisService.ts", () => ({
  useErgebnisService: () => ({
    getErgebnisse: mockDefinitions.getErgebnisse,
    postErgebnisse: mockDefinitions.postErgebnisse,
  }),
}));
vi.mock(
  "@/composables/ergebnisermittlung/ergebnisermittlungService.ts",
  () => ({
    useErgebnisermittlungService: () => ({
      getBegruendungStimmzettelumschlaege:
        mockDefinitions.getBegruendungStimmzettelumschlaege,
      postBegruendung: mockDefinitions.postBegruendung,
    }),
  })
);

const { generateRandomString, generateRandomNumber, getRandomItem } =
  useCommonTestDataFactory();
const { createErgebnis, prepareErgebnis, createErgebnisse, prepareErgebnisse } =
  useErgebnisseTestDataFactory();
const { prepareUser } = useUserTestDataFactory();
const { prepareWahl } = useWahlTestDataFactory();
const { createBegruendung } = useBegruendungTestDataFactory();
const { prepareBezirkUndWahlIDStapelart } =
  useCommonErgebnismeldungTestDataFactory();

describe("ergebnismeldungStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useErgebnismeldungStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useErgebnismeldungStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("deleteErgebnisseWithNumIndexAbove", () => {
    it("should_removeItemsWithNumIndexAboveGivenValue_when_ergebnisseForWahlIdAndStapelArtExists", () => {
      const wahlID = generateRandomString(10);
      const stapelArt = getRandomItem(Object.values(StapelArtEnum));
      const maxAllowedNumIndex = generateRandomNumber(3);

      const itemToKeep1 = prepareErgebnis()
        .numIndex(maxAllowedNumIndex - 1)
        .build();
      const itemToKeep2 = prepareErgebnis()
        .numIndex(maxAllowedNumIndex)
        .build();
      const itemToDelete1 = prepareErgebnis()
        .numIndex(maxAllowedNumIndex + 1)
        .build();
      const itemToDelete2 = prepareErgebnis()
        .numIndex(maxAllowedNumIndex + generateRandomNumber(3))
        .build();
      unitUnderTest.ergebnisse = [
        prepareErgebnisse()
          .bezirkUndWahlIDStapelart({
            wahlID,
            wahlbezirkID: generateRandomString(10),
            stapelArt,
          })
          .ergebnisse([itemToKeep1, itemToDelete1, itemToDelete2, itemToKeep2])
          .build(),
      ];

      unitUnderTest.deleteErgebnisseWithNumIndexAbove(
        wahlID,
        stapelArt,
        maxAllowedNumIndex
      );
      expect(unitUnderTest.ergebnisse[0]?.ergebnisse).toStrictEqual([
        itemToKeep1,
        itemToKeep2,
      ]);
    });

    it("should_doNothing_when_noErgebnisseForWahlIdAndStapelArtExists", () => {
      const wahlID = generateRandomString(10);
      const stapelArt = getRandomItem(Object.values(StapelArtEnum));
      const maxAllowedNumIndex = generateRandomNumber(3);

      unitUnderTest.ergebnisse = [];

      unitUnderTest.deleteErgebnisseWithNumIndexAbove(
        wahlID,
        stapelArt,
        maxAllowedNumIndex
      );
      expect(unitUnderTest.ergebnisse.length).toStrictEqual(0);
    });

    it("should_doNothing_when_noErgebnisseWithNumIndexAboveValueExists", () => {
      const wahlID = generateRandomString(10);
      const stapelArt = getRandomItem(Object.values(StapelArtEnum));
      const maxAllowedNumIndex = generateRandomNumber(3);

      const itemToKeep1 = prepareErgebnis()
        .numIndex(maxAllowedNumIndex - 1)
        .build();
      const itemToKeep2 = prepareErgebnis()
        .numIndex(maxAllowedNumIndex)
        .build();
      unitUnderTest.ergebnisse = [
        prepareErgebnisse()
          .bezirkUndWahlIDStapelart({
            wahlID,
            wahlbezirkID: generateRandomString(10),
            stapelArt,
          })
          .ergebnisse([itemToKeep1, itemToKeep2])
          .build(),
      ];

      unitUnderTest.deleteErgebnisseWithNumIndexAbove(
        wahlID,
        stapelArt,
        maxAllowedNumIndex
      );
      expect(unitUnderTest.ergebnisse[0]?.ergebnisse).toStrictEqual([
        itemToKeep1,
        itemToKeep2,
      ]);
    });
  });

  describe("getErgebnisseAndCreateIfMissing", () => {
    it("should_returnExistingErgebnisse_when_ergebnisseForWahlIdAndStapelartExist", () => {
      const wahlID = generateRandomString(10);
      const stapelArt = getRandomItem(Object.values(StapelArtEnum));

      const ergebnisseToFind = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID,
          wahlbezirkID: generateRandomString(10),
          stapelArt,
        })
        .ergebnisse([createErgebnis(), createErgebnis()])
        .build();
      const ergebnisseToIgnore = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID + generateRandomString(2),
          wahlbezirkID: generateRandomString(10),
          stapelArt,
        })
        .ergebnisse([createErgebnis(), createErgebnis()])
        .build();
      unitUnderTest.ergebnisse = [ergebnisseToFind, ergebnisseToIgnore];

      const result = unitUnderTest.getErgebnisseAndCreateIfMissing({
        wahlID,
        wahlbezirkID: generateRandomString(10),
        stapelArt,
      });
      expect(result).toStrictEqual(ergebnisseToFind);
    });

    it("should_returnNewErgebnisse_when_ergebnisseForWahlIdAndStapelartDoesNotExist", () => {
      const wahlID = generateRandomString(10);
      const stapelArt = getRandomItem(Object.values(StapelArtEnum));

      const ergebnisseToIgnore = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID + generateRandomString(2),
          wahlbezirkID: generateRandomString(10),
          stapelArt,
        })
        .ergebnisse([createErgebnis(), createErgebnis()])
        .build();
      unitUnderTest.ergebnisse = [ergebnisseToIgnore];

      const wahlbezirkID = generateRandomString(10);
      const result = unitUnderTest.getErgebnisseAndCreateIfMissing({
        wahlID,
        wahlbezirkID,
        stapelArt,
      });
      const ergebnisseToCreat = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID,
          wahlbezirkID,
          stapelArt,
        })
        .ergebnisse([])
        .build();
      expect(result).toStrictEqual(ergebnisseToCreat);
    });
  });

  describe("loadErgebnisseByStapelArt", () => {
    it("should_loadErgebnisseByStapelArtAndReturnNull_when_calledAndNoErgebnisseFound", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;

      const userStore = useUserStore();
      userStore.setUser(
        prepareUser()
          .wahlMetaData([
            { wahlbezirkID: wahlbezirkID, wahlID: wahlID, wahlnummer: "0" },
          ])
          .build()
      );

      mockDefinitions.getErgebnisse.mockResolvedValue(null);

      await unitUnderTest.loadErgebnisseByStapelArt(wahlID, stapelArt);

      expect(mockDefinitions.getErgebnisse.mock.calls).toStrictEqual([
        [wahlbezirkID, wahlID, stapelArt, true],
      ]);
      expect(unitUnderTest.ergebnisse).toStrictEqual([]);
    });

    it("should_loadErgebnisseByStapelArt_when_calledAndErgebnisseFound", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;

      const userStore = useUserStore();
      userStore.setUser(
        prepareUser()
          .wahlMetaData([
            { wahlbezirkID: wahlbezirkID, wahlID: wahlID, wahlnummer: "0" },
          ])
          .build()
      );

      const mockedErgebnisseModel = createErgebnisse();

      mockDefinitions.getErgebnisse.mockResolvedValue(mockedErgebnisseModel);

      await unitUnderTest.loadErgebnisseByStapelArt(wahlID, stapelArt);

      expect(mockDefinitions.getErgebnisse.mock.calls).toStrictEqual([
        [wahlbezirkID, wahlID, stapelArt, true],
      ]);
      expect(unitUnderTest.ergebnisse).toStrictEqual([mockedErgebnisseModel]);
    });

    it("should_throwError_when_calledServiceThrowsError", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;

      const userStore = useUserStore();
      userStore.setUser(
        prepareUser()
          .wahlMetaData([
            { wahlbezirkID: wahlbezirkID, wahlID: wahlID, wahlnummer: "0" },
          ])
          .build()
      );

      mockDefinitions.getErgebnisse.mockRejectedValue(
        new Error("service call failed")
      );

      await expect(
        unitUnderTest.loadErgebnisseByStapelArt(wahlID, stapelArt)
      ).rejects.toThrow();
    });
  });

  describe("sendErgebnisseByStapelArt", () => {
    it("should_sendErgebnisse_when_calledWithStapelartAndWahlId", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;

      const userStore = useUserStore();
      userStore.setUser(
        prepareUser()
          .wahlMetaData([
            { wahlbezirkID: wahlbezirkID, wahlID: wahlID, wahlnummer: "0" },
          ])
          .build()
      );

      const mockedErgebnisseModel = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
          stapelArt: stapelArt,
        })
        .build();
      const mockedErgebnisseModelNotToSend = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: "otherID",
          wahlbezirkID: "ID",
          stapelArt: StapelArtEnum.SrwBawA,
        })
        .build();
      unitUnderTest.ergebnisse = [
        mockedErgebnisseModel,
        mockedErgebnisseModelNotToSend,
      ];

      mockDefinitions.postErgebnisse.mockResolvedValue({});

      expect(unitUnderTest.isErgebnisseSaving).toStrictEqual(false);
      const saveErgebnissePromise = unitUnderTest.sendErgebnisseByStapelArt(
        wahlID,
        stapelArt
      );
      expect(unitUnderTest.isErgebnisseSaving).toStrictEqual(true);

      await saveErgebnissePromise;

      expect(unitUnderTest.isErgebnisseSaving).toStrictEqual(false);
      expect(mockDefinitions.postErgebnisse.mock.calls).toStrictEqual([
        [wahlbezirkID, wahlID, stapelArt, mockedErgebnisseModel, true],
      ]);
      expect(mockDefinitions.postErgebnisse.mock.calls).not.toEqual([
        [
          "ID",
          "otherID",
          StapelArtEnum.SrwBawA,
          mockedErgebnisseModelNotToSend,
        ],
      ]);
    });

    it("should_sendEmptyErgebnisse_when_noErgebnisseAreGiven", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;

      const userStore = useUserStore();
      userStore.setUser(
        prepareUser()
          .wahlMetaData([
            { wahlbezirkID: wahlbezirkID, wahlID: wahlID, wahlnummer: "0" },
          ])
          .build()
      );

      const mockedErgebnisseModel = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
          stapelArt: stapelArt,
        })
        .ergebnisse([])
        .build();
      unitUnderTest.ergebnisse = [mockedErgebnisseModel];

      mockDefinitions.postErgebnisse.mockResolvedValue({});

      await unitUnderTest.sendErgebnisseByStapelArt(wahlID, stapelArt, true);

      expect(mockDefinitions.postErgebnisse.mock.calls).toStrictEqual([
        [wahlbezirkID, wahlID, stapelArt, mockedErgebnisseModel, true],
      ]);
    });

    it("should_throwError_when_calledServiceThrowsError", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stapelArt = StapelArtEnum.ObwA;

      const userStore = useUserStore();
      userStore.setUser(
        prepareUser()
          .wahlMetaData([
            { wahlbezirkID: wahlbezirkID, wahlID: wahlID, wahlnummer: "0" },
          ])
          .build()
      );
      unitUnderTest.ergebnisse = [
        prepareErgebnisse()
          .bezirkUndWahlIDStapelart({
            wahlID: wahlID,
            wahlbezirkID: wahlbezirkID,
            stapelArt: stapelArt,
          })
          .build(),
      ];

      mockDefinitions.postErgebnisse.mockRejectedValue(
        new Error("service call failed")
      );

      await expect(
        unitUnderTest.sendErgebnisseByStapelArt(wahlID, stapelArt)
      ).rejects.toThrow();
    });
  });

  describe("findAndUpdateErgebnisseByWahlIdAndStapelArt", () => {
    it("should_updateErgebnisseForStapelB_when_existingErgebnisseFound", () => {
      const wahlID = "id";
      const stapelArt = StapelArtEnum.ObwBLeer;

      const ergebnisBeforeUpdating = 5;
      const ergebnisAfterUpdating = 38;

      unitUnderTest.ergebnisse = [
        prepareErgebnisse()
          .bezirkUndWahlIDStapelart({
            wahlID: wahlID,
            wahlbezirkID: "wahlbezirkID",
            stapelArt: stapelArt,
          })
          .ergebnisse([
            prepareErgebnis().ergebnis(ergebnisBeforeUpdating).build(),
          ])
          .build(),
      ];

      expect(
        unitUnderTest.ergebnisse[0]?.ergebnisse[0]?.ergebnis
      ).toStrictEqual(ergebnisBeforeUpdating);

      unitUnderTest.findAndUpdateErgebnisseByWahlIdAndStapelArt(
        wahlID,
        stapelArt,
        [prepareErgebnis().ergebnis(ergebnisAfterUpdating).build()]
      );

      expect(
        unitUnderTest.ergebnisse[0]?.ergebnisse[0]?.ergebnis
      ).toStrictEqual(ergebnisAfterUpdating);
    });

    it("should_createNewErgebnisseForStapelB_when_noExistingErgebnisseFound", () => {
      const wahlID = "id";
      const stapelArt = StapelArtEnum.ObwBLeer;
      const userStore = useUserStore();
      userStore.setUser(
        prepareUser()
          .wahlMetaData([
            { wahlbezirkID: "wahlbezirkID", wahlID: wahlID, wahlnummer: "0" },
          ])
          .build()
      );

      const ergebnisAfterUpdating = 38;

      unitUnderTest.ergebnisse = [];

      expect(unitUnderTest.ergebnisse.length).toStrictEqual(0);

      unitUnderTest.findAndUpdateErgebnisseByWahlIdAndStapelArt(
        wahlID,
        stapelArt,
        [prepareErgebnis().ergebnis(ergebnisAfterUpdating).build()]
      );

      expect(unitUnderTest.ergebnisse.length).toStrictEqual(1);
      expect(unitUnderTest.ergebnisse[0]).toStrictEqual({
        bezirkUndWahlIDStapelart: {
          stapelArt: stapelArt,
          wahlID: wahlID,
          wahlbezirkID: "wahlbezirkID",
        },
        ergebnisse: [
          {
            ergebnis: ergebnisAfterUpdating,
            kandidatID: null,
            numIndex: null,
            wahlvorschlagID: null,
            wahlvorschlagsOrdnungszahl: null,
          },
        ],
      });
      expect(
        unitUnderTest.ergebnisse[0]?.ergebnisse[0]?.ergebnis
      ).toStrictEqual(ergebnisAfterUpdating);
    });
  });

  describe("switchStapelOfErgebnis", () => {
    it("should_switchStapelOfErgebnis_when_ergebnisseForStapelArtExist", () => {
      const sourceStapelArt = StapelArtEnum.ObwA;
      const keyForErgebnisse: BezirkUndWahlIDStapelArt =
        prepareBezirkUndWahlIDStapelart().stapelArt(sourceStapelArt).build();
      const numIndexOfItemToChange = generateRandomNumber(2);
      const targetStapelArt = StapelArtEnum.SrwBawA;

      const ergebnisToMove = prepareErgebnis()
        .numIndex(numIndexOfItemToChange)
        .build();
      const ergebnisToIgnore = prepareErgebnis()
        .numIndex(numIndexOfItemToChange + 1)
        .build();
      const sourceErgebnisse = prepareErgebnisse()
        .bezirkUndWahlIDStapelart(keyForErgebnisse)
        .ergebnisse([ergebnisToIgnore, ergebnisToMove])
        .build();
      const targetErgebnisse = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          ...keyForErgebnisse,
          stapelArt: targetStapelArt,
        })
        .ergebnisse([])
        .build();
      unitUnderTest.ergebnisse = [sourceErgebnisse, targetErgebnisse];

      unitUnderTest.switchStapelOfErgebnis(
        keyForErgebnisse,
        numIndexOfItemToChange,
        targetStapelArt
      );

      expect(sourceErgebnisse.ergebnisse).toStrictEqual([ergebnisToIgnore]);
      expect(targetErgebnisse.ergebnisse).toStrictEqual([ergebnisToMove]);
    });

    it("should_createNewErgebnisse_when_ergebnisseForTargetStapelArtDoNotExist", () => {
      const sourceStapelArt = StapelArtEnum.ObwA;
      const keyForErgebnisse: BezirkUndWahlIDStapelArt =
        prepareBezirkUndWahlIDStapelart().stapelArt(sourceStapelArt).build();
      const numIndexOfItemToChange = generateRandomNumber(2);
      const targetStapelArt = StapelArtEnum.SrwBawA;

      const ergebnisToMove = prepareErgebnis()
        .numIndex(numIndexOfItemToChange)
        .build();
      const ergebnisToIgnore = prepareErgebnis()
        .numIndex(numIndexOfItemToChange + 1)
        .build();
      const sourceErgebnisse = prepareErgebnisse()
        .bezirkUndWahlIDStapelart(keyForErgebnisse)
        .ergebnisse([ergebnisToIgnore, ergebnisToMove])
        .build();
      unitUnderTest.ergebnisse = [sourceErgebnisse];

      unitUnderTest.switchStapelOfErgebnis(
        keyForErgebnisse,
        numIndexOfItemToChange,
        targetStapelArt
      );

      expect(sourceErgebnisse.ergebnisse).toStrictEqual([ergebnisToIgnore]);
      const createdErgebnisse = unitUnderTest.ergebnisse.find(
        (ergebnisse) =>
          ergebnisse.bezirkUndWahlIDStapelart.stapelArt === targetStapelArt
      );
      const expectedCreatedErgebnisse = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          ...keyForErgebnisse,
          stapelArt: targetStapelArt,
        })
        .ergebnisse([ergebnisToMove])
        .build();
      expect(createdErgebnisse).toStrictEqual(expectedCreatedErgebnisse);
    });

    it("should_doNothing_when_sourceErgebnisseHasNoItemWithNumIndex", () => {
      const sourceStapelArt = StapelArtEnum.ObwA;
      const keyForErgebnisse: BezirkUndWahlIDStapelArt =
        prepareBezirkUndWahlIDStapelart().stapelArt(sourceStapelArt).build();
      const numIndex = generateRandomNumber(2);
      const targetStapelArt = StapelArtEnum.SrwBawA;

      const ergebnisToIgnore = prepareErgebnis().numIndex(numIndex).build();
      const sourceErgebnisse = prepareErgebnisse()
        .bezirkUndWahlIDStapelart(keyForErgebnisse)
        .ergebnisse([ergebnisToIgnore])
        .build();
      unitUnderTest.ergebnisse = [sourceErgebnisse];

      unitUnderTest.switchStapelOfErgebnis(
        keyForErgebnisse,
        numIndex + 1,
        targetStapelArt
      );

      expect(sourceErgebnisse.ergebnisse).toStrictEqual([ergebnisToIgnore]);
    });
  });

  describe("loadBegruendungForWahl", () => {
    it("should_loadBegruendungByWahlIdAndReturnNull_when_calledAndNoErgebnisseFound", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const wahlbezirksArt = WahlbezirksArtEnum.UWB;
      const wahlname = "name";

      const userStore = useUserStore();
      userStore.setUser(
        prepareUser()
          .wahlbezirksArt(wahlbezirksArt)
          .wahlMetaData([
            { wahlbezirkID: wahlbezirkID, wahlID: wahlID, wahlnummer: "0" },
          ])
          .build()
      );

      const wahlenStore = useWahlenStore();
      const wahl = prepareWahl().wahlID(wahlID).name(wahlname).build();
      wahlenStore.wahlenState.wahlen = [wahl];

      mockDefinitions.getBegruendungStimmzettelumschlaege.mockResolvedValue(
        null
      );

      await unitUnderTest.loadBegruendungForWahl(wahl);

      expect(
        mockDefinitions.getBegruendungStimmzettelumschlaege.mock.calls
      ).toStrictEqual([[wahl, wahlbezirkID, "Stimmzettel", true]]);
      expect(unitUnderTest.begruendungen).toStrictEqual([]);
    });

    it("should_loadBegrundungByWahlI_when_calledAndBegruendungFound", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const wahlbezirksArt = WahlbezirksArtEnum.UWB;
      const wahlname = "name";

      const userStore = useUserStore();
      userStore.setUser(
        prepareUser()
          .wahlbezirksArt(wahlbezirksArt)
          .wahlMetaData([
            { wahlbezirkID: wahlbezirkID, wahlID: wahlID, wahlnummer: "0" },
          ])
          .build()
      );

      const wahlenStore = useWahlenStore();
      const wahl = prepareWahl().wahlID(wahlID).name(wahlname).build();
      wahlenStore.wahlenState.wahlen = [wahl];

      const mockedBegruendung = createBegruendung();

      mockDefinitions.getBegruendungStimmzettelumschlaege.mockResolvedValue(
        mockedBegruendung
      );

      await unitUnderTest.loadBegruendungForWahl(wahl);

      expect(
        mockDefinitions.getBegruendungStimmzettelumschlaege.mock.calls
      ).toStrictEqual([[wahl, wahlbezirkID, "Stimmzettel", true]]);
      expect(unitUnderTest.begruendungen).toStrictEqual([mockedBegruendung]);
    });

    it("should_throwError_when_calledServiceThrowsError", async () => {
      const wahlID = generateRandomString(10);

      const userStore = useUserStore();
      userStore.setUser(
        prepareUser()
          .wahlbezirksArt(WahlbezirksArtEnum.UWB)
          .wahlMetaData([
            { wahlbezirkID: "wahlbezirkID", wahlID: wahlID, wahlnummer: "0" },
          ])
          .build()
      );

      const wahlenStore = useWahlenStore();
      const wahl = prepareWahl().wahlID(wahlID).build();
      wahlenStore.wahlenState.wahlen = [wahl];

      mockDefinitions.getBegruendungStimmzettelumschlaege.mockRejectedValue(
        new Error("service call failed")
      );

      await expect(
        unitUnderTest.loadBegruendungForWahl(wahl)
      ).rejects.toThrow();
    });
  });

  describe("saveBegruendung", () => {
    it("should_saveBegruendung_when_serviceReturnsSuccessfully", async () => {
      const begruendung = createBegruendung();
      const wahlbezirkID = generateRandomString(10);

      const userStore = useUserStore();
      userStore.setUser(
        prepareUser()
          .wahlMetaData([
            { wahlbezirkID, wahlID: begruendung.wahlID, wahlnummer: "0" },
          ])
          .build()
      );

      mockDefinitions.postBegruendung.mockResolvedValue({}); // Mock successful response

      await unitUnderTest.saveBegruendung(begruendung);

      expect(mockDefinitions.postBegruendung).toHaveBeenCalledWith(
        begruendung,
        wahlbezirkID,
        true
      );
      expect(unitUnderTest.begruendungen).toContainEqual(begruendung);
      expect(unitUnderTest.isBegruendungSaving).toBe(false);
    });

    it("should_replaceExistingBegruendung_when_savingNewBegruendungWithSameWahlID", async () => {
      const begruendung1 = createBegruendung();
      const begruendung2 = createBegruendung();
      const updatedBegruendung2 = {
        ...begruendung2,
        text: "Updated Text",
      };
      const wahlbezirkID = generateRandomString(10);
      const userStore = useUserStore();
      userStore.setUser(
        prepareUser()
          .wahlMetaData([
            {
              wahlbezirkID,
              wahlID: begruendung2.wahlID,
              wahlnummer: "0",
            },
          ])
          .build()
      );

      unitUnderTest.begruendungen.push(begruendung1);
      unitUnderTest.begruendungen.push(begruendung2);

      mockDefinitions.postBegruendung.mockResolvedValue({});

      await unitUnderTest.saveBegruendung(updatedBegruendung2);

      expect(mockDefinitions.postBegruendung).toHaveBeenCalledWith(
        updatedBegruendung2,
        wahlbezirkID,
        true
      );
      expect(unitUnderTest.begruendungen).toContainEqual(begruendung1);
      expect(unitUnderTest.begruendungen).toContainEqual(updatedBegruendung2);
      expect(unitUnderTest.begruendungen).not.toContainEqual(begruendung2);
    });

    it("should_throwError_when_serviceThrowsError", async () => {
      const begruendung = createBegruendung();
      const wahlbezirkID = generateRandomString(10);

      const userStore = useUserStore();
      userStore.setUser(
        prepareUser()
          .wahlMetaData([
            { wahlbezirkID, wahlID: begruendung.wahlID, wahlnummer: "0" },
          ])
          .build()
      );

      mockDefinitions.postBegruendung.mockRejectedValue(
        new Error("service call failed")
      );

      await expect(unitUnderTest.saveBegruendung(begruendung)).rejects.toThrow(
        "Fehler beim Speichern der Begründung."
      );

      expect(mockDefinitions.postBegruendung).toHaveBeenCalledWith(
        begruendung,
        wahlbezirkID,
        true
      );
      expect(unitUnderTest.isBegruendungSaving).toBe(false);
    });

    it("should_setIsBegruendungSavingToTrue_when_saving", async () => {
      const begruendung = createBegruendung();
      const wahlbezirkID = generateRandomString(10);

      const userStore = useUserStore();
      userStore.setUser(
        prepareUser()
          .wahlMetaData([
            { wahlbezirkID, wahlID: begruendung.wahlID, wahlnummer: "0" },
          ])
          .build()
      );

      mockDefinitions.postBegruendung.mockImplementation(
        () =>
          new Promise((resolve) => {
            setTimeout(() => resolve({}), 100);
          })
      );

      const savePromise = unitUnderTest.saveBegruendung(begruendung);

      expect(unitUnderTest.isBegruendungSaving).toBe(true);
      await savePromise;
      expect(unitUnderTest.isBegruendungSaving).toBe(false);
    });
  });
});
