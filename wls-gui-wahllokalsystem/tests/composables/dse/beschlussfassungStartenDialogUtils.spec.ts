import { useStimmzettelerfassungStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungStatusTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useBeschlussfassungStartenDialogUtils } from "@/composables/dse/beschlussfassungStartenDialogUtils.ts";
import router from "@/plugins/router.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  saveDseWorkflowStatus: vi.fn(),
  routerPush: vi.fn(),
}));

vi.mock(import("@/composables/dse/dseWorkflowStatusService.ts"), () => ({
  useDseWorkflowStatusService: () => ({
    saveDseWorkflowStatus: mockDefinitions.saveDseWorkflowStatus,
    loadDseWorkflowStatus: vi.fn(),
  }),
}));

router.push = mockDefinitions.routerPush;

describe("beschlussfassungStartenDialogUtils.ts", () => {
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
});
