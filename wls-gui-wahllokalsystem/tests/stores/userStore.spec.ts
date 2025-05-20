import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/userStore.ts";
import { createUserLocalDevelopment, User } from "@/types/User.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getUser: vi.fn(),
}));

vi.mock("@/composables/user/userService", () => ({
  useUserService: () => ({
    getUser: mockDefinitions.getUser,
  }),
}));

const { prepareUser } = useUserTestDataFactory();

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
      unitUnderTest.setUser(prepareUser().wahlbezirkID(undefined).build());

      expect(unitUnderTest.currentUserWahlbezirkID).toStrictEqual(undefined);
    });

    it("should_returnWahlbezirkId_when_wahlbezirkIdExists", () => {
      const wahlbezirkID = "ich bin eine id";
      unitUnderTest.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      expect(unitUnderTest.currentUserWahlbezirkID).toStrictEqual(wahlbezirkID);
    });
  });

  describe("currentUserWahltagID", () => {
    it("should_returnUndefined_when_noWahltagIdExists", () => {
      unitUnderTest.setUser(prepareUser().wahltagID(undefined).build());

      expect(unitUnderTest.currentUserWahltagID).toStrictEqual(undefined);
    });

    it("should_returnWahltagId_when_wahltagIdExists", () => {
      const wahltagID = "ich bin eine id";
      unitUnderTest.setUser(prepareUser().wahltagID(wahltagID).build());

      expect(unitUnderTest.currentUserWahltagID).toStrictEqual(wahltagID);
    });
  });

  describe("currentUserWahlbezirksArt", () => {
    it.each([
      { wahlbezirksart: undefined },
      { wahlbezirksart: WahlbezirksArtEnum.UWB },
      { wahlbezirksart: WahlbezirksArtEnum.BWB },
    ])(
      "should_return'$wahlbezirksart'_when_wahlbezirksArtIs'$wahlbezirksart'",
      ({ wahlbezirksart }) => {
        unitUnderTest.setUser(
          prepareUser().wahlbezirksArt(wahlbezirksart).build()
        );

        expect(unitUnderTest.currentUserWahlbezirksArt).toStrictEqual(
          wahlbezirksart
        );
      }
    );
  });

  describe("currentUserHauptWahlID", () => {
    it("should_returnUndefined_when_wahlMetaDataIsUndefined", () => {
      unitUnderTest.setUser(prepareUser().wahlMetaData(undefined).build());

      expect(unitUnderTest.currentUserHauptWahlID).toStrictEqual(undefined);
    });

    it("should_returnHauptWahlId_when_wahlMetaDataHasOneEntry", () => {
      const expectedWahlID = "ID of object with smallest wahlnummer";
      unitUnderTest.setUser(
        prepareUser()
          .wahlMetaData([
            {
              wahlbezirkID: "123",
              wahlnummer: "1",
              wahlID: expectedWahlID,
            },
          ])
          .build()
      );

      expect(unitUnderTest.currentUserHauptWahlID).toStrictEqual(
        expectedWahlID
      );
    });

    it("should_returnHauptWahlIdOfObjectWithSmallestWahlnummer_when_wahlMetaDataHasMultipleEntries", () => {
      const expectedWahlID = "ID of object with smallest wahlnummer";
      unitUnderTest.setUser(
        prepareUser()
          .wahlMetaData([
            {
              wahlbezirkID: "123",
              wahlnummer: "1",
              wahlID: "ID zu wahlnumemr 1",
            },
            {
              wahlbezirkID: "123",
              wahlnummer: "3",
              wahlID: "ID zu wahlunmmer 3",
            },
            {
              wahlbezirkID: "123",
              wahlnummer: "0",
              wahlID: expectedWahlID,
            },
          ])
          .build()
      );

      expect(unitUnderTest.currentUserHauptWahlID).toStrictEqual(
        expectedWahlID
      );
    });
  });
});
