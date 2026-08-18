import { useStimmzettelerfassungStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungStatusTestDataFactory.ts";
import { mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { defineComponent } from "vue";

import { useDseWorkflowStatusUtils } from "@/composables/dse/dseWorkflowStatusUtils.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadDseWorkflowStatus: vi.fn(),
}));

vi.mock("@/composables/dse/dseWorkflowStatusService.ts", () => ({
  useDseWorkflowStatusService: () => ({
    loadDseWorkflowStatus: mockDefinitions.loadDseWorkflowStatus,
  }),
}));

describe("dseWorkflowStatusUtils.ts", () => {
  const wahlID = "W1";
  const wahlbezirkID = "WB1";
  const { createStimmzettelerfassungStatus } =
    useStimmzettelerfassungStatusTestDataFactory();

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("reloadWorkflowStatus", () => {
    let unitUnderTest: ReturnType<typeof useDseWorkflowStatusUtils>;
    const timeout = 100;

    beforeEach(() => {
      vi.useFakeTimers();

      // needed, because onActivated hook inside unitUnderTest needs active component instance
      const TestComponent = defineComponent({
        setup() {
          unitUnderTest = useDseWorkflowStatusUtils(wahlID, wahlbezirkID);
          return {};
        },
        template: "<div />",
      });
      mount(TestComponent);
    });

    it("should_toggleLoadingState_when_called", async () => {
      mockDefinitions.loadDseWorkflowStatus.mockReturnValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve({});
          }, timeout);
        })
      );

      expect(unitUnderTest.isWorkflowStatusLoading.value).toBe(false);
      const promise = unitUnderTest.reloadWorkflowStatus();
      expect(unitUnderTest.isWorkflowStatusLoading.value).toBe(true);
      vi.advanceTimersByTime(timeout);
      await promise;
      expect(unitUnderTest.isWorkflowStatusLoading.value).toBe(false);
    });

    it("should_setWorkflowStatus_when_serviceReturnsStatus", async () => {
      const workflowStatus = createStimmzettelerfassungStatus();
      mockDefinitions.loadDseWorkflowStatus.mockResolvedValue(workflowStatus);

      await unitUnderTest.reloadWorkflowStatus();
      expect(unitUnderTest.workflowStatus.value).toStrictEqual(workflowStatus);
      expect(mockDefinitions.loadDseWorkflowStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        false
      );
    });
  });
});
