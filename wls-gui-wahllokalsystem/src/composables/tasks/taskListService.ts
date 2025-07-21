import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { KonfigurationsparameterTaskFactoryImpl } from "@/composables/tasks/taskFactories/KonfigurationsparameterTaskFactoryImpl.ts";
import { KopfdatenTaskFactoryImpl } from "@/composables/tasks/taskFactories/KopfdatenTaskFactoryImpl.ts";
import { UngueltigeWahlscheineTaskFactoryImpl } from "@/composables/tasks/taskFactories/UngueltigeWahlscheineTaskFactoryImpl.ts";
import { WahlvorstandTaskFactoryImpl } from "@/composables/tasks/taskFactories/WahlvorstandTaskFactoryImpl.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

export function useTaskListService() {
  const { getWahlNameOrBlankStringById, getWahlOrUndefinedById } =
    useWahlenStore();
  const { currentUserWahlMetadata } = storeToRefs(useUserStore());

  const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());

  const _tasks = ref<Task[]>([]);

  function getTaskList(): Task[] {
    return _tasks.value;
  }

  function initTasklist() {
    _tasks.value.push(...initKopfdatenTaskList());
    _tasks.value.push(...initUngueltigeWahlscheineTaskList());
    _tasks.value.push(...initWahlvorstandTaskList());
    _tasks.value.push(...initKonfigurationsparameterTaskList());
  }

  const taskFactoryData = computed(() => {
    const taskFactoryMetaData: ExtendedWahlMetaData[] =
      currentUserWahlMetadata.value.map((wahlMetadata) => {
        const wahl = getWahlOrUndefinedById(wahlMetadata.wahlID);
        if (!wahl) {
          throw new Error("Wahl not found");
        }
        const factoryMetaData: ExtendedWahlMetaData = {
          wahlID: wahlMetadata.wahlID,
          wahlart: wahl.wahlart,
          wahlbezirkID: wahlMetadata.wahlbezirkID,
          wahlname: wahl.name,
          wahlnummer: wahlMetadata.wahlnummer,
        };
        return factoryMetaData;
      });
    return {
      wahlbezirkArt: currentUserWahlbezirksArt.value,
      taskFactoryMetaData: taskFactoryMetaData,
    };
  });

  function initKopfdatenTaskList(): Task[] {
    const kopfdatenFactory = new KopfdatenTaskFactoryImpl();
    return kopfdatenFactory.createTasks(taskFactoryData.value);
  }

  function initUngueltigeWahlscheineTaskList(): Task[] {
    const ungueltigeWahlscheineTaskFactory =
      new UngueltigeWahlscheineTaskFactoryImpl();
    return ungueltigeWahlscheineTaskFactory.createTasks(taskFactoryData.value);
  }

  function initWahlvorstandTaskList(): Task[] {
    const WahlvorstandTaskFactory = new WahlvorstandTaskFactoryImpl();
    return WahlvorstandTaskFactory.createTasks(taskFactoryData.value);
  }

  function initKonfigurationsparameterTaskList(): Task[] {
    const KonfigurationsparameterTaskFactory =
      new KonfigurationsparameterTaskFactoryImpl();
    return KonfigurationsparameterTaskFactory.createTasks(
      taskFactoryData.value
    );
  }

  return {
    initTasklist,
    getTaskList,
  };
}
