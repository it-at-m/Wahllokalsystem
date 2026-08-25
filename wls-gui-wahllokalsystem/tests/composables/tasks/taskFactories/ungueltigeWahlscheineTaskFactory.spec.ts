import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";

import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useUngueltigeWahlscheineTaskFactory } from "@/composables/tasks/taskFactories/ungueltigeWahlscheineTaskFactory.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  initUngueltigeWahlscheine: vi.fn(),
}));

vi.mock("@/stores/wahlbezirkStore.ts", () => ({
  useWahlbezirkStore: vi.fn().mockImplementation(() => ({
    ungueltigeWahlscheineActions: {
      initUngueltigeWahlscheine: mockDefinitions.initUngueltigeWahlscheine,
    },
  })),
}));

describe("ungueltigeWahlscheineTaskFactory.ts", () => {
  const { prepareTaskFactoryContext } = useTasksTestDataFactory();
  const { createTasks } = useUngueltigeWahlscheineTaskFactory();

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("createTasks", () => {
    describe("userHasRoleSchriftfuehrung", () => {
      it.each([
        {
          wahlbezirkArt: WahlbezirksArtEnum.UWB,
          tasksCreated: 1,
          taskCreatedAsString: "One",
        },
        {
          wahlbezirkArt: WahlbezirksArtEnum.BWB,
          tasksCreated: 0,
          taskCreatedAsString: "Zero",
        },
      ])(
        "should_return'$taskCreatedAsString'Task_when_calledWithWahlbezirkArt$wahlbezirkArt",
        ({ wahlbezirkArt, tasksCreated }) => {
          const taskFactoryContext: TaskFactoryContext =
            prepareTaskFactoryContext()
              .isSchriftfuehrung(true)
              .wahlbezirkArt(wahlbezirkArt)
              .build();
          const result = createTasks(taskFactoryContext);

          expect(result.length).toStrictEqual(tasksCreated);
        }
      );

      it("should_returnOneTaskWithExpectedCallback_when_calledWithCorrectWahlbezirkArt", () => {
        mockDefinitions.initUngueltigeWahlscheine.mockReturnValue(
          Promise.resolve()
        );

        const taskFactoryContext: TaskFactoryContext =
          prepareTaskFactoryContext()
            .isSchriftfuehrung(true)
            .wahlbezirkArt(WahlbezirksArtEnum.UWB)
            .build();

        const result = createTasks(taskFactoryContext);

        expect(result.length).toStrictEqual(1);

        result[0]?.callback();

        expect(
          mockDefinitions.initUngueltigeWahlscheine
        ).toHaveBeenCalledOnce();
      });
    });
  });

  describe("userHasNotRoleSchriftfuehrung", () => {
    it("should_returnEmptyList_when_called", () => {
      const context = prepareTaskFactoryContext()
        .isSchriftfuehrung(false)
        .build();

      const result = createTasks(context);
      expect(result.length).toStrictEqual(0);
    });
  });
});
