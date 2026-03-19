import { useAxiosTestDataFactory } from "@tests/utils/common/AxiosTestDataFactory.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useAWerteTestDataFactory } from "@tests/utils/ergebnismeldung/common/aWerteTestDataFactory.ts";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useAWerteService } from "@/composables/ergebnismeldung/common/aWerteService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { REQUEST_HEADER_OFFLINE_STRATEGY } from "@/constants.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  mapToModel: vi.fn(),
  addNotification: vi.fn(),
  getAWerte: vi.fn(),
  configurationConstructor: vi.fn().mockImplementation(() => ({})),
}));

vi.mock("@/api/wls-clients/generated-ergebnismeldung-api", () => ({
  AWerteControllerApi: vi.fn().mockImplementation(() => ({
    getAWerte: mockDefinitions.getAWerte,
  })),
  Configuration: mockDefinitions.configurationConstructor,
}));
vi.mock("@/composables/ergebnismeldung/common/aWerteMapper.ts", () => ({
  useAWerteMapper: () => ({
    toModel: mockDefinitions.mapToModel,
  }),
}));
vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

const { generateRandomString } = useCommonTestDataFactory();
const { createAxiosResponse } = useAxiosTestDataFactory();
const { createAWerteDTO, createAWerte } = useAWerteTestDataFactory();

describe("aWerteService.ts", () => {
  let unitUnderTest: ReturnType<typeof useAWerteService>;

  beforeEach(() => {
    unitUnderTest = useAWerteService();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.restoreAllMocks();
  });

  describe("getAWerte", () => {
    it("should_returnAWerteAndSendNotification_when_loadingSucceeded", async () => {
      const wahlbezirkId = generateRandomString(10);

      const mockedAWerteDto = [createAWerteDTO(), createAWerteDTO()];
      mockDefinitions.getAWerte.mockResolvedValue(
        createAxiosResponse({
          status: 200,
          data: mockedAWerteDto,
        })
      );

      const mockedAWerteModel = createAWerte();
      mockDefinitions.mapToModel.mockReturnValue(mockedAWerteModel);

      const result = await unitUnderTest.getAWerte(wahlbezirkId);
      expect(result).toStrictEqual([mockedAWerteModel, mockedAWerteModel]);

      expect(mockDefinitions.getAWerte).toHaveBeenCalledWith(
        wahlbezirkId,
        expect.objectContaining({
          headers: expect.objectContaining({
            [REQUEST_HEADER_OFFLINE_STRATEGY]: FetchStrategiesEnum.STRATEGY_ONLINE_FIRST
          })
        })
      );
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_notSendNotification_when_loadingSucceededAndSendNotificationIsFalse", async () => {
      const wahlbezirkId = generateRandomString(10);

      const mockedAWerteDto = [createAWerteDTO(), createAWerteDTO()];
      mockDefinitions.getAWerte.mockResolvedValue(
        createAxiosResponse({
          status: 200,
          data: mockedAWerteDto,
        })
      );

      const mockedAWerteModel = createAWerte();
      mockDefinitions.mapToModel.mockReturnValue(mockedAWerteModel);

      await unitUnderTest.getAWerte(wahlbezirkId, false);
      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
      expect(mockDefinitions.getAWerte).toHaveBeenCalledWith(
        wahlbezirkId,
        expect.objectContaining({
          headers: expect.objectContaining({
            [REQUEST_HEADER_OFFLINE_STRATEGY]: FetchStrategiesEnum.STRATEGY_ONLINE_FIRST
          })
        })
      );
    });

    it("should_throwErrorAndSendNotification_when_apiCallFailed", async () => {
      const wahlbezirkId = generateRandomString(10);
      const apiCallError = new Error("mocked api call failed");
      mockDefinitions.getAWerte.mockRejectedValue(apiCallError);

      await expect(unitUnderTest.getAWerte(wahlbezirkId)).rejects.toThrow(
        `Get AWerte failed for wahlbezirkId: ${wahlbezirkId}`
      );
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.ERROR],
      ]);
      expect(mockDefinitions.getAWerte).toHaveBeenCalledWith(
        wahlbezirkId,
        expect.objectContaining({
          headers: expect.objectContaining({
            [REQUEST_HEADER_OFFLINE_STRATEGY]: FetchStrategiesEnum.STRATEGY_ONLINE_FIRST
          })
        })
      );
    });

    it("should_throwErrorAndSendNoNotification_when_apiCallFailedAndSendNotificationIsFalse", async () => {
      const wahlbezirkId = generateRandomString(10);
      const apiCallError = new Error("mocked api call failed");
      mockDefinitions.getAWerte.mockRejectedValue(apiCallError);

      await expect(
        unitUnderTest.getAWerte(wahlbezirkId, false)
      ).rejects.toThrow(`Get AWerte failed for wahlbezirkId: ${wahlbezirkId}`);
      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
      expect(mockDefinitions.getAWerte).toHaveBeenCalledWith(
        wahlbezirkId,
        expect.objectContaining({
          headers: expect.objectContaining({
            [REQUEST_HEADER_OFFLINE_STRATEGY]: FetchStrategiesEnum.STRATEGY_ONLINE_FIRST
          })
        })
      );
    });
  });
});