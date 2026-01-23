import type {
  NavigationGuardNext,
  RouteLocationNormalized,
  RouteLocationNormalizedLoaded,
} from "vue-router";

import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";
import { useStatusTestDataFactory } from "@tests/utils/ergebnismeldung/common/statusTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useNavigationGuards } from "@/composables/navigation/navigationGuards.ts";
import { useStatusStore } from "@/stores/statusStore.ts";

const { generateRandomString } = useCommonTestDataFactory();
const { prepareBezirkUndWahlID } = useCommonErgebnismeldungTestDataFactory();
const { prepareStatus } = useStatusTestDataFactory();

describe("navigationGuards.ts", () => {
  const DUMMY_FROM = {} as unknown as RouteLocationNormalizedLoaded;
  const DUMMY_NEXT_GUARD = {} as unknown as NavigationGuardNext;

  beforeEach(() => {
    createTestingPinia({
      createSpy: vi.fn,
      stubActions: false,
    });
  });

  const { isStepDoneInElectionState } = useNavigationGuards();

  describe("isStepDoneInElectionState", () => {
    it.each([undefined, ["stringArray"]])(
      "should_returnFalse_when_parameterWahlIdIsMissing",
      (wahlIdArgument) => {
        const requiredStep = generateRandomString(10);
        const wahlbezirkId = generateRandomString(10);

        const guard = isStepDoneInElectionState(requiredStep);

        const to = {
          params: {
            wahlbezirkId: wahlbezirkId,
          },
        } as unknown as RouteLocationNormalized;
        if (wahlIdArgument) {
          to.params.wahlId = wahlIdArgument;
        }

        const result = guard(to, DUMMY_FROM, DUMMY_NEXT_GUARD);
        expect(result).toStrictEqual(false);
      }
    );

    it.each([undefined, ["stringArray"]])(
      "should_returnFalse_when_parameterWahlbezirkIdIsMissing",
      (wahlbezirkIdArgument) => {
        const requiredStep = generateRandomString(10);
        const wahlID = generateRandomString(10);

        const guard = isStepDoneInElectionState(requiredStep);

        const to = {
          params: {
            wahlId: wahlID,
          },
        } as unknown as RouteLocationNormalized;
        if (wahlbezirkIdArgument) {
          to.params.wahlbezirkId = wahlbezirkIdArgument;
        }

        const result = guard(to, DUMMY_FROM, DUMMY_NEXT_GUARD);
        expect(result).toStrictEqual(false);
      }
    );

    it.each([undefined, false])(
      "should_returnFalse_when_requiredStepIs'%s'InElectionState",
      (requiredStepStatusTestArgument) => {
        const requiredStep = generateRandomString(10);
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);

        const status = prepareStatus()
          .bezirkUndWahlID(
            prepareBezirkUndWahlID()
              .wahlID(wahlID)
              .wahlbezirkID(wahlbezirkID)
              .build()
          )
          .stepsDone({})
          .build();
        if (requiredStepStatusTestArgument !== undefined) {
          status.stepsDone[requiredStep] = requiredStepStatusTestArgument;
        }
        useStatusStore().status = [status];

        const guard = isStepDoneInElectionState(requiredStep);

        const to = {
          params: {
            wahlId: wahlID,
            wahlbezirkId: wahlbezirkID,
          },
        } as unknown as RouteLocationNormalized;

        const result = guard(to, DUMMY_FROM, DUMMY_NEXT_GUARD);
        expect(result).toStrictEqual(false);
      }
    );

    it("should_returnTrue_when_requiredStepIsPresentInElectionState", () => {
      const requiredStep = generateRandomString(10);
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const status = prepareStatus()
        .bezirkUndWahlID(
          prepareBezirkUndWahlID()
            .wahlID(wahlID)
            .wahlbezirkID(wahlbezirkID)
            .build()
        )
        .stepsDone({ [requiredStep]: true })
        .build();
      useStatusStore().status = [status];

      const guard = isStepDoneInElectionState(requiredStep);

      const to = {
        params: {
          wahlId: wahlID,
          wahlbezirkId: wahlbezirkID,
        },
      } as unknown as RouteLocationNormalized;
      const result = guard(to, DUMMY_FROM, DUMMY_NEXT_GUARD);

      expect(result).toStrictEqual(true);
    });
  });
});
