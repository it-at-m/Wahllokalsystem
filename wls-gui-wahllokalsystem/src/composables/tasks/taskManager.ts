import type { Task } from "@/types/tasks/Task.ts";
import type { Ref } from "vue";

import { computed, ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";

const { logDebug, logError } = useLogging("taskManager");

export function useTaskManager(tasksToManage: Task[] = []) {
  const managedTasks: Ref<Task[]> = ref(tasksToManage);

  const currentlyRunningTask = ref<null | Task>(null);

  const failedTasks: Ref<Task[]> = ref([]);
  const successfullyTasks: Ref<Task[]> = ref([]);

  const numberOfTasksToRun = computed(() => managedTasks.value.length);
  const numberOfTasksFinished = computed(
    () => failedTasks.value.length + successfullyTasks.value.length
  );
  const numberOfTasksFailed = computed(() => failedTasks.value.length);
  const numberOfTasksSucceeded = computed(() => successfullyTasks.value.length);

  const hasAllTasksRun = computed(
    () => numberOfTasksToRun.value === numberOfTasksFinished.value
  );
  const hasAllTasksRunSuccessfully = computed(
    () => hasAllTasksRun.value && failedTasks.value.length === 0
  );
  const hasTasksToRun = computed(() => managedTasks.value.length > 0);

  function setTasks(tasks: Task[]) {
    managedTasks.value = tasks;
    _resetTaskRunResults();
  }

  async function runAllTasks() {
    _resetTaskRunResults();

    for (const task of managedTasks.value) {
      await _runTask(task);
    }

    logDebug(`all tasks completed`);
    currentlyRunningTask.value = null;
  }

  async function rerunFailedTasks() {
    const failedTasksToRerun = failedTasks.value;
    failedTasks.value = [];
    for (const task of failedTasksToRerun) {
      await _runTask(task);
    }

    logDebug(`rerun with failed tasks completed`);
    currentlyRunningTask.value = null;
  }

  async function _runTask(task: Task) {
    currentlyRunningTask.value = task;
    try {
      logDebug(`running task ${task.name}`);
      await task.callback();
      successfullyTasks.value.push(task);
    } catch {
      logError(`failed to run task "${task.name}"`);
      failedTasks.value.push(task);
    }
  }

  function _resetTaskRunResults() {
    failedTasks.value = [];
    successfullyTasks.value = [];
  }

  return {
    currentlyRunningTask,
    failedTasks,
    hasAllTasksRun,
    hasAllTasksRunSuccessfully,
    hasTasksToRun,
    successfullyTasks,
    numberOfTasksFailed,
    numberOfTasksFinished,
    numberOfTasksSucceeded,
    numberOfTasksToRun,
    runAllTasks,
    setTasks,
    rerunFailedTasks,
  };
}
