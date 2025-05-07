import type { Task } from "@/types/Task.ts";
import type { Ref } from "vue";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

import { useWahlStore } from "@/stores/wahlStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/wahlWahlartEnum.ts";

const storeID = "taskManager";
export const useTaskManagerStore = defineStore(storeID, () => {
  const wahlStore = useWahlStore();
  const currentlyRunningTask = ref<null | Task>(null);
  const failedTasks: Ref<Task[]> = ref([]);
  const successfullyTasks: Ref<Task[]> = ref([]);
  const numberOfTasksToRun = ref(0);
  const numberOfFailedTasks = computed(() => {
    return failedTasks.value.length;
  });
  const numberOfSuccessfulTasks = computed(() => {
    return successfullyTasks.value.length;
  });

  const taskList: Task[] = [
    {
      name: "Wahlen",
      wahlbezirksart: undefined,
      forWahlen: [
        WahlWahlartEnum.Obw,
        WahlWahlartEnum.Bzw,
        WahlWahlartEnum.Srw,
      ],
      forAllWVZs: undefined,
      callback: () => {
        return wahlStore.loadWahlen(false);
      },
    },
  ];

  async function initTasks() {
    numberOfTasksToRun.value = taskList.length;
    for (const task of taskList) {
      currentlyRunningTask.value = task;
      try {
        await task.callback();
        successfullyTasks.value.push(task);
      } catch (error) {
        failedTasks.value.push(task);
      }
    }
    currentlyRunningTask.value = null;
  }

  return {
    initTasks,
    numberOfSuccessfulTasks,
    numberOfFailedTasks,
    numberOfTasksToRun,
    currentlyRunningTask,
    successfullyTasks,
    failedTasks,
  };
});
