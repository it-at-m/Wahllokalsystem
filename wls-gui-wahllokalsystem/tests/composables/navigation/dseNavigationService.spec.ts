import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { assertThatRequiredRoutesAreReturned } from "@tests/utils/navigation/navigationTestUtils.ts";
import { beforeEach, describe, it, vi } from "vitest";

import { useDseNavigationService } from "@/composables/navigation/dseNavigationService.ts";
import { DseStepsEnum } from "@/types/navigation/DseStepsEnum.ts";

const { generateRandomString } = useCommonTestDataFactory();

vi.mock(import("@/plugins/router.ts"), () => {
  return {};
});

describe("dseNavigationService.ts", () => {
  const wahlID = generateRandomString(10);
  const wahlbezirkID = generateRandomString(10);

  beforeEach(() => {
    createTestingPinia({
      createSpy: vi.fn,
    });
  });

  describe("navigation", () => {
    it("should_returnNavigation_when_called", () => {
      const unitUnderTest = useDseNavigationService(wahlID, wahlbezirkID);
      const navigation = unitUnderTest.navigation;

      assertThatRequiredRoutesAreReturned(
        navigation,
        wahlID,
        wahlbezirkID,
        DseStepsEnum
      );
    });
  });
});
