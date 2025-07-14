import type { TaskFactoryInterface } from "@/composables/tasks/TaskFactoryInterface.ts";
import type { Task } from "@/types/tasks/Task.ts";
import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";

import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";

export class KopfdatenTaskFactoryImpl implements TaskFactoryInterface {
  createTask(wahlMetaData: WahlMetaData, wahlName: string): Task {
    const { loadKopfdaten } = useKopfdatenStore();
    return {
      callback: () =>
        loadKopfdaten(wahlMetaData.wahlID, wahlMetaData.wahlbezirkID),
      name: wahlName,
      onlyForAllWVaehlerverzeichnisse: undefined,
      onlyForWahlbezirksart: undefined,
      onlyForWahlen: undefined,
    };
  }
}
