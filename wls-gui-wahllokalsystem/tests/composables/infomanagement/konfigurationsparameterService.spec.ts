import { useKonfigurationsparameterTestDataFactory } from "@tests/utils/infomanagement/KonfigurationsparameterTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useKonfigurationsparameterService } from "@/composables/infomanagement/konfigurationsparameterService.ts";
import { REQUEST_HEADER_OFFLINE_STRATEGY } from "@/constants.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getKonfigurations: vi.fn(),
  toModel: vi.fn(),
  addNotification: vi.fn(),
  configurationConstructor: vi.fn(),
}));

vi.mock(
  "@/composables/infomanagement/konfigurationsparameterMapper.ts",
  () => ({
    useKonfigurationsparameterMapper: () => ({
      toModel: mockDefinitions.toModel,
    }),
  })
);
vi.mock("@/api/wls-clients/generated-infomanagement-api", () => ({
  KonfigurationControllerApi: vi.fn().mockImplementation(
    class MockedKonfigurationControllerApi {
      getKonfigurations = mockDefinitions.getKonfigurations;
    } as never
  ),
  Configuration: mockDefinitions.configurationConstructor,
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

const { createKonfigurationDtoList, mapDtosToModel } =
  useKonfigurationsparameterTestDataFactory();

describe("konfigurationsparameterService", () => {
  const { getKonfigurationsparameter } = useKonfigurationsparameterService();

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getKonfigurationsparameter", () => {
    it("should_returnKonfigurationsparameter_when_calledSuccessfully", async () => {
      const mockedKonfigurationsparameterDto = createKonfigurationDtoList(3);
      const mockedMappedKonfigurationsparameter = mapDtosToModel(
        mockedKonfigurationsparameterDto
      );

      mockDefinitions.getKonfigurations.mockReturnValue(
        Promise.resolve(mockedKonfigurationsparameterDto)
      );
      mockDefinitions.toModel.mockReturnValue(
        mockedMappedKonfigurationsparameter
      );

      const result = await getKonfigurationsparameter();

      expect(result).toEqual(mockedMappedKonfigurationsparameter);
      expect(mockDefinitions.getKonfigurations).toHaveBeenCalledWith(
        expect.objectContaining({
          headers: expect.objectContaining({
            [REQUEST_HEADER_OFFLINE_STRATEGY]:
              FetchStrategiesEnum.STRATEGY_ONLINE_FIRST,
          }),
        })
      );
    });

    it("should_triggerNotification_when_anExceptionOccurredDuringApiCall", async () => {
      mockDefinitions.getKonfigurations.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(async () =>
        getKonfigurationsparameter()
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        1
      );
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        UserNotificationCategoryEnum.ERROR,
      ]);
      expect(mockDefinitions.getKonfigurations).toHaveBeenCalledWith(
        expect.objectContaining({
          headers: expect.objectContaining({
            [REQUEST_HEADER_OFFLINE_STRATEGY]:
              FetchStrategiesEnum.STRATEGY_ONLINE_FIRST,
          }),
        })
      );
    });

    it("should_notTriggerNotification_when_anExceptionOccurredDuringApiCallAndNotificationFlagFalse", async () => {
      mockDefinitions.getKonfigurations.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(async () =>
        getKonfigurationsparameter(false)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
      expect(mockDefinitions.getKonfigurations).toHaveBeenCalledWith(
        expect.objectContaining({
          headers: expect.objectContaining({
            [REQUEST_HEADER_OFFLINE_STRATEGY]:
              FetchStrategiesEnum.STRATEGY_ONLINE_FIRST,
          }),
        })
      );
    });
  });
});
