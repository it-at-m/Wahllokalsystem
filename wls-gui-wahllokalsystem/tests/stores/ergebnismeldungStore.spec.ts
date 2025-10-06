import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useBegruendungTestDataFactory } from "@tests/utils/ergebnismeldung/begruendungTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisse: vi.fn(),
  postErgebnisse: vi.fn(),
  getBegruendungStimmzettelumschlaege: vi.fn(),
}));

vi.mock("@/composables/ergebnismeldung/ergebnisService.ts", () => ({
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
    }),
  })
);

const { generateRandomString } = useCommonTestDataFactory();
const { createErgebnisse, prepareErgebnisse, prepareErgebnis } =
  useErgebnisseTestDataFactory();
const { prepareUser } = useUserTestDataFactory();
const { prepareWahl } = useWahlTestDataFactory();
const { createBegruendung } = useBegruendungTestDataFactory();

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
      expect(unitUnderTest.ergebnisse[0].ergebnisse[0].ergebnis).toStrictEqual(
        ergebnisAfterUpdating
      );
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
      wahlenStore.wahlenState.wahlen = [
        prepareWahl().wahlID(wahlID).name(wahlname).build(),
      ];

      mockDefinitions.getBegruendungStimmzettelumschlaege.mockResolvedValue(
        null
      );

      await unitUnderTest.loadBegruendungForWahl(wahlID);

      expect(
        mockDefinitions.getBegruendungStimmzettelumschlaege.mock.calls
      ).toStrictEqual([[wahlID, wahlbezirkID, wahlbezirksArt, wahlname, true]]);
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
      wahlenStore.wahlenState.wahlen = [
        prepareWahl().wahlID(wahlID).name(wahlname).build(),
      ];

      const mockedBegruendung = createBegruendung();

      mockDefinitions.getBegruendungStimmzettelumschlaege.mockResolvedValue(
        mockedBegruendung
      );

      await unitUnderTest.loadBegruendungForWahl(wahlID);

      expect(
        mockDefinitions.getBegruendungStimmzettelumschlaege.mock.calls
      ).toStrictEqual([[wahlID, wahlbezirkID, wahlbezirksArt, wahlname, true]]);
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
      wahlenStore.wahlenState.wahlen = [
        prepareWahl().wahlID(wahlID).name("wahlname").build(),
      ];

      mockDefinitions.getBegruendungStimmzettelumschlaege.mockRejectedValue(
        new Error("service call failed")
      );

      await expect(
        unitUnderTest.loadBegruendungForWahl(wahlID)
      ).rejects.toThrow();
    });
  });
});
