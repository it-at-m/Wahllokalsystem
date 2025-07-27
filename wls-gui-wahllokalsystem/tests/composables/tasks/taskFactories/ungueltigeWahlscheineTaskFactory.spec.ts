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
    initUngueltigeWahlscheine: mockDefinitions.initUngueltigeWahlscheine,
  })),
}));

describe("ungueltigeWahlscheineTaskFactory.ts", () => {
  const { prepareTaskFactoryContext, createExtendedWahlMetaData } =
    useTasksTestDataFactory();
  const { createTasks } = useUngueltigeWahlscheineTaskFactory();

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("createTasks", () => {
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
        const extendedWahlMetaDataOne = createExtendedWahlMetaData();
        const extendedWahlMetaDataTwo = createExtendedWahlMetaData();
        const taskFactoryContext: TaskFactoryContext =
          prepareTaskFactoryContext()
            .extendedWahlMetaData([
              extendedWahlMetaDataOne,
              extendedWahlMetaDataTwo,
            ])
            .wahlbezirkArt(wahlbezirkArt)
            .build();
        const result = createTasks(taskFactoryContext);

        expect(result.length).toStrictEqual(tasksCreated);
      }
    );
  });
});
