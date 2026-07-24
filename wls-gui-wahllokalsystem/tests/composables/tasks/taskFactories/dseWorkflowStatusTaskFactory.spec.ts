import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useDseWorkflowStatusTaskFactory } from "@/composables/tasks/taskFactories/dseWorkflowStatusTaskFactory.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadDseWorkflowStatus: vi.fn(),
}));

vi.mock("@/composables/dse/dseWorkflowStatusService.ts", () => ({
  useDseWorkflowStatusService: vi.fn().mockImplementation(() => ({
    loadDseWorkflowStatus: mockDefinitions.loadDseWorkflowStatus,
  })),
}));

describe("dseWorkflowStatusTaskFactory.ts", () => {
  const { prepareTaskFactoryContext } = useTasksTestDataFactory();
  const { createTasks } = useDseWorkflowStatusTaskFactory();
  const { prepareWahl } = useWahlTestDataFactory();

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("createTasks", () => {
    describe("userHasRoleSchriftfuehrung", () => {
      it("should_returnTaskList_when_userHasRoleSchriftfuehrung", () => {
        const taskFactoryContext = prepareTaskFactoryContext()
          .isSchriftfuehrung(true)
          .build();
        const wahlenStore = useWahlenStore();

        wahlenStore.wahlenState.wahlen = [
          prepareWahl()
            // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
            .wahlID(taskFactoryContext.extendedWahlMetaData[0]!.wahlID)
            .build(),
        ];

        const result = createTasks(taskFactoryContext);

        expect(result.length).toStrictEqual(1);
      });

      it("should_haveExpectedCallback_when_userHasRoleSchriftfuehrung", () => {
        const taskFactoryContext = prepareTaskFactoryContext()
          .isSchriftfuehrung(true)
          .build();
        const wahlenStore = useWahlenStore();

        wahlenStore.wahlenState.wahlen = [
          prepareWahl()
            // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
            .wahlID(taskFactoryContext.extendedWahlMetaData[0]!.wahlID)
            .build(),
        ];

        mockDefinitions.loadDseWorkflowStatus.mockReturnValue(
          Promise.resolve()
        );

        const result = createTasks(taskFactoryContext);

        expect(result.length).toStrictEqual(1);

        result[0]?.callback();
        expect(mockDefinitions.loadDseWorkflowStatus).toHaveBeenCalledOnce();
        expect(result[0]?.name).toContain("DSE-Workflow-Status");
      });
    });
  });

  describe("userHasNotRoleSchriftfuehrung", () => {
    it("should_returnEmptyList_when_userHasNotRoleSchriftfuehrung", () => {
      const taskFactoryContext = prepareTaskFactoryContext()
        .isSchriftfuehrung(false)
        .build();

      const result = createTasks(taskFactoryContext);
      expect(result.length).toStrictEqual(0);
    });
  });
});
