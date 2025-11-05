import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useWahlvorbereitungTaskFactory(): TaskFactory {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.wahlbezirkArt == WahlbezirksArtEnum.UWB
      ? [_createTaskUrnenwahlvorbereitung()]
      : [_createTaskBriefwahlvorbereitung()];
  }

  function _createTaskUrnenwahlvorbereitung(): Task {
    const { urnenwahlVorbereitungActions } = useWahlbezirkStore();
    return {
      name: "Wahlvorbereitung",
      callback: () => {
        return urnenwahlVorbereitungActions.initUrnenwahlvorbereitung(false);
      },
    };
  }

  function _createTaskBriefwahlvorbereitung(): Task {
    const { briefwahlVorbereitungActions } = useWahlbezirkStore();
    return {
      name: "Wahlvorbereitung",
      callback: () => {
        return briefwahlVorbereitungActions.initBriefwahlvorbereitung(false);
      },
    };
  }

  return {
    createTasks,
  };
}
