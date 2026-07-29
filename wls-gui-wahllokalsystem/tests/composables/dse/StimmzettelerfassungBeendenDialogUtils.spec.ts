import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelerfassungBeendenDialogUtils } from "@/composables/dse/StimmzettelerfassungBeendenDialogUtils.ts";
import { ROUTE_FINISHED } from "@/constants.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";
import { DseStepsEnum } from "@/types/navigation/DseStepsEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = await vi.hoisted(async () => {
  const { ref } = await import("vue");

  return {
    synchronizeOfflineData: vi.fn(),
    addNotification: vi.fn(),
    postErfassungTeamStatus: vi.fn(),
    routerPush: vi.fn(),
    hasRoleErfassungsteamValue: ref(true),
    currentUserTeamNameValue: ref("TEAM-1"),
    closeDialogCallback: vi.fn().mockImplementation(() => {
      return;
    }),
    isSavingRef: ref(false),
  };
});

vi.mock("@/composables/userNotification/userNotificationService.ts", () => ({
  useUserNotificationService: () => ({
    addNotification: mockDefinitions.addNotification,
  }),
}));

vi.mock("@/stores/dataSyncStore.ts", () => ({
  useDataSyncStore: () => ({
    synchronizeOfflineData: mockDefinitions.synchronizeOfflineData,
  }),
}));

vi.mock("@/stores/userStore.ts", () => ({
  useUserStore: () => ({
    hasRoleErfassungsteam: mockDefinitions.hasRoleErfassungsteamValue,
    currentUserTeamName: mockDefinitions.currentUserTeamNameValue,
  }),
}));

vi.mock("@/composables/dse/stimmzettelerfassungTeamStatusService.ts", () => ({
  useStimmzettelerfassungStatusTeamService: () => ({
    isSaving: mockDefinitions.isSavingRef,
    postErfassungTeamStatus: mockDefinitions.postErfassungTeamStatus,
  }),
}));

vi.mock("@/plugins/router.ts", () => ({
  default: {
    push: mockDefinitions.routerPush,
  },
}));

const { generateRandomString } = useCommonTestDataFactory();

describe("StimmzettelerfassungBeendenDialogUtils.ts", () => {
  const wahlId = generateRandomString(10);
  const wahlbezirkId = generateRandomString(10);

  let unitUnderTest: ReturnType<
    typeof useStimmzettelerfassungBeendenDialogUtils
  >;

  beforeEach(() => {
    unitUnderTest = useStimmzettelerfassungBeendenDialogUtils(
      wahlId,
      wahlbezirkId,
      mockDefinitions.closeDialogCallback
    );

    mockDefinitions.hasRoleErfassungsteamValue.value = true;
    mockDefinitions.currentUserTeamNameValue.value = "TEAM-1";
    mockDefinitions.isSavingRef.value = false;
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("isSyncWidgetVisible", () => {
    it("should_beFalse_when_unitIsInitialized", () => {
      expect(unitUnderTest.isSyncWidgetVisible.value).toBe(false);
    });

    it("should_beTrue_when_synchronizeDataAndPostTeamErfassungDoneWasCalled", async () => {
      mockDefinitions.synchronizeOfflineData.mockResolvedValue(null);

      expect(unitUnderTest.isSyncWidgetVisible.value).toBe(false);

      await unitUnderTest.synchronizeDataAndPostTeamErfassungDone();

      expect(unitUnderTest.isSyncWidgetVisible.value).toBe(true);
    });
  });

  describe("isSaving", () => {
    it("should_exposeIsSaving_when_unitIsInitialized", () => {
      expect(unitUnderTest.isSaving).toBe(mockDefinitions.isSavingRef);

      mockDefinitions.isSavingRef.value = true;
      expect(unitUnderTest.isSaving.value).toBe(true);
    });
  });

  describe("synchronizeDataAndPostTeamErfassungDone", () => {
    it("should_showWarningNotification_when_synchronizeOfflineDataReturnsNull", async () => {
      mockDefinitions.synchronizeOfflineData.mockResolvedValue(null);

      await unitUnderTest.synchronizeDataAndPostTeamErfassungDone();

      expect(mockDefinitions.synchronizeOfflineData).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.addNotification).toHaveBeenCalledWith(
        expect.any(String),
        UserNotificationCategoryEnum.WARNING
      );
      expect(mockDefinitions.postErfassungTeamStatus).not.toHaveBeenCalled();
      expect(mockDefinitions.routerPush).not.toHaveBeenCalled();
      expect(mockDefinitions.closeDialogCallback).not.toHaveBeenCalled();
    });

    it("should_showErrorNotification_when_synchronizeOfflineDataReturnsDirtyTasks", async () => {
      mockDefinitions.synchronizeOfflineData.mockResolvedValue({
        numberOfDirtyTasksRemaining: 1,
      });

      await unitUnderTest.synchronizeDataAndPostTeamErfassungDone();

      expect(mockDefinitions.addNotification).toHaveBeenCalledWith(
        expect.any(String),
        UserNotificationCategoryEnum.ERROR
      );
      expect(mockDefinitions.postErfassungTeamStatus).not.toHaveBeenCalled();
      expect(mockDefinitions.routerPush).not.toHaveBeenCalled();
      expect(mockDefinitions.closeDialogCallback).not.toHaveBeenCalled();
    });

    it("should_postTeamStatusAndNavigateToFinishedRoute_when_syncSuccessfulAndUserHasRoleErfassungsteam", async () => {
      mockDefinitions.hasRoleErfassungsteamValue.value = true;
      mockDefinitions.currentUserTeamNameValue.value = "TEAM-ERFASSUNG";
      mockDefinitions.synchronizeOfflineData.mockResolvedValue({
        numberOfDirtyTasksRemaining: 0,
      });
      mockDefinitions.postErfassungTeamStatus.mockResolvedValue(undefined);

      await unitUnderTest.synchronizeDataAndPostTeamErfassungDone();

      expect(mockDefinitions.postErfassungTeamStatus).toHaveBeenCalledWith(
        wahlId,
        wahlbezirkId,
        "TEAM-ERFASSUNG",
        { status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN },
        true
      );
      expect(mockDefinitions.routerPush).toHaveBeenCalledWith({
        name: ROUTE_FINISHED,
      });
      expect(mockDefinitions.closeDialogCallback).toHaveBeenCalledTimes(1);
    });

    it("should_postTeamStatusAndNavigateToMonitoring_when_syncSuccessfulAndUserHasNotRoleErfassungsteam", async () => {
      mockDefinitions.hasRoleErfassungsteamValue.value = false;
      mockDefinitions.currentUserTeamNameValue.value = "TEAM-SONSTIG";
      mockDefinitions.synchronizeOfflineData.mockResolvedValue({
        numberOfDirtyTasksRemaining: 0,
      });
      mockDefinitions.postErfassungTeamStatus.mockResolvedValue(undefined);

      await unitUnderTest.synchronizeDataAndPostTeamErfassungDone();

      expect(mockDefinitions.postErfassungTeamStatus).toHaveBeenCalled();
      expect(mockDefinitions.routerPush).toHaveBeenCalledWith({
        name: DseStepsEnum.DSE_MONITORING,
        params: { wahlId, wahlbezirkId },
      });
      expect(mockDefinitions.closeDialogCallback).toHaveBeenCalledTimes(1);
    });
  });
});
