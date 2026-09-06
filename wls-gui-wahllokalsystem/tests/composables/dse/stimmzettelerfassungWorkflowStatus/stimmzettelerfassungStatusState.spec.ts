import { useStimmzettelerfassungStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungStatusTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelerfassungStatusState } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusState.ts";

const mockDefinitions = await vi.hoisted(async () => {
  return {
    loadDseWorkflowStatus: vi.fn(),
  };
});

vi.mock(
  "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusService.ts",
  () => ({
    useDseWorkflowStatusService: () => ({
      loadDseWorkflowStatus: mockDefinitions.loadDseWorkflowStatus,
    }),
  })
);

describe("stimmzettelerfassungStatusState.ts", () => {
  const { createStimmzettelerfassungStatus } =
    useStimmzettelerfassungStatusTestDataFactory();

  const wahlID = "WID";
  const wahlbezirkID = "WBZID";

  let unitUnderTest: ReturnType<typeof useStimmzettelerfassungStatusState>;

  beforeEach(() => {
    vi.clearAllMocks();

    unitUnderTest = useStimmzettelerfassungStatusState(wahlID, wahlbezirkID);
  });

  it("should_haveInitialState_when_created", () => {
    expect(unitUnderTest.isWorkflowStatusLoading.value).toBe(false);
    expect(unitUnderTest.workflowStatus.value).toBeNull();
  });

  it("should_setLoadingAndUpdateStatus_when_loadWorkflowStatusSucceeds", async () => {
    const mockedWorkflowStatus = createStimmzettelerfassungStatus();
    mockDefinitions.loadDseWorkflowStatus.mockResolvedValue(
      mockedWorkflowStatus
    );

    const spy = vi.spyOn(unitUnderTest.isWorkflowStatusLoading, "value", "set");

    const loadingPromise = unitUnderTest.loadWorkflowStatus();

    expect(unitUnderTest.isWorkflowStatusLoading.value).toBe(true);

    await loadingPromise;
    expect(mockDefinitions.loadDseWorkflowStatus).toHaveBeenCalledWith(
      wahlID,
      wahlbezirkID,
      true
    );
    expect(unitUnderTest.workflowStatus.value).toStrictEqual(
      mockedWorkflowStatus
    );
    expect(unitUnderTest.isWorkflowStatusLoading.value).toBe(false);
    expect(spy.mock.calls).toStrictEqual([[true], [false]]);
    spy.mockReset();
  });

  it("should_resetLoadingAndRethrow_when_loadWorkflowStatusFails", async () => {
    const error = new Error("boom");
    mockDefinitions.loadDseWorkflowStatus.mockRejectedValue(error);

    await expect(unitUnderTest.loadWorkflowStatus()).rejects.toBe(error);

    expect(unitUnderTest.isWorkflowStatusLoading.value).toBe(false);
    expect(unitUnderTest.workflowStatus.value).toBeNull();
  });
});
