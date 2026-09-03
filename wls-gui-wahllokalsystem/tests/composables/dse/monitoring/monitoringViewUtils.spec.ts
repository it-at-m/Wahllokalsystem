import { useStimmzettelerfassungStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungStatusTestDataFactory.ts";
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
    loadErfassungTeamStatusListe: vi.fn(),
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
  "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts",
  () => ({
    useStimmzettelerfassungTeamStatusService: () => ({
      loadErfassungTeamStatusListe:
        mockDefinitions.loadErfassungTeamStatusListe,
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

  it("should_beEmptyAndNotLoading_when_initialState", () => {
    expect(unit.teamstatusList.value).toEqual([]);
    expect(unit.lastLoading.value).toBeUndefined();
    expect(unit.isAktualisierenLoading.value).toBe(false);
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
    mockDefinitions.loadWorkflowStatus.mockResolvedValue(workflowStatus);

    await mockDefinitions.runActivatedCallbacks();

    expect(unit.teamstatusList.value).toStrictEqual(sample);
    expect(unit.lastLoading.value).toBeInstanceOf(Date);

    expect(mockDefinitions.loadErfassungTeamStatusListe).toHaveBeenCalledWith(
      wahlID,
      wahlbezirkID,
      true
    );
    expect(mockDefinitions.loadWorkflowStatus).toHaveBeenCalled();
  });
});
