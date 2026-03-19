import { useWahlbezirkTestDataFactory } from "@tests/utils/wahlbezirk/WahlbezirkTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useUngueltigeWahlscheineService } from "@/composables/basisdaten/ungueltigeWahlscheineService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";
import { REQUEST_HEADER_OFFLINE_STRATEGY } from "@/constants.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
  getUngueltigeWahlscheine: vi.fn(),
  configurationConstructor: vi.fn().mockImplementation(() => ({})),
  mapToModel: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-basisdaten-api", () => ({
  UngueltigeWahlscheineControllerApi: vi.fn().mockImplementation(() => ({
    getUngueltigeWahlscheine: mockDefinitions.getUngueltigeWahlscheine,
  })),
  Configuration: mockDefinitions.configurationConstructor,
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));
vi.mock("@/composables/basisdaten/ungueltigeWahlscheineMapper.ts", () => ({
  useUngueltigeWahlscheineMapper: () => ({
    toModel: mockDefinitions.mapToModel,
  }),
}));

const { createUngueltigerWahlschein } = useWahlbezirkTestDataFactory();

describe("ungueltigeWahlscheineService.ts", () => {
  const { getUngueltigeWahlscheine } = useUngueltigeWahlscheineService();

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getUngueltigeWahlscheine", () => {
    it("should_returnListOfUngueltigeWahlscheine_when_apiCallSucceeded", async () => {
      const wahltagID = "wahltagID";
      const wahlbezirksArt = WahlbezirksArtEnum.BWB;

      const mockedUngueltigeWahlscheineApiResponseData = "c;s;v";
      mockDefinitions.getUngueltigeWahlscheine.mockReturnValue({
        status: 200,
        data: mockedUngueltigeWahlscheineApiResponseData,
      });

      const mockedMapperResponse = [
        createUngueltigerWahlschein(),
        createUngueltigerWahlschein(),
      ];
      mockDefinitions.mapToModel.mockReturnValue(mockedMapperResponse);

      const result = await getUngueltigeWahlscheine(wahltagID, wahlbezirksArt);

      expect(result).toStrictEqual(mockedMapperResponse);
      expect(mockDefinitions.mapToModel.mock.calls).toStrictEqual([
        [mockedUngueltigeWahlscheineApiResponseData],
      ]);
      expect(mockDefinitions.getUngueltigeWahlscheine).toHaveBeenCalledWith(
        wahltagID,
        wahlbezirksArt,
        expect.objectContaining({
          headers: expect.objectContaining({
            [REQUEST_HEADER_OFFLINE_STRATEGY]: FetchStrategiesEnum.STRATEGY_ONLINE_FIRST
          })
        })
      );
      expect(mockDefinitions.addNotification.mock.calls).toStrictEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_notAddSuccessNotification_when_sendNotificationIsFalse", async () => {
      const wahltagID = "wahltagID";
      const wahlbezirksArt = WahlbezirksArtEnum.BWB;

      const mockedUngueltigeWahlscheineApiResponseData = "c;s;v";
      mockDefinitions.getUngueltigeWahlscheine.mockReturnValue({
        status: 200,
        data: mockedUngueltigeWahlscheineApiResponseData,
      });

      const mockedMapperResponse = [
        createUngueltigerWahlschein(),
        createUngueltigerWahlschein(),
      ];
      mockDefinitions.mapToModel.mockReturnValue(mockedMapperResponse);

      await getUngueltigeWahlscheine(wahltagID, wahlbezirksArt, false);

      expect(mockDefinitions.addNotification.mock.calls).toHaveLength(0);
      expect(mockDefinitions.getUngueltigeWahlscheine).toHaveBeenCalledWith(
        wahltagID,
        wahlbezirksArt,
        expect.objectContaining({
          headers: expect.objectContaining({
            [REQUEST_HEADER_OFFLINE_STRATEGY]: FetchStrategiesEnum.STRATEGY_ONLINE_FIRST
          })
        })
      );
    });

    it("should_addNotification_when_apiCallFailed", async () => {
      const wahltagID = "wahltagID";
      const wahlbezirksArt = WahlbezirksArtEnum.BWB;

      const apiCallError = new Error(
        "mocked api call get ungueltige Wahlscheine failed"
      );
      mockDefinitions.getUngueltigeWahlscheine.mockRejectedValue(apiCallError);

      await expect(
        getUngueltigeWahlscheine(wahltagID, wahlbezirksArt)
      ).rejects.toThrow(apiCallError);

      expect(mockDefinitions.mapToModel.mock.calls).toHaveLength(0);
      expect(mockDefinitions.getUngueltigeWahlscheine).toHaveBeenCalledWith(
        wahltagID,
        wahlbezirksArt,
        expect.objectContaining({
          headers: expect.objectContaining({
            [REQUEST_HEADER_OFFLINE_STRATEGY]: FetchStrategiesEnum.STRATEGY_ONLINE_FIRST
          })
        })
      );
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
    });

    it("should_notAddNotification_when_apiCallFailedButSendNotificationParameterIsFalse", async () => {
      const wahltagID = "wahltagID";
      const wahlbezirksArt = WahlbezirksArtEnum.BWB;

      const apiCallError = new Error(
        "mocked api call get ungueltige Wahlscheine failed"
      );
      mockDefinitions.getUngueltigeWahlscheine.mockRejectedValue(apiCallError);

      await expect(
        getUngueltigeWahlscheine(wahltagID, wahlbezirksArt, false)
      ).rejects.toThrow(apiCallError);

      expect(mockDefinitions.mapToModel.mock.calls).toHaveLength(0);
      expect(mockDefinitions.getUngueltigeWahlscheine).toHaveBeenCalledWith(
        wahltagID,
        wahlbezirksArt,
        expect.objectContaining({
          headers: expect.objectContaining({
            [REQUEST_HEADER_OFFLINE_STRATEGY]: FetchStrategiesEnum.STRATEGY_ONLINE_FIRST
          })
        })
      );
      expect(mockDefinitions.addNotification.mock.calls).toHaveLength(0);
    });
  });
});