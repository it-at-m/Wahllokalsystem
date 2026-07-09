import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useTaskFactoryBuilder } from "@/composables/tasks/TaskFactoryBuilder.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import {
  getStapelForWahlart,
  StapelArtEnum,
} from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { whenUserIsSchriftfuehrung } = useTaskFactoryBuilder();

export function useErgebnisseTaskFactory(): TaskFactory {
  const { loadErgebnisseByStapelArt } = useErgebnismeldungStore();

  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    const tasksForWahlen: Task[] = [];

    taskFactoryContext.extendedWahlMetaData
      .filter((metaData) => metaData.wahlArt !== WahlWahlartEnum.Mbw) //has own task factory
      .forEach((metaData: ExtendedWahlMetaData) => {
        const allStapelForWahlart = getStapelForWahlart(metaData.wahlArt);

        allStapelForWahlart.forEach((stapelArt) => {
          const skipStapelObwLeerIfWahlbezirksArtIsUwb =
            stapelArt === StapelArtEnum.ObwBLeer &&
            taskFactoryContext.wahlbezirkArt === WahlbezirksArtEnum.UWB;

          if (!skipStapelObwLeerIfWahlbezirksArtIsUwb) {
            tasksForWahlen.push(_createTask(metaData, stapelArt));
          }
        });
      });

    return tasksForWahlen;
  }

  function _createTask(
    taskFactoryMetaData: ExtendedWahlMetaData,
    stapelArt: StapelArtEnum
  ): Task {
    return {
      callback: () =>
        loadErgebnisseByStapelArt(taskFactoryMetaData.wahlID, stapelArt, false),
      name: `Stapel - ${stapelArt} für ${taskFactoryMetaData.wahlName}`,
    };
  }

  return whenUserIsSchriftfuehrung(createTasks);
}
