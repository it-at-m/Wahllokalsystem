import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";

import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelerfassungStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungStatusTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useBeschlussfassungStartenDialogUtils } from "@/composables/dse/beschlussfassung/beschlussfassungStartenDialogUtils.ts";
import router from "@/plugins/router.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getAnzahlStimmzettel: vi.fn(),
  getStimmzettel: vi.fn(),
  loadTeamStatusListe: vi.fn(),
  saveDseWorkflowStatus: vi.fn(),
  routerPush: vi.fn(),
  getNextRoute: vi.fn(),
  routeWithName: vi.fn(),
  routeWithNameAndParams: vi.fn(),
  closeDialog: vi.fn(),
  addNotification: vi.fn(),
}));

vi.mock(import("@/composables/navigation/navigationService.ts"), () => ({
  useNavigationService: () => ({
    getNextRoute: mockDefinitions.getNextRoute,
    routeWithName: mockDefinitions.routeWithName,
    routeWithNameAndParams: mockDefinitions.routeWithNameAndParams,
  }),
}));

vi.mock(
  import("@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusService.ts"),
  () => ({
    useDseWorkflowStatusService: () => ({
      saveDseWorkflowStatus: mockDefinitions.saveDseWorkflowStatus,
      loadDseWorkflowStatus: vi.fn(),
      isWorkflowStatusLoading: ref(false),
    }),
  })
);

vi.mock(
  import("@/composables/dse/stimmzettelerfassung/stimmzettelService.ts"),
  async (importOrginial) => {
    const original = await importOrginial();
    return {
      useStimmzettelService: () => ({
        ...original.useStimmzettelService(),
        getAnzahlStimmzettel: mockDefinitions.getAnzahlStimmzettel,
        getStimmzettel: mockDefinitions.getStimmzettel,
      }),
    };
  }
);

const mockedTeamStatusListRef = ref<StimmzettelerfassungTeamStatusEntry[]>([]);
vi.mock(
  import("@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusListState.ts"),
  () => ({
    useStimmzettelerfassungTeamStatusListState: () => ({
      teamstatusList: mockedTeamStatusListRef,
      loadTeamStatusListe: mockDefinitions.loadTeamStatusListe,
      lastTeamstatusLoadingTime: ref(new Date()),
      isTeamStatusListLoading: ref(true),
    }),
  })
);

vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);

router.push = mockDefinitions.routerPush;

