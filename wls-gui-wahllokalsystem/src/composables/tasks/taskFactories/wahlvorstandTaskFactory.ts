import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useTaskFactoryBuilder } from "@/composables/tasks/TaskFactoryBuilder.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";

const { whenUserIsSchriftfuehrung } = useTaskFactoryBuilder();

export function useWahlvorstandTaskFactory(): TaskFactory {
  function createTasks(): Task[] {
    return [createTask()];
  }

  function createTask(): Task {
    const { initWahlvorstand } = useWahlvorstandStore();
    return {
      name: "Wahlvorstand",
      callback: () => {
        return initWahlvorstand(false);
      },
    };
  }

  return whenUserIsSchriftfuehrung(createTasks);
}
