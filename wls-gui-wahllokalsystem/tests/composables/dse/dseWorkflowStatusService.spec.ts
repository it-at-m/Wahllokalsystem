import { useStimmzettelerfassungStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungStatusTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useDseWorkflowStatusService } from "@/composables/dse/dseWorkflowStatusService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  mapDtoToModel: vi.fn(),
  mapModelToDto: vi.fn(),
  addNotification: vi.fn(),
  configurationConstructor: vi.fn(),
  getStimmzettelerfassungStatus: vi.fn(),
  saveStimmzettelerfassungStatus: vi.fn(),
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
        saveStimmzettelerfassungStatus =
          mockDefinitions.saveStimmzettelerfassungStatus;
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
      modelToDto: mockDefinitions.mapModelToDto,
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
  const {
    isWorkflowStatusLoading,
    loadDseWorkflowStatus,
    saveDseWorkflowStatus,
  } = useDseWorkflowStatusService();

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

    it("should_toggleLoadingState_when_called", async () => {
      vi.useFakeTimers();

      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";
      const timeout = 100;

      mockDefinitions.getStimmzettelerfassungStatus.mockReturnValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve({});
          }, timeout);
        })
      );

      expect(isWorkflowStatusLoading.value).toBe(false);
      const promise = loadDseWorkflowStatus(wahlID, wahlbezirkID);
      expect(isWorkflowStatusLoading.value).toBe(true);
      vi.advanceTimersByTime(timeout);
      await promise;
      expect(isWorkflowStatusLoading.value).toBe(false);

      vi.useRealTimers();
    });
  });

  describe("saveDseWorkflowStatus", () => {
    it("should_sendDTO_when_modelIsGiven", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";
      const status = createStimmzettelerfassungStatus();

      const mockedDto = createStimmzettelerfassungStatusDTO();

      mockDefinitions.mapModelToDto.mockReturnValue(mockedDto);

      await saveDseWorkflowStatus(wahlID, wahlbezirkID, status);

      expect(
        mockDefinitions.saveStimmzettelerfassungStatus.mock.calls
      ).toStrictEqual([[wahlID, wahlbezirkID, mockedDto]]);
      expect(mockDefinitions.mapModelToDto.mock.calls[0]).toStrictEqual([
        status,
      ]);
      expect(mockDefinitions.addNotification.mock.calls).toEqual([
        [expect.any(String), UserNotificationCategoryEnum.SUCCESS],
      ]);
    });

    it("should_triggerErrorNotification_when_anExceptionOccurredDuringApiCall", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";
      const status = createStimmzettelerfassungStatus();

      mockDefinitions.saveStimmzettelerfassungStatus.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(async () =>
        saveDseWorkflowStatus(wahlID, wahlbezirkID, status)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        1
      );
      expect(mockDefinitions.addNotification.mock.calls[0]).toEqual([
        "Speichern des Workflow-Status ist fehlgeschlagen",
        UserNotificationCategoryEnum.ERROR,
      ]);
    });

    it("should_notTriggerErrorNotification_when_anExceptionOccurredDuringApiCallButSendNotificationIsFalse", async () => {
      const wahlID = "wahlID";
      const wahlbezirkID = "wahlbezirkID";
      const status = createStimmzettelerfassungStatus();

      mockDefinitions.saveStimmzettelerfassungStatus.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(async () =>
        saveDseWorkflowStatus(wahlID, wahlbezirkID, status, false)
      ).rejects.toThrowError();

      expect(mockDefinitions.addNotification.mock.calls.length).toStrictEqual(
        0
      );
    });
  });
});
