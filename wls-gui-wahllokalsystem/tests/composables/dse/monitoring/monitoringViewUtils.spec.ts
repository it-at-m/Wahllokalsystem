import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useMonitoringViewUtils } from "@/composables/dse/monitoring/monitoringViewUtils.ts";

const mockDefinitions = await vi.hoisted(async () => {
  const activatedCallbacks: (() => Promise<void> | void)[] = [];

  return {
    registerActivated: (cb: () => Promise<void> | void) =>
      activatedCallbacks.push(cb),
    runActivatedCallbacks: async () => {
      const cbs = activatedCallbacks.splice(0, activatedCallbacks.length);
      for (const cb of cbs) {
        await cb();
      }
    },
    loadTeamStatusListe: vi.fn(),
    loadWorkflowStatus: vi.fn(),
  };
});

vi.mock("vue", async (importOriginal) => {
  const actual = (await importOriginal()) as object;
  return {
    ...actual,
    onActivated: (cb: () => Promise<void> | void) =>
      mockDefinitions.registerActivated(cb),
  };
});

vi.mock(
  "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusListState.ts",
  () => ({
    useStimmzettelerfassungTeamStatusListState: () => ({
      loadTeamStatusListe: mockDefinitions.loadTeamStatusListe,
    }),
  })
);

vi.mock(
  "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusState.ts",
  () => ({
    useStimmzettelerfassungStatusState: () => ({
      loadWorkflowStatus: mockDefinitions.loadWorkflowStatus,
    }),
  })
);

describe("monitoringViewUtils.ts", () => {
  let unit: ReturnType<typeof useMonitoringViewUtils>;

  beforeEach(() => {
    vi.clearAllMocks();
    mockDefinitions.loadTeamStatusListe.mockResolvedValue([]);

    unit = useMonitoringViewUtils("wahlID", "wahlbezirkID");
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  describe("onMonitoringSynchronisierenClicked", async () => {
    it("should_loadTeamStatusListe_when_onMonitoringSynchronisierenClicked", async () => {
      await unit.onMonitoringSynchronisierenClicked();
      expect(mockDefinitions.loadTeamStatusListe).toHaveBeenCalled();
    });
  });

  describe("onActivated", async () => {
    it("should_loadTeamStatusListeAndWorkflowStatus_when_onActivatedSuccess", async () => {
      await mockDefinitions.runActivatedCallbacks();

      expect(mockDefinitions.loadTeamStatusListe).toHaveBeenCalled();
      expect(mockDefinitions.loadWorkflowStatus).toHaveBeenCalled();
    });
  });
});
