import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/user";
import { useEreignisStore } from "@/stores/vorfaelleundvorkommnisseStore.ts";
import User from "@/types/User";
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

describe("vorfaelleundvorkommnisseStore.ts", () => {
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

  describe("loadEreignisse", () => {
    it("should_setWahlbezirkEreignisse_when_userHasWahlbezirkID", async () => {
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
});

function createUser(wahlbezirkID: string | undefined): User {
  //TODO create Issue to use interface for User and provide BuilderImpl-Class => #853
  const user = new User();

  user.wahlbezirkID = wahlbezirkID;

  return user;
}
