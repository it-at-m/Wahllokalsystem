import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";
import type { TaskCreatorFunction } from "@/types/tasks/TaskCreatorFunction.ts";

export function useTaskFactoryBuilder() {
  function whenUserIsSchriftfuehrung(
    taskCreatorFunction: TaskCreatorFunction
  ): TaskFactory {
    return {
      createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
        if (!taskFactoryContext.isSchriftfuehrung) {
          return [];
        }

        return taskCreatorFunction(taskFactoryContext);
      },
    };
  }

  return {
    whenUserIsSchriftfuehrung,
  };
}
