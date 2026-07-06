import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useTaskFactoryBuilder } from "@/composables/tasks/TaskFactoryBuilder.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const { whenUserIsSchriftfuehrung } = useTaskFactoryBuilder();

export function useEroeffnungsuhrzeitTaskFactory(): TaskFactory {
  const { eroeffnungsuhrzeitActions } = useWahlbezirkStore();

  function createTasks(): Task[] {
    return [
      {
        name: "Eröffnungsuhrzeit",
        callback: () => eroeffnungsuhrzeitActions.initEroeffnungsuhrzeit(),
      },
    ];
  }

  return whenUserIsSchriftfuehrung(createTasks);
}
