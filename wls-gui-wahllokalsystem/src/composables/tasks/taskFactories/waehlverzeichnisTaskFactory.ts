import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useWaehlverzeichnisTaskFactory(): TaskFactory {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    const { pflegeWaehlerverzeichnisActions } = useWahlbezirkStore();

    return taskFactoryContext.wahlbezirkArt === WahlbezirksArtEnum.UWB &&
      taskFactoryContext.isSchriftfuehrung
      ? [
          {
            name: "Wählerverzeichnis",
            callback: () =>
              pflegeWaehlerverzeichnisActions.loadPflegeWaehlerverzeichnis(
                false
              ),
          },
        ]
      : [];
  }
  return {
    createTasks,
  };
}
