import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";
import { useWorkflowTestDataFactory } from "@tests/utils/navigation/NavigationTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

const { generateRandomString, generateRandomBoolean } =
  useCommonTestDataFactory();
const { prepareElectionWorkflow } = useWorkflowTestDataFactory();
const { prepareBezirkUndWahlID } = useCommonErgebnismeldungTestDataFactory();

describe("workflowStore.ts", () => {
  beforeEach(() => {
    createTestingPinia({
      createSpy: vi.fn,
      stubActions: false,
    });
  });

  describe("electionWorkflowsStates", () => {
    it("should_returnEmptyArray_when_getAfterSetup", () => {
      expect(useWorkflowStore().electionWorkflowsStates).toStrictEqual([]);
    });
  });

  describe("isWahlvorstandErfasst", () => {
    it("should_returnFalse_when_getAfterSetup", () => {
      expect(useWorkflowStore().isWahlvorstandErfasst).toStrictEqual(false);
    });
  });

  describe("isWahlumgebungErfasst", () => {
    it("should_returnFalse_when_getAfterSetup", () => {
      expect(useWorkflowStore().isWahlumgebungErfasst).toStrictEqual(false);
    });
  });

  describe("getElectionWorkflowState", () => {
    it("should_returnUndefined_when_workflowStateWithIDsDoesNotExist", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      expect(
        useWorkflowStore().getElectionWorkflowState(wahlID, wahlbezirkID)
      ).toBeUndefined();
    });

    it("should_returnFirstWorkflow_when_workflowSateWithIDsExist", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const workflowNotToFind = createWorkflow(
        wahlID + "sth",
        wahlbezirkID + "sth"
      );
      const workflowToFind = createWorkflow(wahlID, wahlbezirkID);
      useWorkflowStore().electionWorkflowsStates = [
        workflowNotToFind,
        workflowToFind,
      ];

      const result = useWorkflowStore().getElectionWorkflowState(
        wahlID,
        wahlbezirkID
      );

      expect(result).toStrictEqual(workflowToFind);
    });
  });

  describe("getWorkflowStateForRoute", () => {
    it("should_returnFalse_when_workflowStateWithIDsDoesNotExist", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      expect(
        useWorkflowStore().getWorkflowStateForRoute(
          wahlID,
          wahlbezirkID,
          MbwRoutesEnum.MBW_NIEDERSCHRIFT
        )
      ).toBe(false);
    });

    it("should_returnFalse_when_routeNameDoesNotExist", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const workflowToFind = createWorkflow(wahlID, wahlbezirkID);
      useWorkflowStore().electionWorkflowsStates = [workflowToFind];

      expect(
        useWorkflowStore().getWorkflowStateForRoute(
          wahlID,
          wahlbezirkID,
          "routeName"
        )
      ).toBe(false);
    });

    it("should_returnTrue_when_workflowSateFourRouteNameIsDone", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const step = MbwRoutesEnum.MBW_NIEDERSCHRIFT;

      const workflowToFind = createWorkflow(wahlID, wahlbezirkID);
      workflowToFind.stepsDone[step] = true;
      useWorkflowStore().electionWorkflowsStates = [workflowToFind];

      expect(
        useWorkflowStore().getWorkflowStateForRoute(wahlID, wahlbezirkID, step)
      ).toStrictEqual(true);
    });
  });

  describe("initElectionWorkflowState", () => {
    it("should_createNewWorkflowStateInUnfinishedState_when_called", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      useWorkflowStore().initElectionWorkflowState(wahlID, wahlbezirkID);

      const result = useWorkflowStore().getElectionWorkflowState(
        wahlID,
        wahlbezirkID
      );
      expect(result).toStrictEqual({
        bezirkUndWahlID: {
          wahlID,
          wahlbezirkID,
        },
        isSchnellmeldungDone: false,
        isNiederschriftDone: false,
        stepsDone: {},
      });
    });
  });

  describe("isElectionFinished", () => {
    describe("should_returnFalse_when_workflowStateWithIDsDoesNotExist", () => {
      it("arrayIsEmpty", () => {
        useWorkflowStore().electionWorkflowsStates = [];
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);

        expect(
          useWorkflowStore().isElectionFinished(wahlID, wahlbezirkID)
        ).toStrictEqual(false);
      });

      it("wahlIDDoesNotMatch", () => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        useWorkflowStore().electionWorkflowsStates = [
          createWorkflow(wahlID + "sth", wahlbezirkID),
        ];

        expect(
          useWorkflowStore().isElectionFinished(wahlID, wahlbezirkID)
        ).toStrictEqual(false);
      });

      it("wahlbezirkIDDoesNotMatch", () => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        useWorkflowStore().electionWorkflowsStates = [
          createWorkflow(wahlID, wahlbezirkID + "sth"),
        ];

        expect(
          useWorkflowStore().isElectionFinished(wahlID, wahlbezirkID)
        ).toStrictEqual(false);
      });

      it("wahlIDAndWahlbezirkIDDoesNotMatch", () => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        useWorkflowStore().electionWorkflowsStates = [
          createWorkflow(wahlID + "sth", wahlbezirkID + "sth"),
        ];

        expect(
          useWorkflowStore().isElectionFinished(wahlID, wahlbezirkID)
        ).toStrictEqual(false);
      });
    });

    it("idsAreMatchingButNiederschriftIsNotDone", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const workflow = createWorkflow(wahlID, wahlbezirkID);
      workflow.isNiederschriftDone = false;
      useWorkflowStore().electionWorkflowsStates = [workflow];

      expect(
        useWorkflowStore().isElectionFinished(wahlID, wahlbezirkID)
      ).toStrictEqual(false);
    });

    it("should_returnTrue_when_IdsAreMatchingAndNiederschriftIsDone", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const workflow = createWorkflow(wahlID, wahlbezirkID);
      workflow.isNiederschriftDone = true;
      useWorkflowStore().electionWorkflowsStates = [workflow];

      expect(
        useWorkflowStore().isElectionFinished(wahlID, wahlbezirkID)
      ).toStrictEqual(true);
    });
  });

  describe("areAllElectionsFinished", () => {
    it("should_returnFalse_when_atLeastOneUnfinishedWahlExists", () => {
      const workflow1 = createWorkflow(
        generateRandomString(10),
        generateRandomString(10)
      );
      workflow1.isNiederschriftDone = false;
      const workflow2 = createWorkflow(
        generateRandomString(10),
        generateRandomString(10)
      );
      workflow2.isNiederschriftDone = true;

      useWorkflowStore().electionWorkflowsStates = [workflow1, workflow2];

      expect(useWorkflowStore().areAllElectionsFinished).toStrictEqual(false);
    });

    it("should_returnTrue_when_allWahlenAreFinished", () => {
      const workflow1 = createWorkflow(
        generateRandomString(10),
        generateRandomString(10)
      );
      workflow1.isNiederschriftDone = true;
      const workflow2 = createWorkflow(
        generateRandomString(10),
        generateRandomString(10)
      );
      workflow2.isNiederschriftDone = true;

      useWorkflowStore().electionWorkflowsStates = [workflow1, workflow2];

      expect(useWorkflowStore().areAllElectionsFinished).toStrictEqual(true);
    });
  });

  describe("isStepDone", () => {
    it("should_returnFalse_when_workflowStateIsEmpty", () => {
      useWorkflowStore().electionWorkflowsStates = [];
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stepToCheck = generateRandomString(10);

      expect(
        useWorkflowStore().isStepDone(wahlID, wahlbezirkID, stepToCheck)
      ).toStrictEqual(false);
    });

    it("should_returnFalse_when_wahlIDDoesNotMatch", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stepToCheck = generateRandomString(10);
      useWorkflowStore().electionWorkflowsStates = [
        createWorkflow(wahlID + "sth", wahlbezirkID),
      ];

      expect(
        useWorkflowStore().isStepDone(wahlID, wahlbezirkID, stepToCheck)
      ).toStrictEqual(false);
    });

    it("should_returnFalse_when_wahlbezirkIDDoesNotMatch", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stepToCheck = generateRandomString(10);
      useWorkflowStore().electionWorkflowsStates = [
        createWorkflow(wahlID, wahlbezirkID + "sth"),
      ];

      expect(
        useWorkflowStore().isStepDone(wahlID, wahlbezirkID, stepToCheck)
      ).toStrictEqual(false);
    });

    it("should_returnFalse_when_wahlIDAndWahlbezirkIDDoesNotMatch", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stepToCheck = generateRandomString(10);
      useWorkflowStore().electionWorkflowsStates = [
        createWorkflow(wahlID + "sth", wahlbezirkID + "sth"),
      ];

      expect(
        useWorkflowStore().isStepDone(wahlID, wahlbezirkID, stepToCheck)
      ).toStrictEqual(false);
    });

    it("should_returnFalse_when_workflowStateForIdsExistsButHasNotThatStep", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stepToCheck = generateRandomString(10);
      useWorkflowStore().electionWorkflowsStates = [
        createWorkflow(wahlID, wahlbezirkID),
      ];

      expect(
        useWorkflowStore().isStepDone(wahlID, wahlbezirkID, stepToCheck)
      ).toStrictEqual(false);
    });

    it("should_returnFalse_when_workflowStateForIdsExistsHasThatStepWithFalse", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stepToCheck = generateRandomString(10);
      const workflow = createWorkflow(wahlID, wahlbezirkID);
      workflow.stepsDone[stepToCheck] = false;
      useWorkflowStore().electionWorkflowsStates = [workflow];

      expect(
        useWorkflowStore().isStepDone(wahlID, wahlbezirkID, stepToCheck)
      ).toStrictEqual(false);
    });

    it("should_returnTrue_when_workflowStateForIdsExistsHasThatStepWithTrue", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stepToCheck = generateRandomString(10);
      const workflow = createWorkflow(wahlID, wahlbezirkID);
      workflow.stepsDone[stepToCheck] = true;
      useWorkflowStore().electionWorkflowsStates = [workflow];

      expect(
        useWorkflowStore().isStepDone(wahlID, wahlbezirkID, stepToCheck)
      ).toStrictEqual(true);
    });
  });

  describe("setStepDone", () => {
    it("should_doNothing_when_workflowStateArrayIsEmpty", () => {
      useWorkflowStore().electionWorkflowsStates = [];
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const step = generateRandomString(10);

      useWorkflowStore().setStepDone(wahlID, wahlbezirkID, step);
      expect(useWorkflowStore().electionWorkflowsStates).toStrictEqual([]);
    });

    it("should_doNothing_when_wahlIDDoesNotMatch", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const step = generateRandomString(10);
      useWorkflowStore().electionWorkflowsStates = [
        createWorkflow(wahlID + "sth", wahlbezirkID),
      ];

      useWorkflowStore().setStepDone(wahlID, wahlbezirkID, step);
      expect(useWorkflowStore().electionWorkflowsStates.length).toStrictEqual(
        1
      );
      expect(
        useWorkflowStore().electionWorkflowsStates[0]?.stepsDone[step]
      ).toBeUndefined();
    });

    it("should_doNothing_when_wahlbezirkIDDoesNotMatch", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const step = generateRandomString(10);
      useWorkflowStore().electionWorkflowsStates = [
        createWorkflow(wahlID, wahlbezirkID + "sth"),
      ];

      useWorkflowStore().setStepDone(wahlID, wahlbezirkID, step);
      expect(useWorkflowStore().electionWorkflowsStates.length).toStrictEqual(
        1
      );
      expect(
        useWorkflowStore().electionWorkflowsStates[0]?.stepsDone[step]
      ).toBeUndefined();
    });

    it("should_doNothing_when_wahlIDAndWahlbezirkIDDoesNotMatch", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const step = generateRandomString(10);
      useWorkflowStore().electionWorkflowsStates = [
        createWorkflow(wahlID + "sth", wahlbezirkID + "sth"),
      ];

      useWorkflowStore().setStepDone(wahlID, wahlbezirkID, step);
      expect(useWorkflowStore().electionWorkflowsStates.length).toStrictEqual(
        1
      );
      expect(
        useWorkflowStore().electionWorkflowsStates[0]?.stepsDone[step]
      ).toBeUndefined();
    });

    it.each([true, false])(
      "should_set'%s'_when_workflowStateWithIDsExist",
      (stepDoneArgument) => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        const step = generateRandomString(10);
        const workflow = createWorkflow(wahlID, wahlbezirkID);
        useWorkflowStore().electionWorkflowsStates = [workflow];

        expect(workflow.stepsDone).toStrictEqual({});
        useWorkflowStore().setStepDone(
          wahlID,
          wahlbezirkID,
          step,
          stepDoneArgument
        );
        expect(workflow.stepsDone).toStrictEqual({ [step]: stepDoneArgument });
      }
    );

    it("should_setStepDoneToTrue_when_noArgumentIsProvided", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const step = generateRandomString(10);
      const workflow = createWorkflow(wahlID, wahlbezirkID);
      workflow.stepsDone[step] = false;
      useWorkflowStore().electionWorkflowsStates = [workflow];

      useWorkflowStore().setStepDone(wahlID, wahlbezirkID, step);
      expect(workflow.stepsDone[step]).toStrictEqual(true);
    });
  });

  describe("isWahlbriefzulassungErfasst", () => {
    it("should_returnTrue_when_allRequiredStepsAreTrue", () => {
      useWorkflowStore().isWahleroeffnungErfasst = true;
      useWorkflowStore().isWahlumgebungErfasst = true;
      useWorkflowStore().isWahlbriefeErfassenErfasst = true;
      useWorkflowStore().isWahlbriefeZulassenErfasst = true;

      expect(useWorkflowStore().isWahlbriefzulassungErfasst).toBe(true);
    });

    it("should_returnFalse_when_atLeastOneRequiredStepIsFalse", () => {
      useWorkflowStore().isWahleroeffnungErfasst = false;
      useWorkflowStore().isWahlumgebungErfasst = generateRandomBoolean();
      useWorkflowStore().isWahlbriefeErfassenErfasst = generateRandomBoolean();
      useWorkflowStore().isWahlbriefeZulassenErfasst = generateRandomBoolean();

      expect(useWorkflowStore().isWahlbriefzulassungErfasst).toBe(false);
    });
  });

  describe("isWahlhandlungErfasst", () => {
    it("should_returnTrue_when_allRequiredStepsAreTrue", () => {
      useWorkflowStore().isWahlumgebungErfasst = true;
      useWorkflowStore().isWaehlerverzeichnisErfasst = true;
      useWorkflowStore().isWahleroeffnungErfasst = true;
      useWorkflowStore().isStimmabgabeErfasst = true;

      expect(useWorkflowStore().isWahlhandlungErfasst).toBe(true);
    });

    it("should_returnFalse_when_atLeastOneRequiredStepIsFalse", () => {
      useWorkflowStore().isWahlumgebungErfasst = false;
      useWorkflowStore().isWaehlerverzeichnisErfasst = generateRandomBoolean();
      useWorkflowStore().isWahleroeffnungErfasst = generateRandomBoolean();
      useWorkflowStore().isStimmabgabeErfasst = generateRandomBoolean();

      expect(useWorkflowStore().isWahlhandlungErfasst).toBe(false);
    });
  });

  function createWorkflow(wahlID: string, wahlbezirkID: string) {
    return prepareElectionWorkflow()
      .bezirkUndWahlID(
        prepareBezirkUndWahlID()
          .wahlID(wahlID)
          .wahlbezirkID(wahlbezirkID)
          .build()
      )
      .build();
  }
});
