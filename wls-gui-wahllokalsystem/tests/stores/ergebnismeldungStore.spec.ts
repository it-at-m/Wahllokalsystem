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

const { generateRandomString } = useCommonTestDataFactory();
const { createErgebnisse, prepareErgebnisse, prepareErgebnis } =
  useErgebnisseTestDataFactory();
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

      expect(unitUnderTest.ergebnisse[0].ergebnisse[0].ergebnis).toStrictEqual(
        ergebnisBeforeUpdating
      );

      unitUnderTest.findAndUpdateErgebnisseByWahlIdAndStapelArt(
        wahlID,
        stapelArt,
        [prepareErgebnis().ergebnis(ergebnisAfterUpdating).build()]
      );

      expect(unitUnderTest.ergebnisse[0].ergebnisse[0].ergebnis).toStrictEqual(
        ergebnisAfterUpdating
      );
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
      expect(unitUnderTest.ergebnisse[0].ergebnisse[0].ergebnis).toStrictEqual(
        ergebnisAfterUpdating
      );
    });
  });
});
