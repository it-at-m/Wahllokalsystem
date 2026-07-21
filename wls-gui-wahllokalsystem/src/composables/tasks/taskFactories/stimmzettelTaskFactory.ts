import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useStimmzettelService } from "@/composables/stimmzettelerfassung/stimmzettelService.ts";
import { useTaskFactoryBuilder } from "@/composables/tasks/TaskFactoryBuilder.ts";

const { whenUserIsSchriftfuehrung } = useTaskFactoryBuilder();

export function useStimmzettelTaskFactory(): TaskFactory {
  const { loadAnzahlStimmzettel } = useStimmzettelService();

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.extendedWahlMetaData.map(
      (metaData: ExtendedWahlMetaData) => _createTask(metaData)
    );
  }

  function _createTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    return {
      callback: () =>
        loadAnzahlStimmzettel(
          taskFactoryMetaData.wahlID,
          taskFactoryMetaData.wahlbezirkID
        ),
      name: `Stimmzettel für ${taskFactoryMetaData.wahlName}`,
    };
  }

  return whenUserIsSchriftfuehrung(createTasks);
}
