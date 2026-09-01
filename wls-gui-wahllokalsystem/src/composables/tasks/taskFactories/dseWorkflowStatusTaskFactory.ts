import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useDseWorkflowStatusService } from "@/composables/dse/dseWorkflowStatusService.ts";
import { useTaskFactoryBuilder } from "@/composables/tasks/TaskFactoryBuilder.ts";

const { whenUserIsSchriftfuehrung } = useTaskFactoryBuilder();

export function useDseWorkflowStatusTaskFactory(): TaskFactory {
  const { loadDseWorkflowStatus } = useDseWorkflowStatusService();

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.extendedWahlMetaData.map((meta) => ({
      name: `DSE-Workflow-Status - ${meta.wahlName}`,
      callback: () =>
        loadDseWorkflowStatus(meta.wahlID, meta.wahlbezirkID, false),
    }));
  }

  return whenUserIsSchriftfuehrung(createTasks);
}
