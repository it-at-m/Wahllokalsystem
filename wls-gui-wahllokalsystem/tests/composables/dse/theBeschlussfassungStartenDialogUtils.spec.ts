import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelerfassungStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungStatusTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { computed } from "vue";

import { useTheBeschlussfassungStartenDialogUtils } from "@/composables/dse/theBeschlussfassungStartenDialog.ts";
import router from "@/plugins/router.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  isLoadingAnzahlStimmzettel: vi.fn(),
  lastLoadedAnzahlStimmzettel: vi.fn(),
  getAnzahlStimmzettel: vi.fn(),
  saveDseWorkflowStatus: vi.fn(),
  routerPush: vi.fn(),
}));

vi.mock(import("@/composables/dse/dseWorkflowStatusService.ts"), () => ({
  useDseWorkflowStatusService: () => ({
    saveDseWorkflowStatus: mockDefinitions.saveDseWorkflowStatus,
    loadDseWorkflowStatus: vi.fn(),
  }),
}));

vi.mock(
  import("@/composables/dse/stimmzettelFetchService.ts"),
  async (importOrginial) => {
    const original = await importOrginial();
    return {
      useStimmzettelFetchService: () => ({
        ...original.useStimmzettelFetchService(),
        loadAnzahlStimmzettel: mockDefinitions.getAnzahlStimmzettel,
        isLoadingAnzahlStimmzettel: computed(() =>
          mockDefinitions.isLoadingAnzahlStimmzettel()
        ),
        lastLoadedAnzahlStimmzettel: computed(() =>
          mockDefinitions.lastLoadedAnzahlStimmzettel()
        ),
      }),
    };
  }
);

router.push = mockDefinitions.routerPush;

describe("theBeschlussfassungStartenDialogUtils.ts", () => {
  const { generateRandomNumber, generateRandomBoolean } =
    useCommonTestDataFactory();
  const { prepareStimmzettelerfassungStatus } =
    useStimmzettelerfassungStatusTestDataFactory();

  let unitUnderTest: ReturnType<
    typeof useTheBeschlussfassungStartenDialogUtils
  >;

  const wahlID = "wahlID";
  const wahlbezirkID = "wahlbezirkID";

  beforeEach(() => {
    unitUnderTest = useTheBeschlussfassungStartenDialogUtils();
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
    vi.useRealTimers();
  });

  describe("isConfirmButtonInLoadingState", () => {
    it("should_returnTrue_when_instantiated", () => {
      expect(unitUnderTest.isConfirmButtonInLoadingState.value).toStrictEqual(
        true
      );
    });

    it("should_returnTrue_when_isLoadingStimmzettel", async () => {
      vi.useFakeTimers();
      const timeout = 1000;

      mockDefinitions.getAnzahlStimmzettel.mockResolvedValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve({});
          }, timeout);
        })
      );

      const promise = unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID);
      expect(unitUnderTest.isConfirmButtonInLoadingState.value).toStrictEqual(
        true
      );

      vi.advanceTimersByTime(timeout);
      await promise;
    });

    it("should_returnFalse_when_loadingWasSuccessful", async () => {
      const anzahlStimmzettel = generateRandomNumber(2);

      mockDefinitions.lastLoadedAnzahlStimmzettel.mockResolvedValue(
        anzahlStimmzettel
      );

      expect(unitUnderTest.isConfirmButtonInLoadingState.value).toStrictEqual(
        false
      );
    });

    it("should_returnTrue_when_loadingFailed", async () => {
      const mockedServiceError = new Error("service call failed");
      mockDefinitions.getAnzahlStimmzettel.mockRejectedValue(
        mockedServiceError
      );

      await expect(
        unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID)
      ).rejects.toThrow(mockedServiceError);

      expect(unitUnderTest.isConfirmButtonInLoadingState.value).toStrictEqual(
        true
      );
    });
  });

  describe("updateWorkflowStatusAndNavigate", () => {
    it("should_updateStateAndNavigate_when_called", async () => {
      await unitUnderTest.updateWorkflowStatusAndNavigate(wahlID, wahlbezirkID);

      expect(mockDefinitions.saveDseWorkflowStatus.mock.calls[0]).toStrictEqual(
        [
          wahlID,
          wahlbezirkID,
          prepareStimmzettelerfassungStatus()
            .status(StimmzettelerfassungStatusEnum.SteAbgeschlossen)
            .build(),
        ]
      );
      expect(mockDefinitions.routerPush.mock.calls.length).toStrictEqual(1);
    });

    it("should_notNavigate_when_updateStateFailed", async () => {
      mockDefinitions.saveDseWorkflowStatus.mockRejectedValue(
        new Error("api call failed")
      );

      await expect(async () =>
        unitUnderTest.updateWorkflowStatusAndNavigate(wahlID, wahlbezirkID)
      ).rejects.toThrowError();

      expect(mockDefinitions.routerPush.mock.calls.length).toStrictEqual(0);
    });
  });

  describe("loadAnzahlStimmzettel", () => {
    it("should_useService_when_called", async () => {
      expect(unitUnderTest.loadAnzahlStimmzettel).toBe(
        mockDefinitions.getAnzahlStimmzettel
      );
    });
  });

  describe("isLoadingAnzahlStimmzettel", () => {
    it("should_useService_when_called", async () => {
      const mockedIsLoadingAnzahlStimmzettel = generateRandomBoolean();
      mockDefinitions.isLoadingAnzahlStimmzettel.mockReturnValue(
        mockedIsLoadingAnzahlStimmzettel
      );
      expect(unitUnderTest.isLoadingAnzahlStimmzettel.value).toBe(
        mockedIsLoadingAnzahlStimmzettel
      );
    });
  });

  describe("lastLoadedAnzahlStimmzettel", () => {
    it("should_useService_when_called", async () => {
      const mockedLastLoadedAnzahlStimmzettel = generateRandomNumber(2);
      mockDefinitions.lastLoadedAnzahlStimmzettel.mockReturnValue(
        mockedLastLoadedAnzahlStimmzettel
      );
      expect(unitUnderTest.lastLoadedAnzahlStimmzettel.value).toBe(
        mockedLastLoadedAnzahlStimmzettel
      );
    });
  });
});
