import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useTaskFactoryBuilder } from "@/composables/tasks/TaskFactoryBuilder.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { whenUserIsSchriftfuehrung } = useTaskFactoryBuilder();

export function useWahlvorbereitungTaskFactory(): TaskFactory {
  const WAHLVORBEREITUNG = "Wahlvorbereitung";

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    return taskFactoryContext.wahlbezirkArt == WahlbezirksArtEnum.UWB
      ? [_createTaskUrnenwahlvorbereitung()]
      : [_createTaskBriefwahlvorbereitung()];
  }

  function _createTaskUrnenwahlvorbereitung(): Task {
    const { urnenwahlVorbereitungActions } = useWahlbezirkStore();
    return {
      name: WAHLVORBEREITUNG,
      callback: () => {
        return urnenwahlVorbereitungActions.initUrnenwahlvorbereitung(false);
      },
    };
  }

  function _createTaskBriefwahlvorbereitung(): Task {
    const { briefwahlVorbereitungActions } = useWahlbezirkStore();
    return {
      name: WAHLVORBEREITUNG,
      callback: () => {
        return briefwahlVorbereitungActions.initBriefwahlvorbereitung(false);
      },
    };
  }

  return whenUserIsSchriftfuehrung(createTasks);
}
