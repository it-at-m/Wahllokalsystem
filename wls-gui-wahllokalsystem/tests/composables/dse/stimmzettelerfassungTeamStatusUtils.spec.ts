import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelerfassungTeamStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungTeamStatusTestDataFactory.ts";
import { setActivePinia, storeToRefs } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelerfassungTeamStatusUtils } from "@/composables/dse/stimmzettelerfassungTeamStatusUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadErfassungTeamStatus: vi.fn(),
  postErfassungTeamStatus: vi.fn(),
  addNotification: vi.fn(),
}));

vi.mock(
  import("@/composables/dse/stimmzettelerfassungTeamStatusService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useStimmzettelerfassungTeamStatusService: () => ({
        ...mod.useStimmzettelerfassungTeamStatusService(),
        loadErfassungTeamStatus: mockDefinitions.loadErfassungTeamStatus,
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

describe("stimmzettelerfassungTeamStatusUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useStimmzettelerfassungTeamStatusUtils>;

  const { generateRandomString } = useCommonTestDataFactory();
  const { createStimmzettelerfassungTeamStatusDTOData } =
    useStimmzettelerfassungTeamStatusTestDataFactory();

  const wahlMedata = {
    wahlbezirkID: generateRandomString(10),
    wahlnummer: generateRandomString(10),
    wahlID: generateRandomString(10),
  };
  const teamName = generateRandomString(1);

  beforeEach(() => {
    setActivePinia(
      createTestingPinia({
        createSpy: vi.fn,
      })
    );
    unitUnderTest = useStimmzettelerfassungTeamStatusUtils();
  });

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("initStimmzettelerfassungTeamStatus", () => {
    it("should_postTeamStatus_when_loadedStatusIsEmpty", async () => {
      const { currentUserWahlMetadata, currentUserTeamName } =
        storeToRefs(useUserStore());
      // @ts-expect-error: cannot set readonly
      currentUserWahlMetadata.value = [wahlMedata];
      // @ts-expect-error: cannot set readonly
      currentUserTeamName.value = teamName;

      mockDefinitions.loadErfassungTeamStatus.mockReturnValue(null);

      await unitUnderTest.initStimmzettelerfassungTeamStatus();

      expect(
        mockDefinitions.postErfassungTeamStatus.mock.calls.length
      ).toStrictEqual(1);
      expect(
        mockDefinitions.postErfassungTeamStatus.mock.calls[0]
      ).toStrictEqual([
        wahlMedata.wahlID,
        wahlMedata.wahlbezirkID,
        teamName,
        { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT },
        false,
      ]);
      expect(mockDefinitions.addNotification).not.toHaveBeenCalled();
    });

    it("should_notPostTeamStatus_when_loadedStatusIsPressent", async () => {
      const { currentUserWahlMetadata, currentUserTeamName } =
        storeToRefs(useUserStore());
      // @ts-expect-error: cannot set readonly
      currentUserWahlMetadata.value = [wahlMedata];
      // @ts-expect-error: cannot set readonly
      currentUserTeamName.value = teamName;

      mockDefinitions.loadErfassungTeamStatus.mockReturnValue(
        createStimmzettelerfassungTeamStatusDTOData()
      );

      await unitUnderTest.initStimmzettelerfassungTeamStatus();

      expect(
        mockDefinitions.postErfassungTeamStatus.mock.calls.length
      ).toStrictEqual(0);
      expect(mockDefinitions.addNotification).not.toHaveBeenCalled();
    });

    it("should_postTeamStatusForEveryWahl_when_loadedStatusIsEmptyforEveryWahl", async () => {
      const { currentUserWahlMetadata, currentUserTeamName } =
        storeToRefs(useUserStore());
      // @ts-expect-error: cannot set readonly
      currentUserWahlMetadata.value = [wahlMedata, wahlMedata];
      // @ts-expect-error: cannot set readonly
      currentUserTeamName.value = teamName;

      mockDefinitions.loadErfassungTeamStatus.mockReturnValue(null);

      await unitUnderTest.initStimmzettelerfassungTeamStatus();

      expect(
        mockDefinitions.postErfassungTeamStatus.mock.calls.length
      ).toStrictEqual(2);
      expect(mockDefinitions.addNotification).not.toHaveBeenCalled();
    });

    it("should_throwErrorAndSendUserNotification_when_loadingOfTeamStatusFailed", async () => {
      const { currentUserWahlMetadata, currentUserTeamName } =
        storeToRefs(useUserStore());
      // @ts-expect-error: cannot set readonly
      currentUserWahlMetadata.value = [wahlMedata];
      // @ts-expect-error: cannot set readonly
      currentUserTeamName.value = teamName;

      const mockedLoadingError = new Error("mocked loading error");
      mockDefinitions.loadErfassungTeamStatus.mockRejectedValue(
        mockedLoadingError
      );

      await expect(
        unitUnderTest.initStimmzettelerfassungTeamStatus()
      ).rejects.toThrowError(mockedLoadingError);
      expect(mockDefinitions.addNotification).toHaveBeenCalledExactlyOnceWith(
        expect.any(String),
        UserNotificationCategoryEnum.ERROR
      );
    });

    it("should_throwErrorAndSendUserNotification_when_sendingOfTeamStatusFailed", async () => {
      const { currentUserWahlMetadata, currentUserTeamName } =
        storeToRefs(useUserStore());
      // @ts-expect-error: cannot set readonly
      currentUserWahlMetadata.value = [wahlMedata];
      // @ts-expect-error: cannot set readonly
      currentUserTeamName.value = teamName;

      mockDefinitions.loadErfassungTeamStatus.mockReturnValue(null);

      const mockedLoadingError = new Error("mocked loading error");
      mockDefinitions.postErfassungTeamStatus.mockRejectedValue(
        mockedLoadingError
      );

      await expect(
        unitUnderTest.initStimmzettelerfassungTeamStatus()
      ).rejects.toThrowError(mockedLoadingError);
      expect(mockDefinitions.addNotification).toHaveBeenCalledExactlyOnceWith(
        expect.any(String),
        UserNotificationCategoryEnum.ERROR
      );
    });
  });
});
