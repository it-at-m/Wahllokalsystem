import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import {
  afterEach,
  beforeAll,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useUserStore } from "@/stores/userStore.ts";
import { createUserLocalDevelopment } from "@/types/User.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getUser: vi.fn(),
  initElectionWorkflowState: vi.fn(),
}));

vi.mock("@/composables/user/userService", () => ({
  useUserService: () => ({
    getUser: mockDefinitions.getUser,
  }),
}));
vi.mock("@/stores/workflowStore.ts", () => ({
  useWorkflowStore: () => ({
    initElectionWorkflowState: mockDefinitions.initElectionWorkflowState,
  }),
}));

const { prepareUser } = useUserTestDataFactory();

describe("userStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useUserStore>;

  beforeAll(() => {
    const mockPostMessage = vi.fn();
    const mockController = { postMessage: mockPostMessage };
    Object.defineProperty(navigator, "serviceWorker", {
      value: {
        controller: mockController,
      },
      writable: true,
    });
  });

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

      await expect(() => unitUnderTest.loadUser()).rejects.toThrow(
        "error in service"
      );
      expect(unitUnderTest.user).toStrictEqual(user);
    });

    it("should_setUser_when_serviceCalledSuccessfully", async () => {
      const user = createUserLocalDevelopment();
      mockDefinitions.getUser.mockResolvedValue(user);

      await unitUnderTest.loadUser();

      expect(unitUnderTest.user).toStrictEqual(user);
      expect(
        mockDefinitions.initElectionWorkflowState.mock.calls
      ).toStrictEqual([
        [user.wahlMetaData[0]?.wahlID, user.wahlMetaData[0]?.wahlbezirkID],
      ]);
    });
  });

  describe("setUser", () => {
    it("should_setUser_when_givenUser", () => {
      const user = prepareUser().build();
      unitUnderTest.setUser(user);

      expect(unitUnderTest.user).toStrictEqual(user);
    });

    it("should_setDefaultUser_when_serviceCallFailedAndInProdMode", async () => {
      mockDefinitions.getUser.mockRejectedValue(new Error("error in service"));

      vi.stubEnv("DEV", false);

      expect(import.meta.env.DEV).toBe(false);
      await expect(() => unitUnderTest.loadUser()).rejects.toThrow(
        "error in service"
      );

      expect(unitUnderTest.user).toStrictEqual({
        username: "",
        email: "",
        userEnabled: false,
        wahltagID: "",
        wahltag: "",
        wahlbezirkID: "",
        wahlbezirkNummer: "",
        wahlbezirksArt: WahlbezirksArtEnum.UWB,
        pin: "",
        authorities: [],
        wahlMetaData: [
          {
            wahlbezirkID: "",
            wahlnummer: "",
            wahlID: "",
          },
        ],
      });
    });
  });

  describe("getWahlbezirkIdFromWahlMetaDataByWahlId", () => {
    it("should_returnWahlbezirkId_when_givenWahlIdThatExists", () => {
      const wahlbezirkIdToReturn = "ich bin eine id";
      const wahlID = "wahlID";

      unitUnderTest.setUser(
        prepareUser()
          .wahlMetaData([
            {
              wahlbezirkID: wahlbezirkIdToReturn,
              wahlID: wahlID,
              wahlnummer: "0",
            },
            {
              wahlbezirkID: "andere id",
              wahlID: "andere wahl id",
              wahlnummer: "1",
            },
          ])
          .build()
      );

      expect(
        unitUnderTest.getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID)
      ).toStrictEqual(wahlbezirkIdToReturn);
    });

    it("should_notReturnWahlbezirkId_when_givenWahlIdThatDoesNotExist", () => {
      unitUnderTest.setUser(
        prepareUser()
          .wahlMetaData([
            {
              wahlbezirkID: "id",
              wahlID: "wahl id",
              wahlnummer: "0",
            },
            {
              wahlbezirkID: "andere id",
              wahlID: "andere wahl id",
              wahlnummer: "1",
            },
          ])
          .build()
      );

      expect(
        unitUnderTest.getWahlbezirkIdFromWahlMetaDataByWahlId(
          "non existant wahl id"
        )
      ).toBeUndefined();
    });
  });

  describe("currentUserWahlbezirkID", () => {
    it("should_returnWahlbezirkId_when_wahlbezirkIdExists", () => {
      const wahlbezirkID = "ich bin eine id";
      unitUnderTest.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      expect(unitUnderTest.currentUserWahlbezirkID).toStrictEqual(wahlbezirkID);
    });
  });

  describe("currentUserWahltagID", () => {
    it("should_returnWahltagId_when_wahltagIdExists", () => {
      const wahltagID = "ich bin eine id";
      unitUnderTest.setUser(prepareUser().wahltagID(wahltagID).build());

      expect(unitUnderTest.currentUserWahltagID).toStrictEqual(wahltagID);
    });
  });

  describe("currentUserWahlbezirksArt", () => {
    it.each([
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

  describe("currentUserWahlMetadata", () => {
    it("should_returnWahlMetadata_when_wahlMetadataExists", () => {
      const wahlMetadata = [
        {
          wahlbezirkID: "123",
          wahlnummer: "1",
          wahlID: "ID zu wahlnumemr 1",
        },
      ];
      unitUnderTest.setUser(prepareUser().wahlMetaData(wahlMetadata).build());

      expect(unitUnderTest.currentUserWahlMetadata).toStrictEqual(wahlMetadata);
    });
  });

  describe("isUWB", () => {
    it("should_returnTrue_when_wahlbezirksArtIsUWB", () => {
      unitUnderTest.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );

      expect(unitUnderTest.isUWB).toStrictEqual(true);
    });

    it("should_returnFalse_when_wahlbezirksArtIsBWB", () => {
      unitUnderTest.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );

      expect(unitUnderTest.isUWB).toStrictEqual(false);
    });
  });

  describe("isBWB", () => {
    it("should_returnTrue_when_wahlbezirksArtIsBWB", () => {
      unitUnderTest.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );

      expect(unitUnderTest.isBWB).toStrictEqual(true);
    });

    it("should_returnFalse_when_wahlbezirksArtIsUWB", () => {
      unitUnderTest.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );

      expect(unitUnderTest.isBWB).toStrictEqual(false);
    });
  });
});
