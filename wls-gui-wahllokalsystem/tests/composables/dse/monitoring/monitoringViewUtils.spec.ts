import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useMonitoringViewUtils } from "@/composables/dse/monitoring/monitoringViewUtils.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";

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
    reopenStimmzettelerfassung: vi.fn(),
    setStepDone: vi.fn(),
    getNextRoute: vi.fn(),
    routerPush: vi.fn(),
    currentUserTeamName: { value: "teamID" },
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

vi.mock(
  "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts",
  () => ({
    useStimmzettelerfassungTeamStatusService: () => ({
      reopenStimmzettelerfassung: mockDefinitions.reopenStimmzettelerfassung,
    }),
  })
);

vi.mock("@/stores/workflowStore.ts", () => ({
  useWorkflowStore: () => ({ setStepDone: mockDefinitions.setStepDone }),
}));

vi.mock("@/stores/userStore.ts", () => ({
  useUserStore: () => ({
    currentUserTeamName: mockDefinitions.currentUserTeamName,
  }),
}));

vi.mock("pinia", () => ({
  storeToRefs: <T>(store: T) => store,
}));

vi.mock("@/composables/navigation/navigationService.ts", () => ({
  useNavigationService: () => ({ getNextRoute: mockDefinitions.getNextRoute }),
}));

vi.mock("@/plugins/router.ts", () => ({
  default: { push: mockDefinitions.routerPush },
}));

describe("monitoringViewUtils.ts", () => {
  let unit: ReturnType<typeof useMonitoringViewUtils>;

  beforeEach(() => {
    vi.clearAllMocks();
    mockDefinitions.loadTeamStatusListe.mockResolvedValue([]);
    mockDefinitions.currentUserTeamName.value = "teamID";
    mockDefinitions.getNextRoute.mockReturnValue({ name: "nextRoute" });

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

  describe("reopenStimmzettelerfassung", () => {
    it("should_reopenCurrentTeamAndNavigateToNextRoute_when_currentTeamIsReopened", async () => {
      await unit.reopenStimmzettelerfassung("teamID");

      expect(mockDefinitions.reopenStimmzettelerfassung).toHaveBeenCalledWith(
        "wahlID",
        "wahlbezirkID",
        "teamID",
        true
      );
      expect(mockDefinitions.setStepDone).toHaveBeenCalledWith(
        "wahlID",
        "wahlbezirkID",
        MbwStepsEnum.MBW_DSE_MONITORING_ERFASSUNGSSTATUS,
        false
      );
      expect(mockDefinitions.setStepDone).toHaveBeenCalledWith(
        "wahlID",
        "wahlbezirkID",
        MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG,
        false
      );
      expect(mockDefinitions.setStepDone).toHaveBeenCalledWith(
        "wahlID",
        "wahlbezirkID",
        MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG,
        false
      );
      expect(mockDefinitions.routerPush).toHaveBeenCalledWith({
        name: "nextRoute",
      });
    });

    it("should_reloadTeamStatusListWithoutNavigation_when_anotherTeamIsReopened", async () => {
      await unit.reopenStimmzettelerfassung("anotherTeamID");

      expect(mockDefinitions.reopenStimmzettelerfassung).toHaveBeenCalledWith(
        "wahlID",
        "wahlbezirkID",
        "anotherTeamID",
        true
      );
      expect(mockDefinitions.setStepDone).toHaveBeenCalledTimes(2);
      expect(mockDefinitions.loadTeamStatusListe).toHaveBeenCalledOnce();
      expect(mockDefinitions.routerPush).not.toHaveBeenCalled();
    });
  });
});
