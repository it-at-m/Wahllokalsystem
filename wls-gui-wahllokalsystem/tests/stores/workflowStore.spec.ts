import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";
import { useWorkflowTestDataFactory } from "@tests/utils/navigation/NavigationTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWorkflowStore } from "@/stores/workflowStore.ts";

const { generateRandomString } = useCommonTestDataFactory();
const { prepareElectionWorkflow } = useWorkflowTestDataFactory();
const { prepareBezirkUndWahlID } = useCommonErgebnismeldungTestDataFactory();

describe("workflowStore.ts", () => {
  beforeEach(() => {
    createTestingPinia({
      createSpy: vi.fn,
      stubActions: false,
    });
  });

  describe("electionWorkflows", () => {
    it("should_returnEmptyArray_when_getAfterSetup", () => {
      expect(useWorkflowStore().electionWorkflows).toStrictEqual([]);
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

  describe("getStatus", () => {
    it("should_returnUndefined_when_workflowWithIDsDoesNotExist", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      expect(
        useWorkflowStore().getStatus(wahlID, wahlbezirkID)
      ).toBeUndefined();
    });

    it("should_returnFirstWorkflow_when_workflowWithIDsExist", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const workflowNotToFind = createWorkflow(
        wahlID + "sth",
        wahlbezirkID + "sth"
      );
      const workflowToFind = createWorkflow(wahlID, wahlbezirkID);
      useWorkflowStore().electionWorkflows = [
        workflowNotToFind,
        workflowToFind,
      ];

      const result = useWorkflowStore().getStatus(wahlID, wahlbezirkID);

      expect(result).toStrictEqual(workflowToFind);
    });
  });

  describe("initStatus", () => {
    it("should_createNewWorkflowInUnfinishedState_when_called", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      useWorkflowStore().initStatus(wahlID, wahlbezirkID);

      const result = useWorkflowStore().getStatus(wahlID, wahlbezirkID);
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
    describe("should_returnFalse_when_workflowWithIDsDoesNotExist", () => {
      it("arrayIsEmpty", () => {
        useWorkflowStore().electionWorkflows = [];
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);

        expect(
          useWorkflowStore().isElectionFinished(wahlID, wahlbezirkID)
        ).toStrictEqual(false);
      });

      it("wahlIDDoesNotMatch", () => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        useWorkflowStore().electionWorkflows = [
          createWorkflow(wahlID + "sth", wahlbezirkID),
        ];

        expect(
          useWorkflowStore().isElectionFinished(wahlID, wahlbezirkID)
        ).toStrictEqual(false);
      });

      it("wahlbezirkIDDoesNotMatch", () => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        useWorkflowStore().electionWorkflows = [
          createWorkflow(wahlID, wahlbezirkID + "sth"),
        ];

        expect(
          useWorkflowStore().isElectionFinished(wahlID, wahlbezirkID)
        ).toStrictEqual(false);
      });

      it("wahlIDAndWahlbezirkIDDoesNotMatch", () => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        useWorkflowStore().electionWorkflows = [
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
      useWorkflowStore().electionWorkflows = [workflow];

      expect(
        useWorkflowStore().isElectionFinished(wahlID, wahlbezirkID)
      ).toStrictEqual(false);
    });

    it("should_returnTrue_when_IdsAreMatchingAndNiederschriftIsDone", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const workflow = createWorkflow(wahlID, wahlbezirkID);
      workflow.isNiederschriftDone = true;
      useWorkflowStore().electionWorkflows = [workflow];

      expect(
        useWorkflowStore().isElectionFinished(wahlID, wahlbezirkID)
      ).toStrictEqual(true);
    });
  });

  describe("isStepDone", () => {
    describe("should_returnFalse_when_workflowForIdsDoesNotExist", () => {
      it("arrayIsEmpty", () => {
        useWorkflowStore().electionWorkflows = [];
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        const stepToCheck = generateRandomString(10);

        expect(
          useWorkflowStore().isStepDone(wahlID, wahlbezirkID, stepToCheck)
        ).toStrictEqual(false);
      });

      it("wahlIDDoesNotMatch", () => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        const stepToCheck = generateRandomString(10);
        useWorkflowStore().electionWorkflows = [
          createWorkflow(wahlID + "sth", wahlbezirkID),
        ];

        expect(
          useWorkflowStore().isStepDone(wahlID, wahlbezirkID, stepToCheck)
        ).toStrictEqual(false);
      });

      it("wahlbezirkIDDoesNotMatch", () => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        const stepToCheck = generateRandomString(10);
        useWorkflowStore().electionWorkflows = [
          createWorkflow(wahlID, wahlbezirkID + "sth"),
        ];

        expect(
          useWorkflowStore().isStepDone(wahlID, wahlbezirkID, stepToCheck)
        ).toStrictEqual(false);
      });

      it("wahlIDAndWahlbezirkIDDoesNotMatch", () => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        const stepToCheck = generateRandomString(10);
        useWorkflowStore().electionWorkflows = [
          createWorkflow(wahlID + "sth", wahlbezirkID + "sth"),
        ];

        expect(
          useWorkflowStore().isStepDone(wahlID, wahlbezirkID, stepToCheck)
        ).toStrictEqual(false);
      });
    });

    it("should_returnFalse_when_workflowForIdsExistsButHasNotThatStep", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stepToCheck = generateRandomString(10);
      useWorkflowStore().electionWorkflows = [
        createWorkflow(wahlID, wahlbezirkID),
      ];

      expect(
        useWorkflowStore().isStepDone(wahlID, wahlbezirkID, stepToCheck)
      ).toStrictEqual(false);
    });

    it("should_returnFalse_when_workflowForIdsExistsHasThatStepWithFalse", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stepToCheck = generateRandomString(10);
      const workflow = createWorkflow(wahlID, wahlbezirkID);
      workflow.stepsDone[stepToCheck] = false;
      useWorkflowStore().electionWorkflows = [workflow];

      expect(
        useWorkflowStore().isStepDone(wahlID, wahlbezirkID, stepToCheck)
      ).toStrictEqual(false);
    });

    it("should_returnTrue_when_workflowForIdsExistsHasThatStepWithTrue", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const stepToCheck = generateRandomString(10);
      const workflow = createWorkflow(wahlID, wahlbezirkID);
      workflow.stepsDone[stepToCheck] = true;
      useWorkflowStore().electionWorkflows = [workflow];

      expect(
        useWorkflowStore().isStepDone(wahlID, wahlbezirkID, stepToCheck)
      ).toStrictEqual(true);
    });
  });

  describe("setStepDone", () => {
    describe("should_doNothing_when_workflowForIdsDoesNotExist", () => {
      it("arrayIsEmpty", () => {
        useWorkflowStore().electionWorkflows = [];
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        const step = generateRandomString(10);

        useWorkflowStore().setStepDone(wahlID, wahlbezirkID, step);
        expect(useWorkflowStore().electionWorkflows).toStrictEqual([]);
      });

      it("wahlIDDoesNotMatch", () => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        const step = generateRandomString(10);
        useWorkflowStore().electionWorkflows = [
          createWorkflow(wahlID + "sth", wahlbezirkID),
        ];

        useWorkflowStore().setStepDone(wahlID, wahlbezirkID, step);
        expect(useWorkflowStore().electionWorkflows.length).toStrictEqual(1);
        expect(
          useWorkflowStore().electionWorkflows[0]?.stepsDone[step]
        ).toBeUndefined();
      });

      it("wahlbezirkIDDoesNotMatch", () => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        const step = generateRandomString(10);
        useWorkflowStore().electionWorkflows = [
          createWorkflow(wahlID, wahlbezirkID + "sth"),
        ];

        useWorkflowStore().setStepDone(wahlID, wahlbezirkID, step);
        expect(useWorkflowStore().electionWorkflows.length).toStrictEqual(1);
        expect(
          useWorkflowStore().electionWorkflows[0]?.stepsDone[step]
        ).toBeUndefined();
      });

      it("wahlIDAndWahlbezirkIDDoesNotMatch", () => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        const step = generateRandomString(10);
        useWorkflowStore().electionWorkflows = [
          createWorkflow(wahlID + "sth", wahlbezirkID + "sth"),
        ];

        useWorkflowStore().setStepDone(wahlID, wahlbezirkID, step);
        expect(useWorkflowStore().electionWorkflows.length).toStrictEqual(1);
        expect(
          useWorkflowStore().electionWorkflows[0]?.stepsDone[step]
        ).toBeUndefined();
      });
    });

    it.each([true, false])(
      "should_set'%s'_when_workflowWithIDsExist",
      (stepDoneArgument) => {
        const wahlID = generateRandomString(10);
        const wahlbezirkID = generateRandomString(10);
        const step = generateRandomString(10);
        const workflow = createWorkflow(wahlID, wahlbezirkID);
        useWorkflowStore().electionWorkflows = [workflow];

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
      useWorkflowStore().electionWorkflows = [workflow];

      useWorkflowStore().setStepDone(wahlID, wahlbezirkID, step);
      expect(workflow.stepsDone[step]).toStrictEqual(true);
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
