import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

export interface TaskFactory {
  createTasks(taskFactoryContext: TaskFactoryContext): Task[];
}
