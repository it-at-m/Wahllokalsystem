import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useTaskFactoryBuilder } from "@/composables/tasks/TaskFactoryBuilder.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";

const { whenUserIsSchriftfuehrung } = useTaskFactoryBuilder();

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

  return whenUserIsSchriftfuehrung(createTasks);
}