describe("beschlussfassungStartenDialogUtils.ts", () => {
  const { generateRandomNumber } = useCommonTestDataFactory();
  const { prepareStimmzettelerfassungStatus } =
    useStimmzettelerfassungStatusTestDataFactory();

  let unitUnderTest: ReturnType<typeof useBeschlussfassungStartenDialogUtils>;

  const wahlID = "wahlID";
  const wahlbezirkID = "wahlbezirkID";
  const currentUserTeamID = "currentUserTeamID";

  beforeEach(() => {
    createTestingPinia({
      createSpy: vi.fn,
      initialState: {
        user: {
          user: {
            teamName: currentUserTeamID,
          },
        },
      },
    });
    mockedTeamStatusListRef.value = [];
    unitUnderTest = useBeschlussfassungStartenDialogUtils(
      wahlID,
      wahlbezirkID,
      mockDefinitions.closeDialog
    );
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
    vi.useRealTimers();
  });

  describe("isConfirmButtonInLoadingState", () => {
    it("should_returnTrue_when_instantiated", () => {
      expect(unitUnderTest.isConfirmButtonInLoadingState.value).toStrictEqual(
        true
      );
    });

    it("should_returnTrue_when_isLoadingStimmzettel", async () => {
      vi.useFakeTimers();
      const timeout = 1000;

      mockDefinitions.getAnzahlStimmzettel.mockResolvedValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve({});
          }, timeout);
        })
      );

      const promise = unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID);
      expect(unitUnderTest.isConfirmButtonInLoadingState.value).toStrictEqual(
        true
      );

      vi.advanceTimersByTime(timeout);
      await promise;
    });

    it("should_returnFalse_when_loadingWasSuccessful", async () => {
      const anzahlStimmzettel = generateRandomNumber(2);

      mockDefinitions.getAnzahlStimmzettel.mockResolvedValue(anzahlStimmzettel);
      await unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID);

      expect(unitUnderTest.isConfirmButtonInLoadingState.value).toStrictEqual(
        false
      );
    });

    it("should_returnTrue_when_loadingFailed", async () => {
      const mockedServiceError = new Error("service call failed");
      mockDefinitions.getAnzahlStimmzettel.mockRejectedValue(
        mockedServiceError
      );

      await expect(
        unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID)
      ).rejects.toThrow(mockedServiceError);

      expect(unitUnderTest.isConfirmButtonInLoadingState.value).toStrictEqual(
        true
      );
    });
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

  describe("loadAnzahlStimmzettel", () => {
    it("should_loadAnzahlStimmzettelAndUpdateLoadingState_when_called", async () => {
      const anzahlStimmzettel = generateRandomNumber(2);

      mockDefinitions.getAnzahlStimmzettel.mockResolvedValue(anzahlStimmzettel);

      const spyOnIsAnzahlStimmzettelLoading = vi.spyOn(
        unitUnderTest.isAnzahlStimmzettelLoading,
        "value",
        "set"
      );

      expect(unitUnderTest.stimmzettelCount.value).toStrictEqual(null);
      expect(unitUnderTest.isAnzahlStimmzettelLoading.value).toStrictEqual(
        false
      );

      await unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID);

      expect(mockDefinitions.getAnzahlStimmzettel.mock.calls).toStrictEqual([
        [wahlID, wahlbezirkID],
      ]);
      expect(unitUnderTest.stimmzettelCount.value).toStrictEqual(
        anzahlStimmzettel
      );
      expect(unitUnderTest.isAnzahlStimmzettelLoading.value).toStrictEqual(
        false
      );
      expect(spyOnIsAnzahlStimmzettelLoading.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      spyOnIsAnzahlStimmzettelLoading.mockRestore();
    });

    it("should_resetLoadingStateAndKeepStimmzettelCountNull_when_loadingFailed", async () => {
      const anzahlStimmzettel = generateRandomNumber(2);
      mockDefinitions.getAnzahlStimmzettel.mockResolvedValue(anzahlStimmzettel);
      await unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID);
      expect(unitUnderTest.stimmzettelCount.value).toStrictEqual(
        anzahlStimmzettel
      );
      mockDefinitions.getAnzahlStimmzettel.mockClear();

      const mockedServiceError = new Error("api call failed");
      mockDefinitions.getAnzahlStimmzettel.mockRejectedValue(
        mockedServiceError
      );

      await expect(async () =>
        unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID)
      ).rejects.toThrow(mockedServiceError);

      expect(mockDefinitions.getAnzahlStimmzettel.mock.calls).toStrictEqual([
        [wahlID, wahlbezirkID],
      ]);
      expect(unitUnderTest.stimmzettelCount.value).toStrictEqual(null);
      expect(unitUnderTest.isAnzahlStimmzettelLoading.value).toStrictEqual(
        false
      );
    });
  });

  describe("continueProcessAfterForceRefreshOfForeignStimmzettel", () => {
    it("should_refreshForeignTeamsAndContinueProcess_when_refreshWasSuccessful", async () => {
      const foreignTeamID = "foreignTeamID";
      const anotherForeignTeamID = "anotherForeignTeamID";
      mockedTeamStatusListRef.value = [
        {
          teamID: currentUserTeamID,
          status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT,
        },
        {
          teamID: foreignTeamID,
          status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT,
        },
        {
          teamID: anotherForeignTeamID,
          status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT,
        },
      ];
      mockDefinitions.getStimmzettel.mockResolvedValue([]);

      await unitUnderTest.continueProcessAfterForceRefreshOfForeignStimmzettel();

      expect(mockDefinitions.loadTeamStatusListe).toHaveBeenCalledOnce();
      expect(mockDefinitions.getStimmzettel.mock.calls).toStrictEqual([
        [wahlID, wahlbezirkID, foreignTeamID, false, true],
        [wahlID, wahlbezirkID, anotherForeignTeamID, false, true],
      ]);
      expect(mockDefinitions.saveDseWorkflowStatus).toHaveBeenCalledOnce();
      expect(mockDefinitions.routerPush).toHaveBeenCalledOnce();
      expect(mockDefinitions.closeDialog).toHaveBeenCalledOnce();
    });

    it("should_disableConfirmButton_when_refreshIsInProgress", async () => {
      let resolveTeamStatusLoading: () => void = () => undefined;
      mockDefinitions.loadTeamStatusListe.mockReturnValue(
        new Promise<void>((resolve) => {
          resolveTeamStatusLoading = resolve;
        })
      );
      mockDefinitions.getStimmzettel.mockResolvedValue([]);
      mockDefinitions.getAnzahlStimmzettel.mockResolvedValue(
        generateRandomNumber(2)
      );
      await unitUnderTest.loadAnzahlStimmzettel(wahlID, wahlbezirkID);

      const continueProcessPromise =
        unitUnderTest.continueProcessAfterForceRefreshOfForeignStimmzettel();

      expect(unitUnderTest.isConfirmButtonInLoadingState.value).toStrictEqual(
        true
      );

      resolveTeamStatusLoading();
      await continueProcessPromise;

      expect(unitUnderTest.isConfirmButtonInLoadingState.value).toStrictEqual(
        false
      );
    });

    it("should_showErrorAndNotContinueProcess_when_refreshFailed", async () => {
      const foreignTeamID = "foreignTeamID";
      const mockedRefreshError = new Error("refresh failed");
      mockedTeamStatusListRef.value = [
        {
          teamID: foreignTeamID,
          status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT,
        },
      ];
      mockDefinitions.getStimmzettel.mockRejectedValue(mockedRefreshError);

      await expect(
        unitUnderTest.continueProcessAfterForceRefreshOfForeignStimmzettel()
      ).rejects.toThrow(mockedRefreshError);

      expect(mockDefinitions.addNotification).toHaveBeenCalledWith(
        "Stimmzettel konnten nicht synchronisiert werden. Bitte versuchen Sie es später erneut.",
        UserNotificationCategoryEnum.ERROR
      );
      expect(mockDefinitions.saveDseWorkflowStatus).not.toHaveBeenCalled();
      expect(mockDefinitions.routerPush).not.toHaveBeenCalled();
      expect(mockDefinitions.closeDialog).not.toHaveBeenCalled();
    });
  });
});
