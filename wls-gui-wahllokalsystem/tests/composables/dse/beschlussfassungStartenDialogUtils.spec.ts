import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelerfassungStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungStatusTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useBeschlussfassungStartenDialogUtils } from "@/composables/dse/beschlussfassungStartenDialogUtils.ts";
import router from "@/plugins/router.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
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
  import("@/composables/dse/stimmzettelService.ts"),
  async (importOrginial) => {
    const original = await importOrginial();
    return {
      useStimmzettelService: () => ({
        ...original.useStimmzettelService(),
        getAnzahlStimmzettel: mockDefinitions.getAnzahlStimmzettel,
      }),
    };
  }
);

router.push = mockDefinitions.routerPush;

describe("beschlussfassungStartenDialogUtils.ts", () => {
  const { generateRandomNumber } = useCommonTestDataFactory();
  const { prepareStimmzettelerfassungStatus } =
    useStimmzettelerfassungStatusTestDataFactory();

  let unitUnderTest: ReturnType<typeof useBeschlussfassungStartenDialogUtils>;

  const wahlID = "wahlID";
  const wahlbezirkID = "wahlbezirkID";

  beforeEach(() => {
    unitUnderTest = useBeschlussfassungStartenDialogUtils();
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

      mockDefinitions.getAnzahlStimmzettel.mockResolvedValue(anzahlStimmzettel);
      await unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID);

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
    it("should_loadAnzahlStimmzettelAndUpdateLoadingState_when_called", async () => {
      const anzahlStimmzettel = generateRandomNumber(2);

      mockDefinitions.getAnzahlStimmzettel.mockResolvedValue(anzahlStimmzettel);

      const spyOnIsAnzahlStimmzettelLoading = vi.spyOn(
        unitUnderTest.isAnzahlStimmzettelLoading,
        "value",
        "set"
      );

      expect(unitUnderTest.stimmzettelCount.value).toStrictEqual(null);
      expect(unitUnderTest.isAnzahlStimmzettelLoading.value).toStrictEqual(
        false
      );

      await unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID);

      expect(mockDefinitions.getAnzahlStimmzettel.mock.calls).toStrictEqual([
        [wahlID, wahlbezirkID],
      ]);
      expect(unitUnderTest.stimmzettelCount.value).toStrictEqual(
        anzahlStimmzettel
      );
      expect(unitUnderTest.isAnzahlStimmzettelLoading.value).toStrictEqual(
        false
      );
      expect(spyOnIsAnzahlStimmzettelLoading.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      spyOnIsAnzahlStimmzettelLoading.mockRestore();
    });

    it("should_resetLoadingStateAndKeepStimmzettelCountNull_when_loadingFailed", async () => {
      const anzahlStimmzettel = generateRandomNumber(2);
      mockDefinitions.getAnzahlStimmzettel.mockResolvedValue(anzahlStimmzettel);
      await unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID);
      expect(unitUnderTest.stimmzettelCount.value).toStrictEqual(
        anzahlStimmzettel
      );
      mockDefinitions.getAnzahlStimmzettel.mockClear();

      const mockedServiceError = new Error("api call failed");
      mockDefinitions.getAnzahlStimmzettel.mockRejectedValue(
        mockedServiceError
      );

      await expect(async () =>
        unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID)
      ).rejects.toThrow(mockedServiceError);

      expect(mockDefinitions.getAnzahlStimmzettel.mock.calls).toStrictEqual([
        [wahlID, wahlbezirkID],
      ]);
      expect(unitUnderTest.stimmzettelCount.value).toStrictEqual(null);
      expect(unitUnderTest.isAnzahlStimmzettelLoading.value).toStrictEqual(
        false
      );
    });
  });
});
