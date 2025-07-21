import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useKonfigurationsparameterTaskFactory } from "@/composables/tasks/taskFactories/konfigurationsparameterTaskFactory.ts";
import { useKopfdatenTaskFactory } from "@/composables/tasks/taskFactories/kopfdatenTaskFactory.ts";
import { useUngueltigeWahlscheineTaskFactory } from "@/composables/tasks/taskFactories/ungueltigeWahlscheineTaskFactory.ts";
import { useWahlvorstandTaskFactory } from "@/composables/tasks/taskFactories/wahlvorstandTaskFactory.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

export function useTaskListService() {
  const { getWahlOrUndefinedById } = useWahlenStore();
  const { currentUserWahlMetadata } = storeToRefs(useUserStore());
  const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());

  const { createTasks: createKopfdatenTasks } = useKopfdatenTaskFactory();
  const { createTasks: createWahlvorstandTasks } = useWahlvorstandTaskFactory();
  const { createTasks: createKonfigurationsparameterTasks } =
    useKonfigurationsparameterTaskFactory();
  const { createTasks: createUngueltigeWahlscheineTasks } =
    useUngueltigeWahlscheineTaskFactory();

  function initTasklist() {
    const tasks = [];
    tasks.push(...initKopfdatenTaskList());
    tasks.push(...initUngueltigeWahlscheineTaskList());
    tasks.push(...initWahlvorstandTaskList());
    tasks.push(...initKonfigurationsparameterTaskList());
    return tasks;
  }

  const taskFactoryData = computed(() => {
    const extendedWahlMetaData: ExtendedWahlMetaData[] =
      currentUserWahlMetadata.value.map((wahlMetadata) => {
        const wahl = getWahlOrUndefinedById(wahlMetadata.wahlID);
        if (!wahl) {
          throw new Error(`Wahl not found for wahlID: ${wahlMetadata.wahlID}`);
        }
        const extendedWahlMetaData: ExtendedWahlMetaData = {
          wahlID: wahlMetadata.wahlID,
          wahlArt: wahl.wahlart,
          wahlbezirkID: wahlMetadata.wahlbezirkID,
          wahlName: wahl.name,
          wahlnummer: wahlMetadata.wahlnummer,
        };
        return extendedWahlMetaData;
      });
    return {
      wahlbezirkArt: currentUserWahlbezirksArt.value,
      extendedWahlMetaData: extendedWahlMetaData,
    };
  });

  function initKopfdatenTaskList(): Task[] {
    return createKopfdatenTasks(taskFactoryData.value);
  }

  function initUngueltigeWahlscheineTaskList(): Task[] {
    return createUngueltigeWahlscheineTasks(taskFactoryData.value);
  }

  function initWahlvorstandTaskList(): Task[] {
    return createWahlvorstandTasks(taskFactoryData.value);
  }

  function initKonfigurationsparameterTaskList(): Task[] {
    return createKonfigurationsparameterTasks(taskFactoryData.value);
  }

  return {
    initTasklist,
  };
}
