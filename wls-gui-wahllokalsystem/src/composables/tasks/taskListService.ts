import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";

import { storeToRefs } from "pinia";

import { useKonfigurationsparameterTaskFactory } from "@/composables/tasks/taskFactories/konfigurationsparameterTaskFactory.ts";
import { useKopfdatenTaskFactory } from "@/composables/tasks/taskFactories/kopfdatenTaskFactory.ts";
import { useStimmabgabevermerkeTaskFactory } from "@/composables/tasks/taskFactories/stimmabgabevermerkeTaskFactory.ts";
import { useUngueltigeWahlscheineTaskFactory } from "@/composables/tasks/taskFactories/ungueltigeWahlscheineTaskFactory.ts";
import { useWahlscheineTaskFactory } from "@/composables/tasks/taskFactories/wahlscheineTaskFactory.ts";
import { useWahlvorstandTaskFactory } from "@/composables/tasks/taskFactories/wahlvorstandTaskFactory.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

export function useTaskListService() {
  const { getWahlOrUndefinedById, getWaehlerverzeichnisNummerOrUndefinedById } =
    useWahlenStore();
  const { currentUserWahlMetadata } = storeToRefs(useUserStore());
  const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());

  const { createTasks: createKopfdatenTasks } = useKopfdatenTaskFactory();
  const { createTasks: createWahlvorstandTasks } = useWahlvorstandTaskFactory();
  const { createTasks: createKonfigurationsparameterTasks } =
    useKonfigurationsparameterTaskFactory();
  const { createTasks: createUngueltigeWahlscheineTasks } =
    useUngueltigeWahlscheineTaskFactory();
  const { createTasks: createWahlscheineTasks } = useWahlscheineTaskFactory();
  const { createTasks: createStimmabgabevermerkeTasks } =
    useStimmabgabevermerkeTaskFactory();

  function initTasklist() {
    const taskFactoryData = _createTaskFactoryData();
    const tasks = [];
    tasks.push(...createKopfdatenTasks(taskFactoryData));
    tasks.push(...createUngueltigeWahlscheineTasks(taskFactoryData));
    tasks.push(...createWahlvorstandTasks(taskFactoryData));
    tasks.push(...createKonfigurationsparameterTasks(taskFactoryData));
    tasks.push(...createWahlscheineTasks(taskFactoryData));
    tasks.push(...createStimmabgabevermerkeTasks(taskFactoryData));
    return tasks;
  }

  function _createTaskFactoryData() {
    const extendedWahlMetaData: ExtendedWahlMetaData[] =
      currentUserWahlMetadata.value.map((wahlMetadata) => {
        const wahl = getWahlOrUndefinedById(wahlMetadata.wahlID);
        const waehlerverzeichnisNummer =
          getWaehlerverzeichnisNummerOrUndefinedById(wahlMetadata.wahlID);
        if (!wahl || !waehlerverzeichnisNummer) {
          throw new Error(`Wahl not found for wahlID: ${wahlMetadata.wahlID}`);
        }
        const extendedWahlMetaData: ExtendedWahlMetaData = {
          wahlID: wahlMetadata.wahlID,
          wahlArt: wahl.wahlart,
          wahlbezirkID: wahlMetadata.wahlbezirkID,
          wahlName: wahl.name,
          wahlnummer: wahlMetadata.wahlnummer,
          waehlerverzeichnisNummer: waehlerverzeichnisNummer,
        };
        return extendedWahlMetaData;
      });
    return {
      wahlbezirkArt: currentUserWahlbezirksArt.value,
      extendedWahlMetaData: extendedWahlMetaData,
    };
  }

  return {
    initTasklist,
  };
}
