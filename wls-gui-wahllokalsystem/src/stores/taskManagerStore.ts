import { defineStore } from "pinia";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useTaskListService } from "@/composables/tasks/taskListService.ts";
import { useTaskManager } from "@/composables/tasks/taskManager.ts";

const storeID = "taskManager";
const { registerStoreHMR } = useHmrUpdate();

export const useTaskManagerStore = defineStore(storeID, () => {
  const { initTasklist } = useTaskListService();
  const taskManager = useTaskManager([]);

  async function initTasks() {
    const taskList = initTasklist();
    taskManager.setTasks(taskList);
    await taskManager.runAllTasks();
  }

  return {
    ...taskManager,
    initTasks,
  };
});

registerStoreHMR(useTaskManagerStore);
