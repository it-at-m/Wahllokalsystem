import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useEreignisStore } from "@/stores/ereignisStore.ts";

export function useEreignisseTaskFactory(): TaskFactory {
  function createTasks(): Task[] {
    return [_createTask()];
  }

  function _createTask(): Task {
    const { loadEreignisse } = useEreignisStore();
    return {
      name: "Ereignisse",
      callback: () => {
        return loadEreignisse();
      },
    };
  }

  return {
    createTasks,
  };
}
