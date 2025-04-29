import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { User } from "@/types/User";
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

describe("ereignisStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useEreignisStore>;

  beforeEach(() => {
    // creates a fresh pinia and makes it active
    // so it's automatically picked up by any useStore() call
    // without having to pass it to it: `useStore(pinia)`
    setActivePinia(createPinia());
    vi.useFakeTimers({
      now: mockedNow,
    });
    unitUnderTest = useEreignisStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
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

  describe("addEreignis", () => {
    it("should_addEreignisToWahlbezirkEreignisse_when_ereignisIsAdded", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      const user = new User();
      user.wahlbezirkID = wahlbezirkID;
      userStore.setUser(user);

      const mockedWahlbezirkEreignisse =
        WahlbezirkEreignisseBuilder.createEmptyWahlbezirkEreignisse();
      mockDefinitions.getEreignisse.mockReturnValue(mockedWahlbezirkEreignisse);

      await unitUnderTest.addEreignis();

      expect(unitUnderTest.wahlbezirkEreignisse.ereigniseintraege).toHaveLength(
        1
      );
    });
  });

  describe("updateUhrzeitByIndex", () => {
    it("should_doNothing_when_noEreignisEintraegeAreGiven", () => {
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
      };

      unitUnderTest.updateUhrzeitByIndex("12:12", 1);

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

      unitUnderTest.updateUhrzeitByIndex("12:12", 1);

      expect(eintragNotToChange.uhrzeit).toEqual(new Date(dateAsString));
    });

    it("should_updateUhrzeit_when_uhrzeitIsGiven", () => {
      const dateAsString = "2025-04-29T09:33:42";
      const eintragToChange = { uhrzeit: new Date(dateAsString) };
      unitUnderTest.wahlbezirkEreignisse = {
        wahlbezirkID: "wahlbezirkID",
        ereigniseintraege: [eintragToChange],
      };

      unitUnderTest.updateUhrzeitByIndex("12:12", 0);

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
});

function createUser(wahlbezirkID: string | undefined): User {
  //TODO create Issue to use interface for User and provide BuilderImpl-Class => #853
  const user = new User();

  user.wahlbezirkID = wahlbezirkID;

  return user;
}
