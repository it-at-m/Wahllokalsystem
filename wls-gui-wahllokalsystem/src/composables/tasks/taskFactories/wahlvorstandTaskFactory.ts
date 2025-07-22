import type { TaskFactoryInterface } from "@/composables/tasks/TaskFactoryInterface.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";

export function useWahlvorstandTaskFactory(): TaskFactoryInterface {
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

  return {
    createTasks,
  };
}
