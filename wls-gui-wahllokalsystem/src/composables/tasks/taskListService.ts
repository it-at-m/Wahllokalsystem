import type { Task } from "@/types/Task.ts";

import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

export function useTaskListService() {
  const wahlenStore = useWahlenStore();

  function getTaskList(): Task[] {
    return [
      {
        name: "Wahlen",
        wahlbezirksart: undefined,
        onlyForWahlen: [
          WahlWahlartEnum.Obw,
          WahlWahlartEnum.Bzw,
          WahlWahlartEnum.Srw,
        ],
        onlyForAllWVZs: undefined,
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
