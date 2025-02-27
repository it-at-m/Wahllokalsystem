import { createTestingPinia } from "@pinia/testing";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/user";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";
import User from "@/types/User";
import { WahlvorstandBuilder } from "@/types/wahlvorstand/wahlvorstand";
import { WahlvorstandsmitgliedBuilder } from "@/types/wahlvorstand/wahlvorstandsmitglied";

const mockDefinitions = vi.hoisted(() => ({
  isSchriftfuehrer: vi.fn(),
  isWahlvorsteher: vi.fn(),
  saveWahlvorstand: vi.fn(),
  getWahlvorstand: vi.fn(),
}));

vi.mock("@/types/wahlvorstand/wahlvorstandsmitgliedFunktion", () => ({
  isSchriftfuehrer: mockDefinitions.isSchriftfuehrer,
  isWahlvorsteher: mockDefinitions.isWahlvorsteher,
}));
vi.mock("@/composables/wahlvorstand/wahlvorstandService", () => ({
  useWahlvorstandService: () => ({
    saveWahlvorstand: mockDefinitions.saveWahlvorstand,
    getWahlvorstand: mockDefinitions.getWahlvorstand,
  }),
}));

const mockedNow = new Date();

describe("wahlvorstandStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlvorstandStore>;

  beforeEach(() => {
    // creates a fresh pinia and makes it active
    // so it's automatically picked up by any useStore() call
    // without having to pass it to it: `useStore(pinia)`
    setActivePinia(createPinia());
    vi.useFakeTimers({
      now: mockedNow,
    });
    unitUnderTest = useWahlvorstandStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("isSchriftfuehrerAnwesend", () => {
    it("should_returnFalse_when_noMitgliedExists", () => {
      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [];

      expect(unitUnderTest.isSchriftfuehrerAnwesend).toStrictEqual(false);
    });

    it("should_returnTrue_when_atLeastOneMitgliedMatches", () => {
      mockDefinitions.isSchriftfuehrer.mockReturnValue(true);

      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [
        WahlvorstandsmitgliedBuilder.createMinimal()
          .withFunktion("SB")
          .withAnwesend(true),
        WahlvorstandsmitgliedBuilder.createMinimal().withFunktion("W"),
      ];

      expect(unitUnderTest.isSchriftfuehrerAnwesend).toStrictEqual(true);

      expect(mockDefinitions.isSchriftfuehrer.mock.calls[0][0]).toStrictEqual(
        "SB"
      );
    });

    it("should_returnFalse_when_whenMitgliedWithFunktionExistsButIsNotAnwesend", () => {
      mockDefinitions.isSchriftfuehrer.mockReturnValue(true);

      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [
        WahlvorstandsmitgliedBuilder.createMinimal().withFunktion("SB"),
        WahlvorstandsmitgliedBuilder.createMinimal().withFunktion("W"),
      ];

      expect(unitUnderTest.isSchriftfuehrerAnwesend).toStrictEqual(false);

      expect(mockDefinitions.isSchriftfuehrer.mock.calls[0][0]).toStrictEqual(
        "SB"
      );
      expect(mockDefinitions.isSchriftfuehrer.mock.calls[1][0]).toStrictEqual(
        "W"
      );
    });

    it("should_returnFalse_when_noMitgliedMatchesFunktion", () => {
      mockDefinitions.isSchriftfuehrer.mockReturnValue(false);

      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [
        WahlvorstandsmitgliedBuilder.createMinimal()
          .withFunktion("SB")
          .withAnwesend(true),
        WahlvorstandsmitgliedBuilder.createMinimal()
          .withFunktion("W")
          .withAnwesend(true),
      ];
      expect(unitUnderTest.isSchriftfuehrerAnwesend).toStrictEqual(false);
      expect(mockDefinitions.isSchriftfuehrer.mock.calls[0][0]).toStrictEqual(
        "SB"
      );
      expect(mockDefinitions.isSchriftfuehrer.mock.calls[1][0]).toStrictEqual(
        "W"
      );
    });
  });

  describe("isWahlvorstandAusreichendAnwesend", () => {
    let unitUnderTest: ReturnType<typeof useWahlvorstandStore>;
    beforeEach(() => {
      const testPinia = createTestingPinia({
        createSpy: vi.fn,
      });
      unitUnderTest = useWahlvorstandStore(testPinia);
    });

    it("should_returnTrue_when_schriftfuehrerAndWahlvorsteherAreAnwesend", () => {
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isWahlvorsteherAnwesend = true;
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isSchriftfuehrerAnwesend = true;

      expect(unitUnderTest.isWahlvorstandAusreichendAnwesend).toStrictEqual(
        true
      );
    });

    it("should_returnFalse_when_schriftfuehrerIsNotAnwesendAndWahlvorsteherIsAnwesend", () => {
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isWahlvorsteherAnwesend = true;
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isSchriftfuehrerAnwesend = false;

      expect(unitUnderTest.isWahlvorstandAusreichendAnwesend).toStrictEqual(
        false
      );
    });

    it("should_returnFalse_when_schriftfuehrerIsAnwesendAndWahlvorsteherIsNotAnwesend", () => {
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isWahlvorsteherAnwesend = false;
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isSchriftfuehrerAnwesend = true;

      expect(unitUnderTest.isWahlvorstandAusreichendAnwesend).toStrictEqual(
        false
      );
    });
  });

  describe("sendWahlvorstand", () => {
    it("should_sendWahlvorstand_when_wahlbezirkIDIsGiven", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      const user = new User();
      user.wahlbezirkID = wahlbezirkID;
      userStore.setUser(user);

      const mockedDatetime = new Date();

      mockDefinitions.saveWahlvorstand.mockReturnValue(
        Promise.resolve({ updateDatetime: mockedDatetime })
      );

      await unitUnderTest.sendWahlvorstand();

      expect(mockDefinitions.saveWahlvorstand).toHaveBeenCalledWith(
        wahlbezirkID,
        unitUnderTest.wahlvorstand
      );
    });

    it("should_setLastSend_when_wahlvorstandIsSent", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      const user = new User();
      user.wahlbezirkID = wahlbezirkID;
      userStore.setUser(user);

      expect(unitUnderTest.lastSending).toBeNull();

      const mockedDatetime = new Date();

      mockDefinitions.saveWahlvorstand.mockReturnValue(
        Promise.resolve({ updateDatetime: mockedDatetime })
      );

      await unitUnderTest.sendWahlvorstand();

      expect(unitUnderTest.lastSending).toStrictEqual(mockedNow);
    });

    it("should_notSendWahlvorstand_when_wahlbezirkIDIsNotGiven", async () => {
      const userStore = useUserStore();
      const user = new User();
      user.wahlbezirkID = undefined;
      userStore.setUser(user);

      await unitUnderTest.sendWahlvorstand();

      expect(mockDefinitions.saveWahlvorstand).toBeCalledTimes(0);
    });
  });

  describe("loadWahlvorstand", () => {
    it("should_setWahlvorstand_when_userHasWahlbezirkID", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      const user = new User();
      user.wahlbezirkID = wahlbezirkID;
      userStore.setUser(user);

      const mockedGetWahlvorstand =
        WahlvorstandBuilder.createEmptyWahlvorstand();
      mockDefinitions.getWahlvorstand.mockReturnValue(mockedGetWahlvorstand);

      await unitUnderTest.loadWahlvorstand();

      expect(unitUnderTest.wahlvorstand).toStrictEqual(mockedGetWahlvorstand);
    });

    it("should_setLastLoading_when_wahlvorstandIsLoaded", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      const user = new User();
      user.wahlbezirkID = wahlbezirkID;
      userStore.setUser(user);

      const mockedGetWahlvorstand =
        WahlvorstandBuilder.createEmptyWahlvorstand();
      mockDefinitions.getWahlvorstand.mockReturnValue(mockedGetWahlvorstand);

      expect(unitUnderTest.lastLoading).toBeNull();

      await unitUnderTest.loadWahlvorstand();

      expect(unitUnderTest.lastLoading).toStrictEqual(mockedNow);
    });

    it.each([
      { user: null, when: "userIsNull" },
      {
        user: createUser(undefined),
        when: "usersWahlbezirkIdIsUndefined",
      },
    ])("should_notLoadWahlvorstand_when_$when", async ({ user }) => {
      const userStore = useUserStore();
      userStore.setUser(user);

      await unitUnderTest.loadWahlvorstand();

      expect(mockDefinitions.getWahlvorstand).toHaveBeenCalledTimes(0);
      expect(unitUnderTest.lastLoading).toBeNull();
    });
  });

  describe("changeAnwesendOfMitglied", () => {
    it("should_setGivenAnwesenheitOfMitglied_when_mitgliedWithIdExists", () => {
      const newAnwesenheit = true;
      const mitgliedID = "mitgliedID";

      const mitgliedToChange = WahlvorstandsmitgliedBuilder.createMinimal()
        .withIdentifikator(mitgliedID)
        .withAnwesend(!newAnwesenheit);

      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [
        WahlvorstandsmitgliedBuilder.createMinimal()
          .withIdentifikator(mitgliedID + "andere")
          .withAnwesend(false),
        mitgliedToChange,
        WahlvorstandsmitgliedBuilder.createMinimal()
          .withIdentifikator(mitgliedID + "andere2")
          .withAnwesend(false),
      ];

      unitUnderTest.changeAnwesendOfMitglied(newAnwesenheit, mitgliedID);

      expect(mitgliedToChange.anwesend).toStrictEqual(newAnwesenheit);
    });

    it("should_notUpdateAnwesenheitOfMitglied_when_mitgliedWithIdDoesNotExists", () => {
      const newAnwesenheit = true;
      const mitgliedID = "mitgliedID";

      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [
        WahlvorstandsmitgliedBuilder.createMinimal()
          .withIdentifikator(mitgliedID + "andere")
          .withAnwesend(false),
        WahlvorstandsmitgliedBuilder.createMinimal()
          .withIdentifikator(mitgliedID + "andere2")
          .withAnwesend(false),
        WahlvorstandsmitgliedBuilder.createMinimal()
          .withIdentifikator(mitgliedID + "andere3")
          .withAnwesend(false),
      ];

      unitUnderTest.changeAnwesendOfMitglied(newAnwesenheit, mitgliedID);

      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder.forEach((mitglied) =>
        expect(mitglied.anwesend).toStrictEqual(false)
      );
    });
  });
});

function createUser(wahlbezirkID: string | undefined): User {
  //TODO create Issue to use interface for User and provide BuilderImpl-Class
  const user = new User();

  user.wahlbezirkID = wahlbezirkID;

  return user;
}
