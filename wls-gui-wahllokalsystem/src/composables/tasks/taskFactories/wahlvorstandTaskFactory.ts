import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";

export function useWahlvorstandTaskFactory(): TaskFactory {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    if (!taskFactoryContext.isSchriftfuehrung) {
      return [];
    }

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

  return {
    createTasks,
  };
}
