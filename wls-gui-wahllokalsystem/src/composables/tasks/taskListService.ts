import type { Task } from "@/types/tasks/Task.ts";

import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

export function useTaskListService() {
  const wahlenStore = useWahlenStore();

  function getTaskList(): Task[] {
    return [
      {
        name: "Wahlen",
        onlyForWahlbezirksart: undefined,
        onlyForWahlen: [
          WahlWahlartEnum.Obw,
          WahlWahlartEnum.Bzw,
          WahlWahlartEnum.Srw,
        ],
        onlyForAllWVaehlerverzeichnisse: undefined,
        callback: () => {
          return wahlenStore.initWahlen(false);
        },
      },
    ];
  }

  return {
    getTaskList,
  };
}
