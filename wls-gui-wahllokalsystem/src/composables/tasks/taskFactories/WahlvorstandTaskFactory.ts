import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { TaskFactoryInterface } from "@/composables/tasks/TaskFactoryInterface.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";

export function useWahlvorstandTaskFactory(): TaskFactoryInterface {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  function createTasks(taskFactoryData: TaskFactoryContext): Task[] {
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
