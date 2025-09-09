import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlscheineTestDataFactory } from "@tests/utils/ergebnismeldung/wahlscheineTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlscheineService } from "@/composables/ergebnismeldung/wahlscheineService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { createWahlscheine } = useWahlscheineTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();
const mockDefinitions = vi.hoisted(() => ({
  getWahlscheine: vi.fn(),
  configurationConstructor: vi.fn(),
  addNotification: vi.fn(),
  mapDtoToModel: vi.fn(),
}));

vi.mock("@/api/wls-clients/generated-ergebnismeldung-api", () => ({
  WahlscheineControllerApi: vi.fn().mockImplementation(() => ({
    getWahlscheine: mockDefinitions.getWahlscheine,
  })),
  Configuration: mockDefinitions.configurationConstructor,
}));

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

vi.mock("@/composables/ergebnismeldung/wahlscheineMapper.ts", () => ({
  useWahlscheineMapper: () => ({
    toModel: mockDefinitions.mapDtoToModel,
  }),
}));

describe("ergebnismeldungService.ts", () => {
  const { getWahlscheine } = useWahlscheineService();
  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getWahlscheine", () => {
    it("should_returnWahlscheine_when_calledWithValidParameters", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const mockedWahlscheine = createWahlscheine();
      mockDefinitions.getWahlscheine.mockReturnValue(
        Promise.resolve({
          status: 200,
          data: createWahlscheine(),
        })
      );
      mockDefinitions.mapDtoToModel.mockReturnValue(mockedWahlscheine);

      const result = await getWahlscheine(wahlID, wahlbezirkID);

      expect(result).toEqual(mockedWahlscheine);
    });

    it("should_triggerNotification_when_anExceptionOccurredDuringApiCall", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const mockedWahlscheine = createWahlscheine();
      mockDefinitions.getWahlscheine.mockRejectedValue(
        new Error("api called failed")
      );

      mockDefinitions.mapDtoToModel.mockReturnValue(mockedWahlscheine);

      await expect(async () =>
        getWahlscheine(wahlID, wahlbezirkID)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        UserNotificationCategoryEnum.ERROR,
      ]);
    });

    it("should_notTriggerNotification_when_anExceptionOccurredDuringApiCallAndNotificationIsDisabled", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      mockDefinitions.getWahlscheine.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(async () =>
        getWahlscheine(wahlID, wahlbezirkID, false)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });
});
