import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";
import { useWorkflowTestDataFactory } from "@tests/utils/navigation/NavigationTestDataFactory.ts";
import { assertThatRequiredRoutesAreReturned } from "@tests/utils/navigation/navigationTestUtils.ts";
import { flushPromises } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useMbwNavigationService } from "@/composables/navigation/mbwNavigationService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";

const { generateRandomString } = useCommonTestDataFactory();
const { prepareElectionWorkflow } = useWorkflowTestDataFactory();
const { prepareBezirkUndWahlID } = useCommonErgebnismeldungTestDataFactory();

vi.mock(import("@/plugins/router.ts"), () => {
  return {};
});

describe("mbwNavigationService.ts", () => {
  const wahlID = generateRandomString(10);
  const wahlbezirkID = generateRandomString(10);

  beforeEach(() => {
    createTestingPinia({
      createSpy: vi.fn,
    });
  });

  describe("navigation", () => {
    it("should_returnEmptyArray_when_noStatusExists", () => {
      const unitUnderTest = useMbwNavigationService(wahlID, wahlbezirkID);

      const navigation = unitUnderTest.navigation;
      expect(navigation.value).toStrictEqual([]);
    });

    it("should_returnNavigation_when_statusExists", () => {
      useWorkflowStore().electionWorkflowsStates = [
        prepareElectionWorkflow()
          .bezirkUndWahlID(
            prepareBezirkUndWahlID()
              .wahlID(wahlID)
              .wahlbezirkID(wahlbezirkID)
              .build()
          )
          .build(),
      ];
      // @ts-expect-error: cannot set readonly
      useUserStore().hasRoleSchriftfuehrung = true;

      const unitUnderTest = useMbwNavigationService(wahlID, wahlbezirkID);

      const navigation = unitUnderTest.navigation;

      assertThatRequiredRoutesAreReturned(
        navigation,
        wahlID,
        wahlbezirkID,
        MbwStepsEnum
      );
    });

    it("should_returnNavigation_when_statusIsSetAfterInit", async () => {
      // @ts-expect-error: cannot set readonly
      useUserStore().hasRoleSchriftfuehrung = true;
      const unitUnderTest = useMbwNavigationService(wahlID, wahlbezirkID);
      const navigation = unitUnderTest.navigation;

      expect(navigation.value.length).toStrictEqual(0);

      useWorkflowStore().electionWorkflowsStates = [
        prepareElectionWorkflow()
          .bezirkUndWahlID(
            prepareBezirkUndWahlID()
              .wahlID(wahlID)
              .wahlbezirkID(wahlbezirkID)
              .build()
          )
          .build(),
      ];
      await flushPromises();

      assertThatRequiredRoutesAreReturned(
        navigation,
        wahlID,
        wahlbezirkID,
        MbwStepsEnum
      );
    });
  });
});
