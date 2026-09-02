import { useStimmzettelerfassungStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungStatusTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useMonitoringViewUtils } from "@/composables/dse/monitoring/monitoringViewUtils.ts";

const mockDefinitions = await vi.hoisted(async () => {
  const { ref } = await import("vue");

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
    loadErfassungTeamStatusListe: vi.fn(),
    loadDseWorkflowStatus: vi.fn(),
    // Expose vue.ref for tests if needed
    ref,
  };
});

vi.mock("vue", () => ({
  ref: mockDefinitions.ref,
  onActivated: (cb: () => Promise<void> | void) =>
    mockDefinitions.registerActivated(cb),
}));

vi.mock(
  "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts",
  () => ({
    useStimmzettelerfassungTeamStatusService: () => ({
      loadErfassungTeamStatusListe:
        mockDefinitions.loadErfassungTeamStatusListe,
    }),
  })
);

vi.mock(
  "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusService.ts",
  () => ({
    useDseWorkflowStatusService: () => ({
      loadDseWorkflowStatus: mockDefinitions.loadDseWorkflowStatus,
    }),
  })
);

describe("monitoringViewUtils.ts", () => {
  const { createStimmzettelerfassungStatus } =
    useStimmzettelerfassungStatusTestDataFactory();

  const wahlID = "W1";
  const wahlbezirkID = "WB1";

  let unit: ReturnType<typeof useMonitoringViewUtils>;

  beforeEach(() => {
    vi.clearAllMocks();
    mockDefinitions.loadErfassungTeamStatusListe.mockResolvedValue([]);

    unit = useMonitoringViewUtils(wahlID, wahlbezirkID);
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  it("should_BeEmptyAndNotLoading_when_initialState", () => {
    expect(unit.teamstatusList.value).toEqual([]);
    expect(unit.lastLoading.value).toBeUndefined();
    expect(unit.isAktualisierenLoading.value).toBe(false);
    expect(unit.isWorkflowStatusLoading.value).toBe(false);
    expect(unit.workflowStatus.value).toBe(null);
  });

  it("should_loadListAndUpdateState_when_onMonitoringSynchronisierenClicked", async () => {
    const sample = [{ team: "T1" }];
    mockDefinitions.loadErfassungTeamStatusListe.mockResolvedValue(sample);

    const promise = unit.onMonitoringSynchronisierenClicked();
    expect(unit.isAktualisierenLoading.value).toBe(true);

    await promise;

    expect(unit.isAktualisierenLoading.value).toBe(false);
    expect(unit.teamstatusList.value).toStrictEqual(sample);
    expect(unit.lastLoading.value).toBeInstanceOf(Date);

    expect(mockDefinitions.loadErfassungTeamStatusListe).toHaveBeenCalledWith(
      wahlID,
      wahlbezirkID,
      true
    );
  });

  it("should_updateListAndLastLoading_when_falsyValueWasReturned", async () => {
    mockDefinitions.loadErfassungTeamStatusListe.mockResolvedValue(null);

    await unit.onMonitoringSynchronisierenClicked();

    expect(unit.teamstatusList.value).toEqual([]);
    expect(unit.lastLoading.value).toBeUndefined();
  });

  it("should_loadListAndUpdateState_when_onActivatedSuccess", async () => {
    const sample = [{ team: "T2" }];
    const workflowStatus = createStimmzettelerfassungStatus();
    mockDefinitions.loadErfassungTeamStatusListe.mockResolvedValue(sample);
    mockDefinitions.loadDseWorkflowStatus.mockResolvedValue(workflowStatus);
    const spyOnIsWorkflowStatusLoading = vi.spyOn(
      unit.isWorkflowStatusLoading,
      "value",
      "set"
    );

    expect(unit.isWorkflowStatusLoading.value).toStrictEqual(false);
    await mockDefinitions.runActivatedCallbacks();

    expect(unit.teamstatusList.value).toStrictEqual(sample);
    expect(unit.lastLoading.value).toBeInstanceOf(Date);
    expect(unit.workflowStatus.value).toStrictEqual(workflowStatus);

    expect(mockDefinitions.loadErfassungTeamStatusListe).toHaveBeenCalledWith(
      wahlID,
      wahlbezirkID,
      true
    );
    expect(mockDefinitions.loadDseWorkflowStatus).toHaveBeenCalledWith(
      wahlID,
      wahlbezirkID,
      true
    );
    expect(spyOnIsWorkflowStatusLoading.mock.calls).toStrictEqual([
      [true],
      [false],
    ]);
    spyOnIsWorkflowStatusLoading.mockRestore();
  });
});
