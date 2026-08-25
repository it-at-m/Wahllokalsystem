import type { TaskCreatorFunction } from "@/types/tasks/TaskCreatorFunction.ts";

export interface TaskFactory {
  createTasks: TaskCreatorFunction;
}
