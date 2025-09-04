import { defineStore } from "pinia";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useTaskListService } from "@/composables/tasks/taskListService.ts";
import { useTaskManager } from "@/composables/tasks/taskManager.ts";

const storeID = "taskManager";
const { registerStoreHMR } = useHmrUpdate();

export const useTaskManagerStore = defineStore(storeID, () => {
  const { initTasklist } = useTaskListService();
  const {
    currentlyRunningTask,
    failedTasks,
    hasAllTasksRun,
    hasAllTasksRunSuccessfully,
    numberOfTasksFailed,
    numberOfTasksFinished,
    numberOfTasksSucceeded,
    numberOfTasksToRun,
    successfullyTasks,
    setTasks,
    runAllTasks,
  } = useTaskManager([]);

  async function initTasks() {
    const taskList = initTasklist();
    setTasks(taskList);
    await runAllTasks();
  }

  return {
    hasAllTasksRun,
    initTasks,
    numberOfTasksToRun,
    numberOfTasksFailed,
    numberOfTasksFinished,
    numberOfTasksSucceeded,
    currentlyRunningTask,
    successfullyTasks,
    failedTasks,
    hasAllTasksRunSuccessfully,
  };
});

registerStoreHMR(useTaskManagerStore);
