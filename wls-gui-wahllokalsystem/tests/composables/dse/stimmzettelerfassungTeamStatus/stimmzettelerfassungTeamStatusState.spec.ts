import { createTestingPinia } from "@pinia/testing";
import { useStimmzettelerfassungTeamStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungTeamStatusTestDataFactory.ts";
import { setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelerfassungTeamStatusState } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusState.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadErfassungTeamStatus: vi.fn(),
  loadTeamStatusListe: vi.fn(),
  postErfassungTeamStatus: vi.fn(),
  addNotification: vi.fn(),
}));

vi.mock(
  import("@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useStimmzettelerfassungTeamStatusService: () => ({
        ...mod.useStimmzettelerfassungTeamStatusService(),
        loadErfassungTeamStatus: mockDefinitions.loadErfassungTeamStatus,
        loadErfassungTeamStatusListe: mockDefinitions.loadTeamStatusListe,
        postErfassungTeamStatus: mockDefinitions.postErfassungTeamStatus,
      }),
    };
  }
);

vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useUserNotificationService: () => ({
        ...mod.useUserNotificationService(),
        addNotification: mockDefinitions.addNotification,
      }),
    };
  }
);

describe("stimmzettelerfassungTeamStatusState.ts", () => {
  let unitUnderTest: ReturnType<typeof useStimmzettelerfassungTeamStatusState>;

  const { createStimmzettelerfassungTeamStatusListe } =
    useStimmzettelerfassungTeamStatusTestDataFactory();

  const wahlID = "WID";
  const wahlbezirkID = "WBZID";

  beforeEach(() => {
    setActivePinia(
      createTestingPinia({
        createSpy: vi.fn,
      })
    );
    unitUnderTest = useStimmzettelerfassungTeamStatusState(
      wahlID,
      wahlbezirkID
    );
  });

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  it("should_haveInitialState_when_created", () => {
    expect(unitUnderTest.teamstatusList.value).toEqual([]);
    expect(unitUnderTest.lastTeamstatusLoadingTime.value).toBeUndefined();
    expect(unitUnderTest.isTeamStatusLoading.value).toBe(false);
  });

  describe("loadTeamStatusListe", async () => {
    it("should_setLoadingAndUpdateStatus_when_loadWorkflowStatusSucceeds", async () => {
      const mockedListe = createStimmzettelerfassungTeamStatusListe();
      mockDefinitions.loadTeamStatusListe.mockResolvedValue(mockedListe);

      const spy = vi.spyOn(unitUnderTest.isTeamStatusLoading, "value", "set");

      const loadingPromise = unitUnderTest.loadTeamStatusListe();
      expect(unitUnderTest.isTeamStatusLoading.value).toBe(true);
      await loadingPromise;

      expect(mockDefinitions.loadTeamStatusListe).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        true
      );
      expect(unitUnderTest.teamstatusList.value).toEqual(mockedListe);
      expect(unitUnderTest.lastTeamstatusLoadingTime.value).toBeInstanceOf(
        Date
      );
      expect(unitUnderTest.isTeamStatusLoading.value).toBe(false);
      expect(spy.mock.calls).toStrictEqual([[true], [false]]);
      spy.mockReset();
    });

    it("should_resetLoadingAndRethrow_when_loadFails", async () => {
      const error = new Error("boom");
      mockDefinitions.loadTeamStatusListe.mockRejectedValue(error);

      await expect(unitUnderTest.loadTeamStatusListe()).rejects.toBe(error);

      expect(unitUnderTest.isTeamStatusLoading.value).toBe(false);
      expect(unitUnderTest.teamstatusList.value).toStrictEqual([]);
      expect(unitUnderTest.lastTeamstatusLoadingTime.value).toBeUndefined();
    });
  });
});
