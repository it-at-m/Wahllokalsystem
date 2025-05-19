import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/userStore.ts";
import { createUserLocalDevelopment, User } from "@/types/User.ts";

const mockDefinitions = vi.hoisted(() => ({
  getUser: vi.fn(),
}));

vi.mock("@/composables/user/userService", () => ({
  useUserService: () => ({
    getUser: mockDefinitions.getUser,
  }),
}));

const {
  createUserWithUndefinedWahlbezirkID,
  createUserWithRandomWahlbezirkID,
  createUserWithUndefinedWahltagID,
  createUserWithRandomWahltagID,
  createUserWithUndefinedWahlbezirksArt,
  createUserWithBwbWahlbezirksArt,
  createUserWithUwbWahlbezirksArt,
} = useUserTestDataFactory();

describe("userStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useUserStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useUserStore();
  });

  afterEach(() => {
    vi.unstubAllEnvs();
  });

  describe("loadUser", () => {
    it("should_setUserLocalDevelopment_when_serviceCallFailedAndInDevMode", async () => {
      const user = createUserLocalDevelopment();
      mockDefinitions.getUser.mockRejectedValue(new Error("error in service"));

      await unitUnderTest.loadUser();

      expect(unitUnderTest.user).toStrictEqual(user);
    });

    it("should_setUserNull_when_serviceCallFailedAndInProdMode", async () => {
      mockDefinitions.getUser.mockRejectedValue(new Error("error in service"));

      vi.stubEnv("DEV", false);

      expect(import.meta.env.DEV).toBe(false);
      await unitUnderTest.loadUser();

      expect(unitUnderTest.user).toStrictEqual(null);
    });

    it("should_setUser_when_serviceCalledSuccessfully", async () => {
      const user = createUserLocalDevelopment();
      mockDefinitions.getUser.mockResolvedValue(user);

      await unitUnderTest.loadUser();

      expect(unitUnderTest.user).toStrictEqual(user);
    });
  });

  describe("setUser", () => {
    it("should_setUserNull_when_givenNull", () => {
      unitUnderTest.setUser(null);

      expect(unitUnderTest.user).toStrictEqual(null);
    });

    it("should_setUser_when_givenUser", () => {
      const user = new User();
      unitUnderTest.setUser(user);

      expect(unitUnderTest.user).toStrictEqual(user);
    });
  });

  describe("currentUserWahlbezirkID", () => {
    it("should_returnUndefined_when_noWahlbezirkIdExists", () => {
      unitUnderTest.setUser(createUserWithUndefinedWahlbezirkID());

      expect(unitUnderTest.currentUserWahlbezirkID).toStrictEqual(undefined);
    });

    it("should_returnWahlbezirkId_when_wahlbezirkIdExists", () => {
      const user: User = createUserWithRandomWahlbezirkID();
      unitUnderTest.setUser(user);

      expect(unitUnderTest.currentUserWahlbezirkID).toStrictEqual(
        user.wahlbezirkID
      );
    });
  });

  describe("currentUserWahltagID", () => {
    it("should_returnUndefined_when_noWahltagIdExists", () => {
      unitUnderTest.setUser(createUserWithUndefinedWahltagID());

      expect(unitUnderTest.currentUserWahltagID).toStrictEqual(undefined);
    });

    it("should_returnWahltagId_when_wahltagIdExists", () => {
      const user: User = createUserWithRandomWahltagID();
      unitUnderTest.setUser(user);

      expect(unitUnderTest.currentUserWahltagID).toStrictEqual(user.wahltagID);
    });
  });

  describe("currentUserWahlbezirksArt", () => {
    it("should_returnBwb_when_wahlbezirksArtIsUndefined", () => {
      unitUnderTest.setUser(createUserWithUndefinedWahlbezirksArt());

      expect(unitUnderTest.currentUserWahlbezirksArt).toStrictEqual("BWB");
    });

    it("should_returnBwb_when_wahlbezirksArtIsBwb", () => {
      unitUnderTest.setUser(createUserWithBwbWahlbezirksArt());

      expect(unitUnderTest.currentUserWahlbezirksArt).toStrictEqual("BWB");
    });

    it("should_returnUwb_when_wahlbezirksArtIsUwb", () => {
      unitUnderTest.setUser(createUserWithUwbWahlbezirksArt());

      expect(unitUnderTest.currentUserWahlbezirksArt).toStrictEqual("UWB");
    });
  });
});
