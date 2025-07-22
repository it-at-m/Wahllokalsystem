import type { Task } from "@/types/tasks/Task.ts";

export function useTasksTestDataFactory() {
  function createTask(name: string): Task {
    return {
      onlyForWahlbezirksart: undefined,
      onlyForWahlen: undefined,
      onlyForAllWVaehlerverzeichnisse: undefined,
      name: name,
      callback: () => {
        return Promise.resolve();
      },
    };
  }

  return { createTask };
}
