import { createTestingPinia } from "@pinia/testing";
import { useStimmzettelerfassungTeamStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungTeamStatusTestDataFactory.ts";
import { setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelerfassungTeamStatusListState } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusListState.ts";

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

describe("stimmzettelerfassungTeamStatusListState.ts", () => {
  let unitUnderTest: ReturnType<
    typeof useStimmzettelerfassungTeamStatusListState
  >;

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
    unitUnderTest = useStimmzettelerfassungTeamStatusListState(
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
    expect(unitUnderTest.isTeamStatusListLoading.value).toBe(false);
  });

  describe("loadTeamStatusListe", async () => {
    it("should_setLoadingAndUpdateStatus_when_loadWorkflowStatusSucceeds", async () => {
      const mockedListe = createStimmzettelerfassungTeamStatusListe();
      mockDefinitions.loadTeamStatusListe.mockResolvedValue(mockedListe);

      const spy = vi.spyOn(
        unitUnderTest.isTeamStatusListLoading,
        "value",
        "set"
      );

      const loadingPromise = unitUnderTest.loadTeamStatusListe();
      expect(unitUnderTest.isTeamStatusListLoading.value).toBe(true);
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
      expect(unitUnderTest.isTeamStatusListLoading.value).toBe(false);
      expect(spy.mock.calls).toStrictEqual([[true], [false]]);
      spy.mockReset();
    });

    it("should_resetLoadingAndRethrow_when_loadFails", async () => {
      const error = new Error("boom");
      mockDefinitions.loadTeamStatusListe.mockRejectedValue(error);

      await expect(unitUnderTest.loadTeamStatusListe()).rejects.toBe(error);

      expect(unitUnderTest.isTeamStatusListLoading.value).toBe(false);
      expect(unitUnderTest.teamstatusList.value).toStrictEqual([]);
      expect(unitUnderTest.lastTeamstatusLoadingTime.value).toBeUndefined();
    });
  });
});
