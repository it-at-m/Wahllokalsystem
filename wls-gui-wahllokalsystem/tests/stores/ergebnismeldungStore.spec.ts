import type { BezirkUndWahlIDStapelArt } from "@/types/ergebnismeldung/BezirkUndWahlIDStapelArt.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisse: vi.fn(),
  postErgebnisse: vi.fn(),
}));

vi.mock("@/composables/ergebnismeldung/ergebnisService.ts", () => ({
  useErgebnisService: () => ({
    getErgebnisse: mockDefinitions.getErgebnisse,
    postErgebnisse: mockDefinitions.postErgebnisse,
  }),
}));

const { generateRandomString, generateRandomNumber } =
  useCommonTestDataFactory();
const {
  prepareErgebnis,
  createErgebnisse,
  prepareErgebnisse,
  prepareBezirkUndWahlIDStapelart,
} = useErgebnisseTestDataFactory();
const { prepareUser } = useUserTestDataFactory();

describe("ergebnismeldungStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useErgebnismeldungStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useErgebnismeldungStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
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
        [wahlbezirkID, wahlID, stapelArt],
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
        [wahlbezirkID, wahlID, stapelArt],
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

      await unitUnderTest.sendErgebnisseByStapelArt(wahlID, stapelArt);

      expect(mockDefinitions.postErgebnisse.mock.calls).toStrictEqual([
        [wahlbezirkID, wahlID, stapelArt, mockedErgebnisseModel],
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
});
