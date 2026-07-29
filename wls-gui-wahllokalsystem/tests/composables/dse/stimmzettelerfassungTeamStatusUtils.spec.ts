import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { setActivePinia, storeToRefs } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelerfassungTeamStatusUtils } from "@/composables/dse/stimmzettelerfassungTeamStatusUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadErfassungTeamStatus: vi.fn(),
  postErfassungTeamStatus: vi.fn(),
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

describe("stimmzettelerfassungTeamStatusUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useStimmzettelerfassungTeamStatusUtils>;

  const { generateRandomString } = useCommonTestDataFactory();

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
      ]);
    });

    it("should_notPostTeamStatus_when_loadedStatusIsPressent", async () => {
      const { currentUserWahlMetadata, currentUserTeamName } =
        storeToRefs(useUserStore());
      // @ts-expect-error: cannot set readonly
      currentUserWahlMetadata.value = [wahlMedata];
      // @ts-expect-error: cannot set readonly
      currentUserTeamName.value = teamName;

      mockDefinitions.loadErfassungTeamStatus.mockReturnValue({
        status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT,
      });

      await unitUnderTest.initStimmzettelerfassungTeamStatus();

      expect(
        mockDefinitions.postErfassungTeamStatus.mock.calls.length
      ).toStrictEqual(0);
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
    });
  });
});
