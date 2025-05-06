import { createTestingPinia } from "@pinia/testing";
import { spyOn } from "@storybook/test";
import { useVorfaelleundvorkommnisseTestDateFactory } from "@tests/utils/vorfaelleundvorkommnisse/VorfaelleundvorkommnisseTestDateFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { User } from "@/types/User";
import * as ImportAllFromEreignisArt from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";
import { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";
import { WahlbezirkEreignisseBuilder } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

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

const { createEreignis } = useVorfaelleundvorkommnisseTestDateFactory();

describe("ereignisStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useEreignisStore>;
  let wahlbezirkStore: ReturnType<typeof useWahlbezirkStore>;

  beforeEach(() => {
    // creates a fresh pinia and makes it active
    // so it's automatically picked up by any useStore() call
    // without having to pass it to it: `useStore(pinia)`
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

  describe("areKeineEreignisseFlagsValid", () => {
    describe("should_returnTrue_when_ereignisseMatchingTheirNoDataFlag", () => {
      it.each([
        {
          testcaseName: "hasVorfaelle=true && hasVorkommnisse=true",
          data: {
            vorfaelle: true,
            vorkommnisse: true,
          },
        },
        {
          testcaseName: "hasVorfaelle=false && hasVorkommnisse=true",
          data: {
            vorfaelle: false,
            vorkommnisse: true,
          },
        },
        {
          testcaseName: "hasVorfaelle=true && hasVorkommnisse=false",
          data: {
            vorfaelle: true,
            vorkommnisse: false,
          },
        },
        {
          testcaseName: "hasVorfaelle=false && hasVorkommnisse=false",
          data: {
            vorfaelle: false,
            vorkommnisse: false,
          },
        },
      ])("$testcaseName", ({ data }) => {
        // @ts-expect-error: cannot set readonly
        unitUnderTest.hasVorfaelle = data.vorfaelle;
        unitUnderTest.wahlbezirkEreignisse.keineVorfaelle = !data.vorfaelle;

        // @ts-expect-error: cannot set readonly
        unitUnderTest.hasVorkommnisse = data.vorkommnisse;
        unitUnderTest.wahlbezirkEreignisse.keineVorkommnisse =
          !data.vorkommnisse;

        expect(unitUnderTest.areKeineEreignisseFlagsValid).toStrictEqual(true);
      });
    });

    describe("should_returnFalse_when_ereignisseNotMatchingTheirNoDataFlag", () => {
      it.each([
        {
          testcaseName: "hasVorfaelle=true && hasVorkommnisse=true",
          data: {
            vorfaelle: true,
            vorkommnisse: true,
          },
        },
        {
          testcaseName: "hasVorfaelle=false && hasVorkommnisse=true",
          data: {
            vorfaelle: false,
            vorkommnisse: true,
          },
        },
        {
          testcaseName: "hasVorfaelle=true && hasVorkommnisse=false",
          data: {
            vorfaelle: true,
            vorkommnisse: false,
          },
        },
        {
          testcaseName: "hasVorfaelle=false && hasVorkommnisse=false",
          data: {
            vorfaelle: false,
            vorkommnisse: false,
          },
        },
      ])("$testcaseName", ({ data }) => {
        // @ts-expect-error: cannot set readonly
        unitUnderTest.hasVorfaelle = data.vorfaelle;
        unitUnderTest.wahlbezirkEreignisse.keineVorfaelle = data.vorfaelle;

        // @ts-expect-error: cannot set readonly
        unitUnderTest.hasVorkommnisse = data.vorkommnisse;
        unitUnderTest.wahlbezirkEreignisse.keineVorkommnisse =
          data.vorkommnisse;

        expect(unitUnderTest.areKeineEreignisseFlagsValid).toStrictEqual(false);
      });
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
      const user = new User();
      user.wahlbezirkID = wahlbezirkID;
      userStore.setUser(user);

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
      const user = new User();
      user.wahlbezirkID = wahlbezirkID;
      userStore.setUser(user);

      const mockedWahlbezirkEreignisse =
        WahlbezirkEreignisseBuilder.createEmptyWahlbezirkEreignisse();
      mockDefinitions.getEreignisse.mockReturnValue(mockedWahlbezirkEreignisse);

      await unitUnderTest.loadEreignisse();

      expect(unitUnderTest.wahlbezirkEreignisse).toStrictEqual(
        mockedWahlbezirkEreignisse
      );
    });

    it.each([
      { user: null, when: "userIsNull" },
      {
        user: createUser(undefined),
        when: "usersWahlbezirkIdIsUndefined",
      },
    ])("should_notLoadWahlbezirkEreignisse_when_$when", async ({ user }) => {
      const userStore = useUserStore();
      userStore.setUser(user);

      await unitUnderTest.loadEreignisse();

      expect(mockDefinitions.getEreignisse).toHaveBeenCalledTimes(0);
      expect(unitUnderTest.wahlbezirkEreignisse.ereigniseintraege).toHaveLength(
        0
      );
    });

    it("should_handleError_when_getEreignisseThrowsError", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      const user = new User();
      user.wahlbezirkID = wahlbezirkID;
      userStore.setUser(user);

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
      const user = new User();
      user.wahlbezirkID = wahlbezirkID;
      userStore.setUser(user);

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
      const user = new User();
      user.wahlbezirkID = undefined;
      userStore.setUser(user);

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
    describe("schliessungsUhrzeitSent", () => {
      it("should_updateEreignisart_when_schliessungsuhrzeitSentHasChanged", async () => {
        const schliessungsuhrzeitSend = new Date();

        const ereignisEintraege = [
          createEreignis(),
          createEreignis(),
          createEreignis(),
        ];
        unitUnderTest.wahlbezirkEreignisse.ereigniseintraege =
          ereignisEintraege;

        wahlbezirkStore.schliessungsUhrzeitSent = schliessungsuhrzeitSend;

        const spyGetEreignisArtForDateRelatedToSchliessungsuhrzeit = spyOn(
          ImportAllFromEreignisArt,
          "getEreignisArtForDateRelatedToSchliessungsuhrzeit"
        );

        await nextTick(); //wait till store has processed changed schliessungsuhrzeit
        await nextTick(); //wait till

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
    });
  });
});

function createUser(wahlbezirkID: string | undefined): User {
  //TODO create Issue to use interface for User and provide BuilderImpl-Class => #853
  const user = new User();

  user.wahlbezirkID = wahlbezirkID;

  return user;
}
