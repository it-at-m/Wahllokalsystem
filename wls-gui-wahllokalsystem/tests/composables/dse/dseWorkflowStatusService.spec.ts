import { useStimmzettelerfassungStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungStatusTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useDseWorkflowStatusService } from "@/composables/dse/dseWorkflowStatusService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  mapDtoToModel: vi.fn(),
  addNotification: vi.fn(),
  configurationConstructor: vi.fn(),
  getStimmzettelerfassungStatus: vi.fn(),
}));

vi.mock(
  "@/api/wls-clients/generated-ergebnismeldung-api",
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      ...(mod as object),
      StimmzettelerfassungControllerApi: class {
        getStimmzettelerfassungStatus =
          mockDefinitions.getStimmzettelerfassungStatus;
      },
      Configuration: vi.fn(),
    };
  }
);
vi.mock(
  import("@/composables/dse/stimmzettelerfassungStatusMapper.ts"),
  () => ({
    useStimmzettelerfassungStatusMapper: () => ({
      dtoToModel: mockDefinitions.mapDtoToModel,
    }),
  })
);
vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);

const {
  createStimmzettelerfassungStatusDTO,
  createStimmzettelerfassungStatus,
} = useStimmzettelerfassungStatusTestDataFactory();

describe("DseWorkflowStatusService.ts", () => {
  const { loadDseWorkflowStatus } = useDseWorkflowStatusService();

  beforeEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("loadDseWorkflowStatus", () => {
    it("should_returnStimmzettelerfassungStatus_when_responseIsReceivedFromApi", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";

      mockDefinitions.getStimmzettelerfassungStatus.mockReturnValue(
        Promise.resolve({
          status: 200,
          data: createStimmzettelerfassungStatusDTO(),
        })
      );

      const mockedResult = createStimmzettelerfassungStatus();
      mockDefinitions.mapDtoToModel.mockReturnValue(mockedResult);

      const result = await loadDseWorkflowStatus(wahlID, wahlbezirkID);

      expect(result).toStrictEqual(mockedResult);

      expect(
        mockDefinitions.getStimmzettelerfassungStatus.mock.calls.length
      ).toStrictEqual(1);
      expect(
        mockDefinitions.getStimmzettelerfassungStatus.mock.calls
      ).toStrictEqual([[wahlID, wahlbezirkID]]);
    });

    it("should_returnNull_when_apiReturned204", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";

      mockDefinitions.getStimmzettelerfassungStatus.mockReturnValue(
        Promise.resolve({ status: 204, data: null })
      );

      const result = await loadDseWorkflowStatus(wahlID, wahlbezirkID);

      expect(result).toBeNull();
    });

    it("should_triggerErrorNotification_when_anExceptionOccurredDuringApiCall", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";

      mockDefinitions.getStimmzettelerfassungStatus.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(async () =>
        loadDseWorkflowStatus(wahlID, wahlbezirkID)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        1
      );
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        expect.any(String),
        UserNotificationCategoryEnum.ERROR,
      ]);
    });

    it("should_notTriggerErrorNotification_when_anExceptionOccurredDuringApiCallButSendNotificationIsFalse", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";

      mockDefinitions.getStimmzettelerfassungStatus.mockRejectedValue(
        new Error("api called failed")
      );

      await expect(async () =>
        loadDseWorkflowStatus(wahlID, wahlbezirkID, false)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });
});
