import type {
  NavigationGuardNext,
  RouteLocationNormalized,
  RouteLocationNormalizedLoaded,
} from "vue-router";

import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";
import { useWorkflowTestDataFactory } from "@tests/utils/navigation/NavigationTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useNavigationGuards } from "@/composables/navigation/navigationGuards.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { generateRandomString } = useCommonTestDataFactory();
const { prepareBezirkUndWahlID } = useCommonErgebnismeldungTestDataFactory();
const { prepareElectionWorkflow } = useWorkflowTestDataFactory();
const { prepareUser } = useUserTestDataFactory();

describe("navigationGuards.ts", () => {
  const DUMMY_TO = {} as unknown as RouteLocationNormalized;
  const DUMMY_FROM = {} as unknown as RouteLocationNormalizedLoaded;
  const DUMMY_NEXT_GUARD = {} as unknown as NavigationGuardNext;

  beforeEach(() => {
    createTestingPinia({
      createSpy: vi.fn,
      stubActions: false,
    });
  });

  const {
    isStepDoneInElectionState,
    permitNavigationWhenWahlvorstandIsErfasst,
    permitNavigationWhenWahleroeffnungIsErfasst,
    permitNavigationWhenWahlumgebungIsErfasst,
    permitNavigationWhenWahlbriefeErfassenIsErfasst,
    permitNavigationWhenWahlbriefeZulassenIsErfasst,
    permitNavigationWhenWaehlerverzeichnisIsErfasst,
    permitNavigationWhenStimmabgabeIsErfasst,
    requiresWahlumgebungErfasstWhenWahlbezirksArtUwb,
    requiresWaehlerverzeichnisErfasstWhenWahlbezirksArtUwb,
    requiresWahleroeffnungErfasstWhenWahlbezirksArtBwb,
    permitNavigationOnlyForWahlbezirksArtUwb,
    permitNavigationOnlyForWahlbezirksArtBwb,
    permitNavigationOnlyIfUserIsLoggedOut,
  } = useNavigationGuards();

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

        const workflow = prepareElectionWorkflow()
          .bezirkUndWahlID(
            prepareBezirkUndWahlID()
              .wahlID(wahlID)
              .wahlbezirkID(wahlbezirkID)
              .build()
          )
          .stepsDone({})
          .build();
        if (requiredStepStatusTestArgument !== undefined) {
          workflow.stepsDone[requiredStep] = requiredStepStatusTestArgument;
        }
        useWorkflowStore().electionWorkflowsStates = [workflow];

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

      const workflow = prepareElectionWorkflow()
        .bezirkUndWahlID(
          prepareBezirkUndWahlID()
            .wahlID(wahlID)
            .wahlbezirkID(wahlbezirkID)
            .build()
        )
        .stepsDone({ [requiredStep]: true })
        .build();
      useWorkflowStore().electionWorkflowsStates = [workflow];

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

  describe("permitNavigationWhenWahlvorstandIsErfasst", () => {
    it("should_returnFalse_when_statusIsWahlvorstandErfasstIsFalse", () => {
      useWorkflowStore().isWahlvorstandErfasst = false;
      const result = permitNavigationWhenWahlvorstandIsErfasst(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_statusIsWahlvorstandErfasstIsTrue", () => {
      useWorkflowStore().isWahlvorstandErfasst = true;
      const result = permitNavigationWhenWahlvorstandIsErfasst(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });
  });

  describe("permitNavigationWhenWahleroeffnungIsErfasst", () => {
    it("should_returnFalse_when_statusIsWahleroeffnungErfasstIsFalse", () => {
      useWorkflowStore().isWahleroeffnungErfasst = false;
      const result = permitNavigationWhenWahleroeffnungIsErfasst(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_statusIsWahleroeffnungErfasstIsTrue", () => {
      useWorkflowStore().isWahleroeffnungErfasst = true;
      const result = permitNavigationWhenWahleroeffnungIsErfasst(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });
  });

  describe("requiresWahleroeffnungErfasstWhenWahlbezirksArtBwb", () => {
    it("should_returnFalse_when_statusIsWahleroeffnungErfasstIsFalseAndUsersWahlbezirksArtIsBwb", () => {
      useUserStore().setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      useWorkflowStore().isWahleroeffnungErfasst = false;
      const result = requiresWahleroeffnungErfasstWhenWahlbezirksArtBwb(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_statusIsWahleroeffnungErfasstIsTrueAndUsersWahlbezirksArtIsBwb", () => {
      useUserStore().setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      useWorkflowStore().isWahleroeffnungErfasst = true;
      const result = requiresWahleroeffnungErfasstWhenWahlbezirksArtBwb(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });

    it("should_returnTrue_when_statusIsWahleroeffnungErfasstIsFalseAndUsersWahlbezirksArtIsUwb", () => {
      useUserStore().setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      useWorkflowStore().isWahleroeffnungErfasst = false;
      const result = requiresWahleroeffnungErfasstWhenWahlbezirksArtBwb(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });
  });

  describe("permitNavigationWhenWahlumgebungIsErfasst", () => {
    it("should_returnFalse_when_statusIsWahlumgebungIstErfasstIsFalse", () => {
      useWorkflowStore().isWahlumgebungErfasst = false;
      const result = permitNavigationWhenWahlumgebungIsErfasst(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_statusIsWahlumgebungIstErfasstIsTrue", () => {
      useWorkflowStore().isWahlumgebungErfasst = true;
      const result = permitNavigationWhenWahlumgebungIsErfasst(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });
  });

  describe("requiresWahlumgebungErfasstWhenWahlbezirksArtUwb", () => {
    it("should_returnFalse_when_statusIsWahlumgebungErfasstIsFalseAndUsersWahlbezirksArtIsUwb", () => {
      useUserStore().setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      useWorkflowStore().isWahlumgebungErfasst = false;
      const result = requiresWahlumgebungErfasstWhenWahlbezirksArtUwb(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_statusIsWahlumgebungErfasstIsTrueAndUsersWahlbezirksArtIsUwb", () => {
      useUserStore().setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      useWorkflowStore().isWahlumgebungErfasst = true;
      const result = requiresWahlumgebungErfasstWhenWahlbezirksArtUwb(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });

    it("should_returnTrue_when_statusIsWahlumgebungErfasstIsFalseAndUsersWahlbezirksArtIsBwb", () => {
      useUserStore().setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      useWorkflowStore().isWahlumgebungErfasst = false;
      const result = requiresWahlumgebungErfasstWhenWahlbezirksArtUwb(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });
  });

  describe("permitNavigationWhenWahlbriefeErfassenIsErfasst", () => {
    it("should_returnFalse_when_statusIsWahlbriefeErfassenErfasstIsFalse", () => {
      useWorkflowStore().isWahlbriefeErfassenErfasst = false;
      const result = permitNavigationWhenWahlbriefeErfassenIsErfasst(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_statusIsWahlbriefeErfassenErfasstIsTrue", () => {
      useWorkflowStore().isWahlbriefeErfassenErfasst = true;
      const result = permitNavigationWhenWahlbriefeErfassenIsErfasst(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });
  });

  describe("permitNavigationWhenWahlbriefeZulassenIsErfasst", () => {
    it("should_returnFalse_when_statusIsWahlbriefeZulassenErfasstIsFalse", () => {
      useWorkflowStore().isWahlbriefeZulassenErfasst = false;
      const result = permitNavigationWhenWahlbriefeZulassenIsErfasst(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_statusIsWahlbriefeZulassenErfasstIsTrue", () => {
      useWorkflowStore().isWahlbriefeZulassenErfasst = true;
      const result = permitNavigationWhenWahlbriefeZulassenIsErfasst(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });
  });

  describe("permitNavigationWhenWaehlerverzeichnisIsErfasst", () => {
    it("should_returnFalse_when_statusIsWaehlerverzeichnisErfasstIsFalse", () => {
      useWorkflowStore().isWaehlerverzeichnisErfasst = false;
      const result = permitNavigationWhenWaehlerverzeichnisIsErfasst(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_statusIsWaehlerverzeichnisErfasstIsTrue", () => {
      useWorkflowStore().isWaehlerverzeichnisErfasst = true;
      const result = permitNavigationWhenWaehlerverzeichnisIsErfasst(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });
  });

  describe("requiresWaehlerverzeichnisErfasstWhenWahlbezirksArtUwb", () => {
    it("should_returnFalse_when_statusIsWaehlerverzeichnisErfasstIsFalseAndUsersWahlbezirksArtIsUwb", () => {
      useUserStore().setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      useWorkflowStore().isWaehlerverzeichnisErfasst = false;
      const result = requiresWaehlerverzeichnisErfasstWhenWahlbezirksArtUwb(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_statusIsWaehlerverzeichnisErfasstIsTrueAndUsersWahlbezirksArtIsUwb", () => {
      useUserStore().setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      useWorkflowStore().isWaehlerverzeichnisErfasst = true;
      const result = requiresWaehlerverzeichnisErfasstWhenWahlbezirksArtUwb(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });

    it("should_returnTrue_when_statusIsWaehlerverzeichnisErfasstIsFalseAndUsersWahlbezirksArtIsBwb", () => {
      useUserStore().setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      useWorkflowStore().isWaehlerverzeichnisErfasst = false;
      const result = requiresWaehlerverzeichnisErfasstWhenWahlbezirksArtUwb(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });
  });

  describe("permitNavigationWhenStimmabgabeIsErfasst", () => {
    it("should_returnFalse_when_statusIsWahlvorstandErfasstIsFalse", () => {
      useWorkflowStore().isStimmabgabeErfasst = false;
      const result = permitNavigationWhenStimmabgabeIsErfasst(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_statusIsWahlvorstandErfasstIsTrue", () => {
      useWorkflowStore().isStimmabgabeErfasst = true;
      const result = permitNavigationWhenStimmabgabeIsErfasst(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });
  });

  describe("permitNavigationOnlyForWahlbezirksArtUwb", () => {
    it("should_returnFalse_when_usersWahlbezirksArtIsBwb", () => {
      useUserStore().setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      const result = permitNavigationOnlyForWahlbezirksArtUwb(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_usersWahlbezirksArtIsUwb", () => {
      useUserStore().setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      const result = permitNavigationOnlyForWahlbezirksArtUwb(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });
  });

  describe("permitNavigationOnlyForWahlbezirksArtBwb", () => {
    it("should_returnFalse_when_usersWahlbezirksArtIsUwb", () => {
      useUserStore().setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      const result = permitNavigationOnlyForWahlbezirksArtBwb(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_usersWahlbezirksArtIsBwb", () => {
      useUserStore().setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );
      const result = permitNavigationOnlyForWahlbezirksArtBwb(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });
  });

  describe("permitNavigationOnlyIfUserIsLoggedOut", () => {
    it("should_returnFalse_when_userIsLoggedIn", () => {
      useUserStore().isUserLoggedIn = true;
      const result = permitNavigationOnlyIfUserIsLoggedOut(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_userIsLoggedOut", () => {
      useUserStore().isUserLoggedIn = false;
      const result = permitNavigationOnlyIfUserIsLoggedOut(
        DUMMY_TO,
        DUMMY_FROM,
        DUMMY_NEXT_GUARD
      );
      expect(result).toStrictEqual(true);
    });
  });
});
