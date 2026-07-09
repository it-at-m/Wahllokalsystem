import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useTaskFactoryBuilder } from "@/composables/tasks/TaskFactoryBuilder.ts";
import { useMonitoringStore } from "@/stores/monitoringStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { whenUserIsSchriftfuehrung } = useTaskFactoryBuilder();

export function useWaehlerTaskFactory(): TaskFactory {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    const { loadWaehler } = useMonitoringStore();

    return taskFactoryContext.wahlbezirkArt === WahlbezirksArtEnum.UWB
      ? [
          {
            name: "Wahlbeteiligung",
            callback: () => loadWaehler(),
          },
        ]
      : [];
  }

  return whenUserIsSchriftfuehrung(createTasks);
}
