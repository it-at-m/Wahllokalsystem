import { createTestingPinia } from "@pinia/testing";
import { spyOn } from "@storybook/test";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useVorfaelleundvorkommnisseTestDataFactory } from "@tests/utils/vorfaelleundvorkommnisse/VorfaelleundvorkommnisseTestDataFactory";
import { flushPromises } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import * as ImportAllFromEreignisArt from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";
import { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";
import { WahlbezirkEreignisseBuilder } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getEreignisse: vi.fn(),
  saveEreignisse: vi.fn(),
}));

vi.mock("@/composables/vorfaelleundvorkommnisse/ereignisService", () => ({
  useEreignisService: () => ({
    getEreignisse: mockDefinitions.getEreignisse,
    saveEreignisse: mockDefinitions.saveEreignisse,
  }),
}));

const mockedNow = new Date();

const { createEreignis } = useVorfaelleundvorkommnisseTestDataFactory();
const { prepareUser } = useUserTestDataFactory();

describe("ereignisStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useEreignisStore>;
  let wahlbezirkStore: ReturnType<typeof useWahlbezirkStore>;

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    unitUnderTest = useEreignisStore(testPinia);
    wahlbezirkStore = useWahlbezirkStore(testPinia);

    vi.useFakeTimers({
      now: mockedNow,
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("hasMissingEreignisFlags", () => {
    describe("should_returnExpectedValue_dependingOnFunctionData", () => {
      it.each(_generateTestdataForAreKeineEreignisseFlagsValid())(
        "hasVorfaelle=$data.vorfaelle | hasVorkommnisse=$data.vorkommnisse | wahlbezirkKeineVorfaelle=$data.wahlbezirkKeineVorfaelle | wahlbezirkKeineVorkommnisse=$data.wahlbezirkKeineVorkommnisse | wahlbezirkArt=$data.wahlbezirkArt | schliessungsuhrzeit=$data.schliessungsuhrzeitSent --> Expected=$expected",
        ({ data, expected }) => {
          const userStore = useUserStore();
          userStore.setUser(
            prepareUser().wahlbezirksArt(data.wahlbezirkArt).build()
          );

          const wahlbezirkStore = useWahlbezirkStore();
          wahlbezirkStore.schliessungsuhrzeitSent =
            data.schliessungsuhrzeitSent;

          // @ts-expect-error: cannot set readonly
          unitUnderTest.hasVorfaelle = data.vorfaelle;
          unitUnderTest.wahlbezirkEreignisse.keineVorfaelle =
            data.wahlbezirkKeineVorfaelle;

          // @ts-expect-error: cannot set readonly
          unitUnderTest.hasVorkommnisse = data.vorkommnisse;
          unitUnderTest.wahlbezirkEreignisse.keineVorkommnisse =
            data.wahlbezirkKeineVorkommnisse;

          expect(unitUnderTest.hasMissingEreignisFlags).toStrictEqual(expected);
        }
      );
    });
  });

  describe("hasEintraege", () => {
    it("should_returnFalse_when_ereigniseintraegeAreUndefined", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = undefined;

      expect(unitUnderTest.hasEintraege).toStrictEqual(false);
    });
    it("should_returnFalse_when_ereigniseintraegeAreEmptyArray", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [];

      expect(unitUnderTest.hasEintraege).toStrictEqual(false);
    });
    it("should_returnTrue_when_ereigniseintraegeHasOneItem", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [{}];

      expect(unitUnderTest.hasEintraege).toStrictEqual(true);
    });
    it("should_returnTrue_when_ereigniseintraegeHasMoreThanOneItem", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [{}, {}, {}, {}];

      expect(unitUnderTest.hasEintraege).toStrictEqual(true);
    });
  });

  describe("hasVorfaelle", () => {
    it("should_returnTrue_when_ereignisEintraegeHasOneEintragOfTypeVorfall", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        { ereignisart: "VORFALL" },
      ];

      expect(unitUnderTest.hasVorfaelle).toStrictEqual(true);
    });

    it("should_returnTrue_when_ereignisEintraegeHasMoreThanOneOfTypeVorfall", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        { ereignisart: "VORFALL" },
        { ereignisart: "VORKOMMNIS" },
        { ereignisart: "VORFALL" },
        { ereignisart: "VORFALL" },
      ];

      expect(unitUnderTest.hasVorfaelle).toStrictEqual(true);
    });

    it("should_returnFalse_when_ereignisEintraegeHasNonOfTypeVorfall", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        { ereignisart: "VORKOMMNIS" },
        { ereignisart: "VORKOMMNIS" },
        { ereignisart: "VORKOMMNIS" },
        { ereignisart: "VORKOMMNIS" },
      ];

      expect(unitUnderTest.hasVorfaelle).toStrictEqual(false);
    });

    it("should_returnFalse_when_ereignisEintraegeIsUndefined", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = undefined;

      expect(unitUnderTest.hasVorfaelle).toStrictEqual(false);
    });
  });

  describe("hasVorkommnisse", () => {
    it("should_returnTrue_when_ereignisEintraegeHasOneEintragOfTypeVorkommnis", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        { ereignisart: "VORKOMMNIS" },
      ];

      expect(unitUnderTest.hasVorkommnisse).toStrictEqual(true);
    });

    it("should_returnTrue_when_ereignisEintraegeHasMoreThanOneOfTypeVORKOMMNIS", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        { ereignisart: "VORKOMMNIS" },
        { ereignisart: "VORFALL" },
        { ereignisart: "VORKOMMNIS" },
        { ereignisart: "VORKOMMNIS" },
      ];

      expect(unitUnderTest.hasVorkommnisse).toStrictEqual(true);
    });

    it("should_returnFalse_when_ereignisEintraegeHasNonOfTypeVORKOMMNIS", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        { ereignisart: "VORFALL" },
        { ereignisart: "VORFALL" },
        { ereignisart: "VORFALL" },
        { ereignisart: "VORFALL" },
      ];

      expect(unitUnderTest.hasVorkommnisse).toStrictEqual(false);
    });

    it("should_returnFalse_when_ereignisEintraegeIsUndefined", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = undefined;

      expect(unitUnderTest.hasVorkommnisse).toStrictEqual(false);
    });
  });

  describe("addEreignis", () => {
    it("should_addEreignisToWahlbezirkEreignisse_when_ereignisIsAdded", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit = spyOn(
        ImportAllFromEreignisArt,
        "getEreignisArtForDateRelatedToSchliessungsuhrzeit"
      );

      const mockedEreignisartOfNewEreignis = EreignisartEnum.Vorfall;
      spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit.mockReturnValue(
        mockedEreignisartOfNewEreignis
      );

      const mockedWahlbezirkEreignisse =
        WahlbezirkEreignisseBuilder.createEmptyWahlbezirkEreignisse();
      mockDefinitions.getEreignisse.mockReturnValue(mockedWahlbezirkEreignisse);

      await unitUnderTest.addEreignis();

      expect(unitUnderTest.wahlbezirkEreignisse.ereigniseintraege).toHaveLength(
        1
      );
      expect(
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege?.[0].ereignisart
      ).toStrictEqual(mockedEreignisartOfNewEreignis);

      spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit.mockRestore();
    });
  });

  describe("deleteEreignisByIndex", () => {
    it("should_removeItemOfIndex_when_hasEintraegeAndIndexIsInRange", () => {
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
        ereigniseintraege: [
          { beschreibung: "1" },
          { beschreibung: "2" },
          { beschreibung: "3" },
          { beschreibung: "4" },
        ],
      };

      unitUnderTest.deleteEreignisByIndex(1);

      expect(
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege
      ).toStrictEqual([
        { beschreibung: "1" },
        { beschreibung: "3" },
        { beschreibung: "4" },
      ]);
    });

    it("should_doNothing_when_statesEintraegeAreUndefined", () => {
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
      };

      unitUnderTest.deleteEreignisByIndex(1);

      expect(
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege
      ).toBeUndefined();
    });

    it("should_doNothing_when_indexIsOutOfRange", () => {
      const ereigniseintraege = [
        { beschreibung: "1" },
        { beschreibung: "2" },
        { beschreibung: "3" },
        { beschreibung: "4" },
      ];
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
        ereigniseintraege: Array.from(ereigniseintraege),
      };

      unitUnderTest.deleteEreignisByIndex(ereigniseintraege.length);

      expect(
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege
      ).toStrictEqual(ereigniseintraege);
    });
  });

  describe("loadEreignisse", () => {
    it("should_loadWahlbezirkEreignisse_when_userHasWahlbezirkID", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const mockedWahlbezirkEreignisse =
        WahlbezirkEreignisseBuilder.createEmptyWahlbezirkEreignisse();
      mockDefinitions.getEreignisse.mockReturnValue(mockedWahlbezirkEreignisse);

      await unitUnderTest.loadEreignisse();

      expect(unitUnderTest.wahlbezirkEreignisse).toStrictEqual(
        mockedWahlbezirkEreignisse
      );
    });

    it("should_notLoadWahlbezirkEreignisse_when_usersWahlbezirkIdIsUndefined", async () => {
      const userStore = useUserStore();
      userStore.setUser(prepareUser().wahlbezirkID(undefined).build());

      await unitUnderTest.loadEreignisse();

      expect(mockDefinitions.getEreignisse).toHaveBeenCalledTimes(0);
      expect(unitUnderTest.wahlbezirkEreignisse.ereigniseintraege).toHaveLength(
        0
      );
    });

    it("should_handleError_when_getEreignisseThrowsError", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const mockedError = new Error("Network error");
      mockDefinitions.getEreignisse.mockRejectedValue(mockedError);

      await unitUnderTest.loadEreignisse();
      expect(unitUnderTest.error).equals("Fehler beim Laden der Ereignisse");
    });
  });

  describe("sendEreignisse", () => {
    it("should_sendEreignisse_when_wahlbezirkIDIsGiven", () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const mockedDatetime = new Date();

      mockDefinitions.saveEreignisse.mockReturnValue(
        Promise.resolve({ updateDatetime: mockedDatetime })
      );

      unitUnderTest.sendEreignisse();

      expect(mockDefinitions.saveEreignisse).toHaveBeenCalledWith(
        wahlbezirkID,
        unitUnderTest.wahlbezirkEreignisse
      );
    });

    it("should_notsendEreignisse_when_wahlbezirkIDIsNotGiven", async () => {
      const userStore = useUserStore();
      userStore.setUser(prepareUser().wahlbezirkID(undefined).build());

      await unitUnderTest.sendEreignisse();

      expect(mockDefinitions.saveEreignisse).toBeCalledTimes(0);
    });
  });

  describe("updateUhrzeitByIndex", () => {
    it("should_doNothing_when_noEreignisEintraegeAreGiven", () => {
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
      };

      unitUnderTest.updateUhrzeitByIndex(new Date(), 1);

      expect(
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege
      ).toBeUndefined();
    });

    it("should_doNothing_when_indexIsOutOfRange", () => {
      const dateAsString = "2025-04-29T09:33:42";
      const eintragNotToChange = { uhrzeit: new Date(dateAsString) };
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
        ereigniseintraege: [eintragNotToChange],
      };

      unitUnderTest.updateUhrzeitByIndex(new Date(), 1);

      expect(eintragNotToChange.uhrzeit).toEqual(new Date(dateAsString));
    });

    it("should_updateUhrzeit_when_uhrzeitIsGiven", () => {
      const dateAsString = "2025-04-29T09:33:42";
      const eintragToChange = { uhrzeit: new Date(dateAsString) };
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
        ereigniseintraege: [eintragToChange],
      };

      const updateDate = new Date("2025-04-29T12:12:42");
      unitUnderTest.updateUhrzeitByIndex(updateDate, 0);

      expect(eintragToChange.uhrzeit).toEqual(new Date("2025-04-29T12:12:42"));
    });

    it("should_setUhrzeitUndefined_when_uhrzeitIsUndefined", () => {
      const dateAsString = "2025-04-29T09:33:42";
      const eintragToChange = { uhrzeit: new Date(dateAsString) };
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
        ereigniseintraege: [eintragToChange],
      };

      unitUnderTest.updateUhrzeitByIndex(undefined, 0);

      expect(eintragToChange.uhrzeit).toBeUndefined();
    });
  });

  describe("watch", () => {
    describe("schliessungsuhrzeitSent", () => {
      it("should_updateEreignisart_when_schliessungsuhrzeitSentHasChanged", async () => {
        const schliessungsuhrzeitSend = new Date();

        const ereignisEintraege = [
          createEreignis(),
          createEreignis(),
          createEreignis(),
        ];
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege =
          ereignisEintraege;

        wahlbezirkStore.schliessungsuhrzeitSent = schliessungsuhrzeitSend;

        const spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit = spyOn(
          ImportAllFromEreignisArt,
          "getEreignisArtForDateRelatedToSchliessungsuhrzeit"
        );

        await flushPromises();

        expect(
          spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit.mock.calls.length
        ).toStrictEqual(ereignisEintraege.length);

        spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit.mockRestore();
      });
    });
  });

  describe("watchEffect", () => {
    describe("updateKeineFlagsOfEreignisseBasedOnCurrentState", () => {
      it("should_setKeineVorfaelleTrue_when_lastVorfallWasDeleted", async () => {
        const ereigniseintraege = [
          { beschreibung: "1", ereignisart: EreignisartEnum.Vorfall },
          { beschreibung: "2", ereignisart: EreignisartEnum.Vorkommnis },
          { beschreibung: "3", ereignisart: EreignisartEnum.Vorkommnis },
          { beschreibung: "4", ereignisart: EreignisartEnum.Vorkommnis },
        ];
        unitUnderTest.wahlbezirkEreignisse = {
          wahlbezirkID: "wahlbezirkID",
          keineVorfaelle: false,
          ereigniseintraege: Array.from(ereigniseintraege),
        };

        unitUnderTest.deleteEreignisByIndex(0);

        await nextTick();

        expect(unitUnderTest.wahlbezirkEreignisse.keineVorfaelle).toStrictEqual(
          true
        );
      });

      it("should_setKeineVorkommnisseTrue_when_lastVorkommnisWasDeleted", async () => {
        const ereigniseintraege = [
          { beschreibung: "1", ereignisart: EreignisartEnum.Vorkommnis },
          { beschreibung: "2", ereignisart: EreignisartEnum.Vorfall },
          { beschreibung: "3", ereignisart: EreignisartEnum.Vorfall },
          { beschreibung: "4", ereignisart: EreignisartEnum.Vorfall },
        ];
        unitUnderTest.wahlbezirkEreignisse = {
          wahlbezirkID: "wahlbezirkID",
          keineVorkommnisse: false,
          ereigniseintraege: Array.from(ereigniseintraege),
        };

        unitUnderTest.deleteEreignisByIndex(0);

        await nextTick();

        expect(
          unitUnderTest.wahlbezirkEreignisse.keineVorkommnisse
        ).toStrictEqual(true);
      });

      it("should_setKeineVorfaelleFalse_when_vorfallWasAdded", async () => {
        const spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit = spyOn(
          ImportAllFromEreignisArt,
          "getEreignisArtForDateRelatedToSchliessungsuhrzeit"
        );

        unitUnderTest.wahlbezirkEreignisse.keineVorfaelle = true;
        spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit.mockReturnValue(
          EreignisartEnum.Vorfall
        );

        unitUnderTest.addEreignis();

        await nextTick();

        expect(unitUnderTest.wahlbezirkEreignisse.keineVorfaelle).toStrictEqual(
          false
        );

        spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit.mockRestore();
      });

      it("should_setKeineVorkommnisseFalse_when_vorkommnissWasAdded", async () => {
        const spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit = spyOn(
          ImportAllFromEreignisArt,
          "getEreignisArtForDateRelatedToSchliessungsuhrzeit"
        );

        unitUnderTest.wahlbezirkEreignisse.keineVorkommnisse = true;
        spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit.mockReturnValue(
          EreignisartEnum.Vorkommnis
        );

        unitUnderTest.addEreignis();

        await nextTick();

        expect(
          unitUnderTest.wahlbezirkEreignisse.keineVorkommnisse
        ).toStrictEqual(false);

        spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit.mockRestore();
      });

      it("should_switchFromKeineVorfaelleToKeineVorkommnisse_when_allEreignisseOfArtVorkommnissSwitchedToVorfall", async () => {
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
          { ereignisart: EreignisartEnum.Vorkommnis },
        ];
        unitUnderTest.wahlbezirkEreignisse.keineVorfaelle = true;
        unitUnderTest.wahlbezirkEreignisse.keineVorkommnisse = false;

        const spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit = spyOn(
          ImportAllFromEreignisArt,
          "getEreignisArtForDateRelatedToSchliessungsuhrzeit"
        );
        spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit.mockReturnValue(
          EreignisartEnum.Vorfall
        );

        unitUnderTest.updateUhrzeitByIndex(new Date(), 0);

        await nextTick();

        expect(unitUnderTest.wahlbezirkEreignisse.keineVorfaelle).toStrictEqual(
          false
        );
        expect(
          unitUnderTest.wahlbezirkEreignisse.keineVorkommnisse
        ).toStrictEqual(true);
      });

      it("should_switchFromKeineVorkommnisseToKeineVorfaelle_when_allEreignisseOfArtVorfallSwitchedToVorkommniss", async () => {
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
          { ereignisart: EreignisartEnum.Vorfall },
        ];
        unitUnderTest.wahlbezirkEreignisse.keineVorfaelle = false;
        unitUnderTest.wahlbezirkEreignisse.keineVorkommnisse = true;

        const spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit = spyOn(
          ImportAllFromEreignisArt,
          "getEreignisArtForDateRelatedToSchliessungsuhrzeit"
        );
        spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit.mockReturnValue(
          EreignisartEnum.Vorkommnis
        );

        unitUnderTest.updateUhrzeitByIndex(new Date(), 0);

        await nextTick();

        expect(unitUnderTest.wahlbezirkEreignisse.keineVorfaelle).toStrictEqual(
          true
        );
        expect(
          unitUnderTest.wahlbezirkEreignisse.keineVorkommnisse
        ).toStrictEqual(false);
      });
    });
  });
});

function _generateTestdataForAreKeineEreignisseFlagsValid() {
  return [
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: true,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: true,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: undefined,
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: true,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: false,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: false,
      },
      expected: false,
    },
    {
      data: {
        vorfaelle: false,
        vorkommnisse: false,
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        schliessungsuhrzeitSent: new Date(),
        wahlbezirkKeineVorfaelle: true,
        wahlbezirkKeineVorkommnisse: true,
      },
      expected: true,
    },
  ];
}
