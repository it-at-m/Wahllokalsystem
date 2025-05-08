import type { Task } from "@/types/Task.ts";
import type { Ref } from "vue";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

import { useTaskListService } from "@/composables/tasks/taskListService.ts";

const storeID = "taskManager";
export const useTaskManagerStore = defineStore(storeID, () => {
  const taskListService = useTaskListService();
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

  const hasInitializationOfTasksCompletelyRun = computed(
    () =>
      numberOfTasksToRun.value > 0 &&
      numberOfTasksToRun.value ==
        numberOfSuccessfulTasks.value + numberOfFailedTasks.value
  );

  async function initTasks() {
    const taskList = taskListService.getTaskList();
    numberOfTasksToRun.value = taskList.length;
    for (const task of taskList) {
      currentlyRunningTask.value = task;
      try {
        await task.callback();
        successfullyTasks.value.push(task);
      } catch {
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
    hasInitializationOfTasksCompletelyRun,
  };
});
