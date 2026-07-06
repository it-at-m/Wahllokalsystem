import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { useTaskFactoryBuilder } from "@/composables/tasks/TaskFactoryBuilder.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import {
  getStapelForWahlart,
  StapelArtEnum,
} from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const { whenUserIsSchriftfuehrung } = useTaskFactoryBuilder();

export function useMBWWahlvorschlaegeAndErgebnisseTaskFactory(): TaskFactory {
  const wahlvorschlaegeStore = useWahlvorschlaegeStore();
  const ergebnismeldungsStore = useErgebnismeldungStore();

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    const tasks: Task[] = [];
    taskFactoryContext.extendedWahlMetaData.forEach((extendedWahlMetaData) => {
      if (extendedWahlMetaData.wahlArt === WahlWahlartEnum.Mbw) {
        tasks.push(_createMBWTask(extendedWahlMetaData));
      }
    });
    return tasks;
  }

  function _createMBWTask(taskFactoryMetaData: ExtendedWahlMetaData): Task {
    return {
      callback: async () => {
        const allStapelForWahlart = getStapelForWahlart(
          taskFactoryMetaData.wahlArt
        );

        const loadingPromises: Promise<void>[] = [];
        allStapelForWahlart.forEach((stapelArt) => {
          loadingPromises.push(
            ergebnismeldungsStore.loadErgebnisseByStapelArt(
              taskFactoryMetaData.wahlID,
              stapelArt,
              false
            )
          );
        });

        loadingPromises.push(
          wahlvorschlaegeStore.loadWahlvorschlaege(
            taskFactoryMetaData.wahlID,
            taskFactoryMetaData.wahlbezirkID
          )
        );

        await Promise.all(loadingPromises);

        //Prüfen ob StapelMBW_BC vollständig ist, um den Navigationsworkflow korrekt setzen zu können
        const ergebnisseForStapelBC =
          ergebnismeldungsStore.getErgebnisseByWahlIdAndStapelartOrUndefined(
            taskFactoryMetaData.wahlID,
            StapelArtEnum.MbwBC
          );
        const wahlvorschlaege =
          wahlvorschlaegeStore.getWahlvorschlaegeByWahlIDAndWahlbezirkID(
            taskFactoryMetaData.wahlID,
            taskFactoryMetaData.wahlbezirkID
          )?.wahlvorschlaege || ([] as Wahlvorschlag[]);
        const countKandidaten = wahlvorschlaege.flatMap(
          (wahlvorschlag) => wahlvorschlag.kandidaten
        ).length;

        if (ergebnisseForStapelBC?.ergebnisse.length === countKandidaten) {
          useWorkflowStore().setStepDone(
            taskFactoryMetaData.wahlID,
            taskFactoryMetaData.wahlbezirkID,
            MbwStepsEnum.MBW_STAPEL_BC
          );
        }
      },
      name: `Wahlvorschläge und Ergebnisse - ${taskFactoryMetaData.wahlName}`,
    };
  }

  return whenUserIsSchriftfuehrung(createTasks);
}
