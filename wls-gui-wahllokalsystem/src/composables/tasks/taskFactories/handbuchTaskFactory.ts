import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useHandbuchService } from "@/composables/basisdaten/handbuchService.ts";

export function useHandbuchTaskFactory(): TaskFactory {
  const { getHandbuch } = useHandbuchService();

  function createTasks(): Task[] {
    return [{ name: "Handbuch", callback: () => getHandbuch(false) }];
  }

  return {
    createTasks,
  };
}
