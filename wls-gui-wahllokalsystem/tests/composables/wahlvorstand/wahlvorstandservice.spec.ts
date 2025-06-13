import { useWahlvorstandTestDataFactory } from "@tests/utils/wahlvorstand/WahlvorstandTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlvorstandService } from "@/composables/wahlvorstand/wahlvorstandService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  getWahlvorstand: vi.fn(),
  postWahlvorstand: vi.fn(),
  mapDtoToModel: vi.fn(),
  mapModelToDto: vi.fn(),
}));

vi.mock(
  import("@/api/wls-clients/generated-wahlvorstand-api"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      ...mod,
      WahlvorstandControllerApi: vi.fn().mockImplementation(() => ({
        getWahlvorstand: mockDefinitions.getWahlvorstand,
        postWahlvorstand: mockDefinitions.postWahlvorstand,
      })),
      Configuration: vi.fn(),
    };
  }
);

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

vi.mock("@/composables/wahlvorstand/wahlvorstandMapper", () => ({
  useWahlvorstandMapper: () => ({
    toModel: mockDefinitions.mapDtoToModel,
    toDto: mockDefinitions.mapModelToDto,
  }),
}));

const { createWahlvorstandDTO, createWahlvorstand } =
  useWahlvorstandTestDataFactory();

describe("WahlvorstandService.ts", () => {
  const unitUnderTest = useWahlvorstandService();

  const fakedNow = new Date();

  beforeEach(() => {
    vi.useFakeTimers();
    vi.setSystemTime(fakedNow);
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
    vi.useRealTimers();
  });

  describe("getWahlvorstand", () => {
    it.each([
      { forceUpdate: true, expectedApiCallHeader: true },
      { forceUpdate: false, expectedApiCallHeader: false },
      { forceUpdate: undefined, expectedApiCallHeader: false },
    ])(
      "should_callApiWithForceUpdateEquals'$expectedApiCallHeader'_when_calledWithForceUpdateEquals'$forceUpdate'",
      async ({ forceUpdate, expectedApiCallHeader }) => {
        const wahlbezirkID = "wahlbezirkID";

        mockDefinitions.getWahlvorstand.mockReturnValue(
          Promise.resolve({ data: createWahlvorstandDTO() })
        );

        await unitUnderTest.getWahlvorstand(wahlbezirkID, {
          forceUpdate: forceUpdate,
          sendNotification: false,
        });

        expect(mockDefinitions.getWahlvorstand.mock.calls).toStrictEqual([
          [wahlbezirkID, expectedApiCallHeader],
        ]);
      }
    );

    it("should_callUserNotificationWithSuccessAndReturnMappedResponse_when_apiCallSucceeded", async () => {
      const mockedMappedWahlvorstand = createWahlvorstand();
      mockDefinitions.mapDtoToModel.mockReturnValue(mockedMappedWahlvorstand);

      mockDefinitions.getWahlvorstand.mockReturnValue(
        Promise.resolve({ data: createWahlvorstandDTO() })
      );
      const result = await unitUnderTest.getWahlvorstand("wahlbezirkID", {
        forceUpdate: false,
        sendNotification: true,
      });

      expect(result).toStrictEqual(mockedMappedWahlvorstand);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_callUserNotificationWithError_when_apiCallFailed", async () => {
      mockDefinitions.getWahlvorstand.mockReturnValue(
        Promise.reject("mocked api call failed")
      );
      await expect(
        unitUnderTest.getWahlvorstand("wahlbezirkID", {
          forceUpdate: false,
          sendNotification: true,
        })
      ).rejects.toThrow("mocked api call failed");

      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });
  });

  describe("saveWahlvorstand", () => {
    it("should_callUserNotificationWithSuccessAndReturnObject_when_apiCallSucceeded", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const wahlvorstand = createWahlvorstand();

      const mockedMappedWahlvorstand = createWahlvorstandDTO();
      mockDefinitions.mapModelToDto.mockReturnValue(mockedMappedWahlvorstand);

      const result = await unitUnderTest.saveWahlvorstand(
        wahlbezirkID,
        wahlvorstand
      );

      const expectedResult = { updateDatetime: fakedNow };
      expect(result).toStrictEqual(expectedResult);
      expect(mockDefinitions.postWahlvorstand.mock.calls).toStrictEqual([
        [wahlbezirkID, mockedMappedWahlvorstand],
      ]);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_callUserNotificationWithError_when_apiCallFailed", async () => {
      const wahlbezirkID = "wahlbezirkID";
      const wahlvorstand = createWahlvorstand();

      const mockedMappedWahlvorstand = createWahlvorstandDTO();
      mockDefinitions.mapModelToDto.mockReturnValue(mockedMappedWahlvorstand);
      mockDefinitions.postWahlvorstand.mockReturnValue(
        Promise.reject("mocked api call failed")
      );

      await expect(
        unitUnderTest.saveWahlvorstand(wahlbezirkID, wahlvorstand)
      ).rejects.toThrow("mocked api call failed");

      expect(mockDefinitions.postWahlvorstand.mock.calls).toStrictEqual([
        [wahlbezirkID, mockedMappedWahlvorstand],
      ]);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });
  });
});
