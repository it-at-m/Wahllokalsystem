import type { TaskFactoryData } from "@/composables/tasks/TaskFactoryData.ts";
import type { Task } from "@/types/tasks/Task.ts";

export interface TaskFactoryInterface {
  createTasks(taskFactoryData: TaskFactoryData): Task[];
}
