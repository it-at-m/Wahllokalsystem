import type { Task } from "@/types/Task.ts";
import type { Ref } from "vue";

import { computed, ref } from "vue";

import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/wahlWahlartEnum.ts";

export function useTaskManager() {
  const wahlStore = useWahlenStore();
  const currentlyRunningTask = ref<null | Task>(null);
  const failedTasks: Ref<Task[]> = ref([]);
  const successfullyTasks: Ref<Task[]> = ref([]);
  const numberOfTasksToRun = ref(0);
  const numberOfFailedTasks = computed(() => {
    return failedTasks.value.length;
  });
  const numberOfSuccessfullTasks = computed(() => {
    return successfullyTasks.value.length;
  });

  const taskList: Task[] = [
    {
      name: "test",
      wahlbezirksart: "alle",
      forWahlen: [
        WahlWahlartEnum.Obw,
        WahlWahlartEnum.Bzw,
        WahlWahlartEnum.Srw,
      ],
      forAllWVZs: undefined,
      callback: () => {
        return wahlStore.loadWahlen();
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
    numberOfSuccessfullTasks,
    numberOfFailedTasks,
    numberOfTasksToRun,
    currentlyRunningTask,
  };
}
