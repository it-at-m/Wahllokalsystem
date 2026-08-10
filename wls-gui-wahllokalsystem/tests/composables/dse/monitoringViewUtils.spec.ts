import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useMonitoringViewUtils } from "@/composables/dse/monitoringViewUtils.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

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
    addNotification: vi.fn(),
    // Expose vue.ref for tests if needed
    ref,
  };
});

vi.mock("vue", () => ({
  ref: mockDefinitions.ref,
  onActivated: (cb: () => Promise<void> | void) =>
    mockDefinitions.registerActivated(cb),
}));

vi.mock("@/composables/dse/stimmzettelerfassungTeamStatusService.ts", () => ({
  useStimmzettelerfassungTeamStatusService: () => ({
    loadErfassungTeamStatusListe: mockDefinitions.loadErfassungTeamStatusListe,
  }),
}));

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

describe("monitoringViewUtils.ts", () => {
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

  it("initial state should be empty and not loading", () => {
    expect(unit.teamstatusList.value).toEqual([]);
    expect(unit.lastLoading.value).toBeUndefined();
    expect(unit.isAktualisiserenLoading.value).toBe(false);
  });

  it("onMonitoringSynchronisierenClicked should load list and update state", async () => {
    const sample = [{ team: "T1" }];
    mockDefinitions.loadErfassungTeamStatusListe.mockResolvedValue(sample);

    const promise = unit.onMonitoringSynchronisierenClicked();
    expect(unit.isAktualisiserenLoading.value).toBe(true);

    await promise;

    expect(unit.isAktualisiserenLoading.value).toBe(false);
    expect(unit.teamstatusList.value).toStrictEqual(sample);
    expect(unit.lastLoading.value).toBeInstanceOf(Date);

    expect(mockDefinitions.loadErfassungTeamStatusListe).toHaveBeenCalledWith(
      wahlID,
      wahlbezirkID,
      true
    );
  });

  it("should not update list or lastLoading when service returns falsy value", async () => {
    mockDefinitions.loadErfassungTeamStatusListe.mockResolvedValue(null);

    await unit.onMonitoringSynchronisierenClicked();

    expect(unit.teamstatusList.value).toEqual([]);
    expect(unit.lastLoading.value).toBeUndefined();
  });

  it("onActivated should load list and update state on success without notification", async () => {
    const sample = [{ team: "T2" }];
    mockDefinitions.loadErfassungTeamStatusListe.mockResolvedValue(sample);

    await mockDefinitions.runActivatedCallbacks();

    expect(mockDefinitions.addNotification).not.toHaveBeenCalled();
    expect(unit.teamstatusList.value).toStrictEqual(sample);
    expect(unit.lastLoading.value).toBeInstanceOf(Date);

    expect(mockDefinitions.loadErfassungTeamStatusListe).toHaveBeenCalledWith(
      wahlID,
      wahlbezirkID,
      false
    );
  });

  it("onActivated should call loadTeamStatusListe with sendNotification=false and show error on failure", async () => {
    mockDefinitions.loadErfassungTeamStatusListe.mockRejectedValue(
      new Error("fail")
    );

    await mockDefinitions.runActivatedCallbacks();

    expect(mockDefinitions.addNotification).toHaveBeenCalledWith(
      "Team-Status konnten nicht initialisiert werden.",
      UserNotificationCategoryEnum.ERROR
    );

    expect(mockDefinitions.loadErfassungTeamStatusListe).toHaveBeenCalledWith(
      wahlID,
      wahlbezirkID,
      false
    );
  });
});
