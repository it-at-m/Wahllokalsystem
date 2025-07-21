import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useKonfigurationsparameterTaskFactory } from "@/composables/tasks/taskFactories/KonfigurationsparameterTaskFactory.ts";
import { useKopfdatenTaskFactory } from "@/composables/tasks/taskFactories/KopfdatenTaskFactory.ts";
import { useUngueltigeWahlscheineTaskFactory } from "@/composables/tasks/taskFactories/UngueltigeWahlscheineTaskFactory.ts";
import { useWahlvorstandTaskFactory } from "@/composables/tasks/taskFactories/WahlvorstandTaskFactory.ts";
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

  const tasks = ref<Task[]>([]);

  function getTaskList(): Task[] {
    return tasks.value;
  }

  function initTasklist() {
    tasks.value.push(...initKopfdatenTaskList());
    tasks.value.push(...initUngueltigeWahlscheineTaskList());
    tasks.value.push(...initWahlvorstandTaskList());
    tasks.value.push(...initKonfigurationsparameterTaskList());
  }

  const taskFactoryData = computed(() => {
    const extendedWahlMetaData: ExtendedWahlMetaData[] =
      currentUserWahlMetadata.value.map((wahlMetadata) => {
        const wahl = getWahlOrUndefinedById(wahlMetadata.wahlID);
        if (!wahl) {
          throw new Error("Wahl not found");
        }
        const extendedWahlMetaData: ExtendedWahlMetaData = {
          wahlID: wahlMetadata.wahlID,
          wahlart: wahl.wahlart,
          wahlbezirkID: wahlMetadata.wahlbezirkID,
          wahlname: wahl.name,
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
    getTaskList,
    tasks,
  };
}
