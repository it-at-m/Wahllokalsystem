import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

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

  return {
    createTasks,
  };
}
