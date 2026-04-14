import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";

import { createTestingPinia } from "@pinia/testing";
import { spyOn } from "@storybook/test";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useVorfaelleundvorkommnisseTestDataFactory } from "@tests/utils/vorfaelleundvorkommnisse/VorfaelleundvorkommnisseTestDataFactory";
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

const { prepareEreignis, prepareWahlbezirkEreignisse } =
  useVorfaelleundvorkommnisseTestDataFactory();
const { prepareUser } = useUserTestDataFactory();

describe("ereignisStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useEreignisStore>;
  let userStore: ReturnType<typeof useUserStore>;

  const BESCHREIBUNG = "Beschreibung";
  const BESCHREIBUNG_NEU = "Neue Beschreibung";

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    unitUnderTest = useEreignisStore(testPinia);
    userStore = useUserStore(testPinia);

    vi.useFakeTimers({
      now: mockedNow,
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("isEreignisFlagsAndEreigniseintraegeInconsistent", () => {
    describe("should_returnExpectedValue_dependingOnFunctionData", () => {
      it.each(_generateTestdataForAreKeineEreignisseFlagsValid())(
        "ereigniseintraegeContainsVorfaelle=$data.vorfaelle | ereigniseintraegeContainsVorkommnisse=$data.vorkommnisse | wahlbezirkKeineVorfaelle=$data.wahlbezirkKeineVorfaelle | wahlbezirkKeineVorkommnisse=$data.wahlbezirkKeineVorkommnisse | wahlbezirkArt=$data.wahlbezirkArt | schliessungsuhrzeit=$data.schliessungsuhrzeitSent --> Expected=$expected",
        ({ data, expected }) => {
          const userStore = useUserStore();
          userStore.setUser(
            prepareUser().wahlbezirksArt(data.wahlbezirkArt).build()
          );

          const wahlbezirkStore = useWahlbezirkStore();
          wahlbezirkStore.schliessungsuhrzeitState.schliessungsuhrzeitSent =
            data.schliessungsuhrzeitSent;

          // @ts-expect-error: cannot set readonly
          unitUnderTest.ereigniseintraegeContainsVorfaelle = data.vorfaelle;
          unitUnderTest.wahlbezirkEreignisse.keineVorfaelle =
            data.wahlbezirkKeineVorfaelle;

          // @ts-expect-error: cannot set readonly
          unitUnderTest.ereigniseintraegeContainsVorkommnisse =
            data.vorkommnisse;
          unitUnderTest.wahlbezirkEreignisse.keineVorkommnisse =
            data.wahlbezirkKeineVorkommnisse;

          expect(
            unitUnderTest.isEreignisFlagsAndEreigniseintraegeInconsistent
          ).toStrictEqual(expected);
        }
      );
    });
  });

  describe("hasEintraege", () => {
    it("should_returnFalse_when_ereigniseintraegeAreUndefined", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [];

      expect(unitUnderTest.hasEintraege).toStrictEqual(false);
    });
    it("should_returnFalse_when_ereigniseintraegeAreEmptyArray", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [];

      expect(unitUnderTest.hasEintraege).toStrictEqual(false);
    });
    it("should_returnTrue_when_ereigniseintraegeHasOneItem", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        { ereignisart: EreignisartEnum.Vorfall },
      ];

      expect(unitUnderTest.hasEintraege).toStrictEqual(true);
    });
    it("should_returnTrue_when_ereigniseintraegeHasMoreThanOneItem", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        { ereignisart: EreignisartEnum.Vorfall },
        { ereignisart: EreignisartEnum.Vorfall },
        { ereignisart: EreignisartEnum.Vorfall },
        { ereignisart: EreignisartEnum.Vorfall },
      ];

      expect(unitUnderTest.hasEintraege).toStrictEqual(true);
    });
  });

  describe("ereigniseintraegeContainsVorfaelle", () => {
    it("should_returnTrue_when_ereignisEintraegeHasOneEintragOfTypeVorfall", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        { ereignisart: "VORFALL" },
      ];

      expect(unitUnderTest.ereigniseintraegeContainsVorfaelle).toStrictEqual(
        true
      );
    });

    it("should_returnTrue_when_ereignisEintraegeHasMoreThanOneOfTypeVorfall", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        { ereignisart: "VORFALL" },
        { ereignisart: "VORKOMMNIS" },
        { ereignisart: "VORFALL" },
        { ereignisart: "VORFALL" },
      ];

      expect(unitUnderTest.ereigniseintraegeContainsVorfaelle).toStrictEqual(
        true
      );
    });

    it("should_returnFalse_when_ereignisEintraegeHasNonOfTypeVorfall", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        { ereignisart: "VORKOMMNIS" },
        { ereignisart: "VORKOMMNIS" },
        { ereignisart: "VORKOMMNIS" },
        { ereignisart: "VORKOMMNIS" },
      ];

      expect(unitUnderTest.ereigniseintraegeContainsVorfaelle).toStrictEqual(
        false
      );
    });

    it("should_returnFalse_when_ereignisEintraegeIsUndefined", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [];

      expect(unitUnderTest.ereigniseintraegeContainsVorfaelle).toStrictEqual(
        false
      );
    });
  });

  describe("ereigniseintraegeContainsVorkommnisse", () => {
    it("should_returnTrue_when_ereignisEintraegeHasOneEintragOfTypeVorkommnis", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        { ereignisart: "VORKOMMNIS" },
      ];

      expect(unitUnderTest.ereigniseintraegeContainsVorkommnisse).toStrictEqual(
        true
      );
    });

    it("should_returnTrue_when_ereignisEintraegeHasMoreThanOneOfTypeVORKOMMNIS", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        { ereignisart: "VORKOMMNIS" },
        { ereignisart: "VORFALL" },
        { ereignisart: "VORKOMMNIS" },
        { ereignisart: "VORKOMMNIS" },
      ];

      expect(unitUnderTest.ereigniseintraegeContainsVorkommnisse).toStrictEqual(
        true
      );
    });

    it("should_returnFalse_when_ereignisEintraegeHasNonOfTypeVORKOMMNIS", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        { ereignisart: "VORFALL" },
        { ereignisart: "VORFALL" },
        { ereignisart: "VORFALL" },
        { ereignisart: "VORFALL" },
      ];

      expect(unitUnderTest.ereigniseintraegeContainsVorkommnisse).toStrictEqual(
        false
      );
    });

    it("should_returnFalse_when_ereignisEintraegeIsUndefined", () => {
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [];

      expect(unitUnderTest.ereigniseintraegeContainsVorkommnisse).toStrictEqual(
        false
      );
    });
  });

  describe("isSaving", () => {
    it("should_updateIsSaving_when_sendErgebnisseIsCalled", async () => {
      const timeout = 100;
      const wahlbezirkID = "wahlbezirkID";
      const userStore = useUserStore();
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      mockDefinitions.saveEreignisse.mockReturnValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve({});
          }, timeout);
        })
      );

      expect(unitUnderTest.isSaving).toBe(false);

      const promise = unitUnderTest.sendEreignisse();

      expect(unitUnderTest.isSaving).toBe(true);

      vi.advanceTimersByTime(timeout);
      await promise;

      expect(unitUnderTest.isSaving).toBe(false);
    });

    it("should_updateIsSaving_when_sendErgebnisseFails", async () => {
      const timeout = 100;
      const wahlbezirkID = "wahlbezirkID";
      const userStore = useUserStore();
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      mockDefinitions.saveEreignisse.mockReturnValue(
        new Promise((resolve, reject) => {
          setTimeout(() => {
            reject("Mocked API Error");
          }, timeout);
        })
      );

      expect(unitUnderTest.isSaving).toBe(false);

      const promise = unitUnderTest.sendEreignisse();

      expect(unitUnderTest.isSaving).toBe(true);

      vi.advanceTimersByTime(timeout);
      await promise;

      expect(unitUnderTest.isSaving).toBe(false);
    });
  });

  describe("addEreignis", () => {
    it("should_addEreignisToWahlbezirkEreignisseWithDefaultValues_when_ereignisToAddIsUndefined", async () => {
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
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege[0]?.ereignisart
      ).toStrictEqual(mockedEreignisartOfNewEreignis);

      spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit.mockRestore();
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

    it("should_setKeineVorfaelleTrue_when_isBWB", async () => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );

      unitUnderTest.addEreignis();

      await nextTick();

      expect(unitUnderTest.wahlbezirkEreignisse.keineVorfaelle).toStrictEqual(
        true
      );
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

    it("should_addEreignisWithData_when_ereignisToAddIsGiven", () => {
      const ereignisToAdd: Ereignis = {
        uhrzeit: new Date("2025-07-31T12:43:07.999"),
        ereignisart: EreignisartEnum.Vorfall,
        beschreibung: "dies ist die Ereignisbeschreibung",
      };

      unitUnderTest.addEreignis(ereignisToAdd);

      expect(
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege
      ).toStrictEqual([ereignisToAdd]);
    });
  });

  describe("deleteEreignisByIndex", () => {
    it("should_removeItemOfIndex_when_hasEintraegeAndIndexIsInRange", () => {
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
        ereigniseintraege: [
          { beschreibung: "1", ereignisart: EreignisartEnum.Vorfall },
          { beschreibung: "2", ereignisart: EreignisartEnum.Vorfall },
          { beschreibung: "3", ereignisart: EreignisartEnum.Vorfall },
          { beschreibung: "4", ereignisart: EreignisartEnum.Vorfall },
        ],
      };

      unitUnderTest.deleteEreignisByIndex(1);

      expect(
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege
      ).toStrictEqual([
        { beschreibung: "1", ereignisart: EreignisartEnum.Vorfall },
        { beschreibung: "3", ereignisart: EreignisartEnum.Vorfall },
        { beschreibung: "4", ereignisart: EreignisartEnum.Vorfall },
      ]);
    });

    it("should_doNothing_when_statesEintraegeAreUndefined", () => {
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
        ereigniseintraege: [],
      };

      unitUnderTest.deleteEreignisByIndex(1);

      expect(
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege
      ).toStrictEqual([]);
    });

    it("should_doNothing_when_indexIsOutOfRange", () => {
      const ereigniseintraege = [
        { beschreibung: "1", ereignisart: EreignisartEnum.Vorfall },
        { beschreibung: "2", ereignisart: EreignisartEnum.Vorfall },
        { beschreibung: "3", ereignisart: EreignisartEnum.Vorfall },
        { beschreibung: "4", ereignisart: EreignisartEnum.Vorfall },
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
      unitUnderTest.isVorfaelleMaintained = false;

      const mockedWahlbezirkEreignisse = prepareWahlbezirkEreignisse()
        .ereigniseintraege([])
        .build();
      mockDefinitions.getEreignisse.mockReturnValue(mockedWahlbezirkEreignisse);

      await unitUnderTest.loadEreignisse();

      expect(unitUnderTest.wahlbezirkEreignisse).toStrictEqual(
        mockedWahlbezirkEreignisse
      );
      expect(unitUnderTest.isVorfaelleMaintained).toStrictEqual(
        mockedWahlbezirkEreignisse.keineVorfaelle
      );
    });

    it("should_handleError_when_getEreignisseThrowsError", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      unitUnderTest.isVorfaelleMaintained = false;
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const mockedError = new Error("Network error");
      mockDefinitions.getEreignisse.mockRejectedValue(mockedError);

      await unitUnderTest.loadEreignisse();
      expect(unitUnderTest.error).equals("Fehler beim Laden der Ereignisse");
      expect(unitUnderTest.isVorfaelleMaintained).toBeFalsy();
    });
  });

  describe("sendEreignisse", () => {
    it("should_sendEreignisse_when_wahlbezirkIDIsGiven", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());
      unitUnderTest.isVorfaelleMaintained = false;
      unitUnderTest.wahlbezirkEreignisse = prepareWahlbezirkEreignisse()
        .keineVorfaelle(true)
        .ereigniseintraege([])
        .build();

      const mockedDatetime = new Date();

      mockDefinitions.saveEreignisse.mockReturnValue(
        Promise.resolve({ updateDatetime: mockedDatetime })
      );

      await unitUnderTest.sendEreignisse();

      await nextTick();
      expect(mockDefinitions.saveEreignisse).toHaveBeenCalledWith(
        wahlbezirkID,
        unitUnderTest.wahlbezirkEreignisse,
        true
      );
      expect(unitUnderTest.isVorfaelleMaintained).toBeTruthy();
    });

    it("should_notChangeVorfaelleMaintained_when_postCallFailed", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());
      unitUnderTest.isVorfaelleMaintained = false;

      mockDefinitions.saveEreignisse.mockRejectedValue(
        new Error("error in service")
      );

      await unitUnderTest.sendEreignisse();

      await nextTick();

      expect(unitUnderTest.isVorfaelleMaintained).toBeFalsy();
    });
  });

  describe("updateBeschreibungByIndex", () => {
    it("should_doNoting_when_noEreignisEintraegeAreGiven", () => {
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
        ereigniseintraege: [],
      };

      unitUnderTest.updateBeschreibungByIndex(BESCHREIBUNG_NEU, 1);

      expect(
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege
      ).toStrictEqual([]);
    });

    it("should_doNothing_when_indexIsOutOfRange", () => {
      const eintragNotToChange = {
        ereignisart: EreignisartEnum.Vorfall,
        beschreibung: BESCHREIBUNG,
      };
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
        ereigniseintraege: [eintragNotToChange],
      };

      unitUnderTest.updateBeschreibungByIndex(BESCHREIBUNG_NEU, 1);

      expect(eintragNotToChange.beschreibung).toEqual(BESCHREIBUNG);
    });

    it("should_updateBeschreibung_when_beschreibungGiven", () => {
      const eintragToChange = {
        ereignisart: EreignisartEnum.Vorfall,
        beschreibung: BESCHREIBUNG,
      };
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
        ereigniseintraege: [eintragToChange],
      };

      unitUnderTest.updateBeschreibungByIndex(BESCHREIBUNG_NEU, 0);

      expect(eintragToChange.beschreibung).toEqual(BESCHREIBUNG_NEU);
    });
  });

  describe("updateUhrzeitByIndex", () => {
    it("should_doNothing_when_noEreignisEintraegeAreGiven", () => {
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
        ereigniseintraege: [],
      };

      unitUnderTest.updateUhrzeitByIndex(new Date(), 1);

      expect(
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege
      ).toStrictEqual([]);
    });

    it("should_doNothing_when_indexIsOutOfRange", () => {
      const dateAsString = "2025-04-29T09:33:42";
      const eintragNotToChange = {
        uhrzeit: new Date(dateAsString),
        ereignisart: EreignisartEnum.Vorfall,
      };
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
        ereigniseintraege: [eintragNotToChange],
      };

      unitUnderTest.updateUhrzeitByIndex(new Date(), 1);

      expect(eintragNotToChange.uhrzeit).toEqual(new Date(dateAsString));
    });

    it("should_updateUhrzeit_when_uhrzeitIsGiven", () => {
      const dateAsString = "2025-04-29T09:33:42";
      const eintragToChange = {
        uhrzeit: new Date(dateAsString),
        ereignisart: EreignisartEnum.Vorfall,
      };
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
      const eintragToChange = {
        uhrzeit: new Date(dateAsString),
        ereignisart: EreignisartEnum.Vorfall,
      };
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
        ereigniseintraege: [eintragToChange],
      };

      unitUnderTest.updateUhrzeitByIndex(undefined, 0);

      expect(eintragToChange.uhrzeit).toBeUndefined();
    });
  });

  describe("onSchliessungsuhrzeitSentChanged", () => {
    it("should_setKeinVorfaelleTrue_when_schliessungsuhrzeitSentHasChangedAndOnlyVorkommnisseAreGiven", async () => {
      const schliessungsuhrzeitSend = new Date();

      const ereignisEintraege = [
        prepareEreignis()
          .ereignisart(EreignisartEnum.Vorkommnis)
          .uhrzeit(new Date(schliessungsuhrzeitSend.getTime() + 1))
          .build(),
        prepareEreignis()
          .ereignisart(EreignisartEnum.Vorkommnis)
          .uhrzeit(new Date(schliessungsuhrzeitSend.getTime() + 2))
          .build(),
      ];
      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = ereignisEintraege;

      const spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit = spyOn(
        ImportAllFromEreignisArt,
        "getEreignisArtForDateRelatedToSchliessungsuhrzeit"
      );

      await unitUnderTest.onSchliessungsuhrzeitSentChanged(
        schliessungsuhrzeitSend
      );

      expect(
        spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit.mock.calls.length
      ).toStrictEqual(ereignisEintraege.length);
      expect(unitUnderTest.wahlbezirkEreignisse.keineVorfaelle).toStrictEqual(
        true
      );

      spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit.mockRestore();
    });

    it("should_setKeinVorfaelleTrue_when_schliessungsuhrzeitSentHasChangedAndNoEreignisseAreGiven", async () => {
      const schliessungsuhrzeitSend = new Date();

      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [];

      await unitUnderTest.onSchliessungsuhrzeitSentChanged(
        schliessungsuhrzeitSend
      );

      expect(unitUnderTest.wahlbezirkEreignisse.keineVorfaelle).toStrictEqual(
        true
      );
    });

    it("should_setKeinVorfaelleFalse_when_schliessungsuhrzeitSentHasChangedAndVorfaelleAreGiven", async () => {
      const schliessungsuhrzeitSend = new Date();

      unitUnderTest.wahlbezirkEreignisse.ereigniseintraege = [
        prepareEreignis()
          .ereignisart(EreignisartEnum.Vorfall)
          .uhrzeit(new Date(schliessungsuhrzeitSend.getTime()))
          .build(),
      ];

      await unitUnderTest.onSchliessungsuhrzeitSentChanged(
        schliessungsuhrzeitSend
      );

      expect(unitUnderTest.wahlbezirkEreignisse.keineVorfaelle).toStrictEqual(
        false
      );
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
      expected: false,
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
      expected: false,
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
      expected: true,
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
      expected: true,
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
      expected: false,
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
      expected: true,
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
      expected: true,
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
      expected: true,
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
      expected: false,
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
      expected: false,
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
      expected: true,
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
      expected: true,
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
      expected: true,
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
      expected: false,
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
      expected: true,
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
      expected: true,
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
      expected: true,
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
      expected: true,
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
      expected: false,
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
      expected: false,
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
      expected: true,
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
      expected: true,
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
      expected: false,
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
      expected: true,
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
      expected: true,
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
      expected: true,
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
      expected: false,
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
      expected: false,
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
      expected: true,
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
      expected: true,
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
      expected: true,
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
      expected: false,
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
      expected: false,
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
      expected: true,
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
      expected: true,
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
      expected: false,
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
      expected: true,
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
      expected: true,
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
      expected: true,
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
      expected: false,
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
      expected: true,
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
      expected: true,
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
      expected: false,
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
      expected: true,
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
      expected: true,
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
      expected: false,
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
      expected: true,
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
      expected: true,
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
      expected: false,
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
      expected: true,
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
      expected: true,
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
      expected: true,
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
      expected: false,
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
      expected: true,
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
      expected: true,
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
      expected: false,
    },
  ];
}
