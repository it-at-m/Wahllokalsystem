import { useStatusTestDataFactory } from "@tests/utils/ergebnismeldung/statusTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useStatusService } from "@/composables/ergebnismeldung/statusService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { createStatusDTO, createStatus } = useStatusTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  getStatus: vi.fn(),
  configurationConstructor: vi.fn(),
  addNotification: vi.fn(),
  mapDtoToModel: vi.fn(),
}));

vi.mock(
  "@/api/wls-clients/generated-ergebnismeldung-api",
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      ...(mod as object),
      StatusControllerApi: vi.fn().mockImplementation(() => ({
        getStatus: mockDefinitions.getStatus,
      })),
      Configuration: mockDefinitions.configurationConstructor,
    };
  }
);

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

vi.mock("@/composables/ergebnismeldung/statusMapper.ts", () => ({
  useStatusMapper: () => ({
    toModel: mockDefinitions.mapDtoToModel,
  }),
}));

describe("statusService.ts", () => {
  const { getStatus } = useStatusService();

  const wahlID = "wahlID";
  const wahlbezirkID = "wahlbezirkID";

  beforeEach(() => {
    setActivePinia(createPinia());
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getStatus", () => {
    it("should_returnStatus_when_calledWithValidParameters", async () => {
      mockDefinitions.getStatus.mockReturnValue(
        Promise.resolve({
          status: 200,
          data: createStatusDTO(),
        })
      );

      const expectedResultModel = createStatus();
      mockDefinitions.mapDtoToModel.mockReturnValue(expectedResultModel);

      const result = await getStatus(wahlID, wahlbezirkID);
      expect(result).toEqual(expectedResultModel);
    });

    it("should_triggerNotification_when_anExceptionOccurredDuringApiCall", async () => {
      mockDefinitions.getStatus.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(async () =>
        getStatus(wahlID, wahlbezirkID)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        UserNotificationCategoryEnum.ERROR,
      ]);
    });

    it("should_notTriggerNotification_when_anExceptionOccurredDuringApiCall", async () => {
      mockDefinitions.getStatus.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(async () =>
        getStatus(wahlID, wahlbezirkID, false)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });
});
