import type { Task } from "@/types/tasks/Task.ts";

import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

export function useTaskListService() {
  const { initWahlen } = useWahlenStore();
  const { initKonfigurationsparameter } = useInfomanagementStore();

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
          return initWahlen(false);
        },
      },
      {
        name: "Konfigurationsparameter",
        // todo: welche konfig muss hier rein?
        onlyForWahlbezirksart: undefined,
        onlyForWahlen: [
          WahlWahlartEnum.Obw,
          WahlWahlartEnum.Bzw,
          WahlWahlartEnum.Srw,
        ],
        onlyForAllWVaehlerverzeichnisse: undefined,
        callback: () => {
          return initKonfigurationsparameter(false);
        },
      },
    ];
  }

  return {
    getTaskList,
  };
}
