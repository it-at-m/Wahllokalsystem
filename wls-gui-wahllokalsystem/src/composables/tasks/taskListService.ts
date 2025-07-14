import type { Task } from "@/types/tasks/Task.ts";

import { storeToRefs } from "pinia";

import { KopfdatenTaskFactoryImpl } from "@/composables/tasks/KopfdatenTaskFactoryImpl.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useTaskListService() {
  const { initWahlen, getWahlNameOrBlankStringById } = useWahlenStore();
  const { initKonfigurationsparameter } = useInfomanagementStore();
  const { initWahlvorstand } = useWahlvorstandStore();
  const { initUngueltigeWahlscheine } = useWahlbezirkStore();
  const { initKopfdaten } = useKopfdatenStore();
  const { currentUserWahlMetadata } = useUserStore();

  const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());

  const matchingWahlbezirkArt = (task: Task) =>
    task.onlyForWahlbezirksart === undefined ||
    task.onlyForWahlbezirksart === currentUserWahlbezirksArt.value;

  function getTaskList(): Task[] {
    return _tasks.filter(matchingWahlbezirkArt);
  }

  function initTasklist() {
    initKopfdatenTaskList();
  }

  function initKopfdatenTaskList(): Task[] {
    const kopfdatenFactory = new KopfdatenTaskFactoryImpl();
    const taskList: Task[] = [];
    currentUserWahlMetadata.forEach((wahlMetaData) => {
      taskList.push(
        kopfdatenFactory.createTask(
          wahlMetaData,
          getWahlNameOrBlankStringById(wahlMetaData.wahlID)
        )
      );
    });
    return taskList;
  }

  const _tasks: Task[] = [
    {
      name: "Konfigurationsparameter",
      onlyForWahlbezirksart: undefined,
      onlyForWahlen: undefined,
      onlyForAllWVaehlerverzeichnisse: undefined,
      callback: () => {
        return initKonfigurationsparameter(false);
      },
    },
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
      name: "Wahlvorstand",
      onlyForWahlbezirksart: undefined,
      onlyForWahlen: undefined,
      onlyForAllWVaehlerverzeichnisse: undefined,
      callback: () => {
        return initWahlvorstand(false);
      },
    },
    {
      name: "UngültigeWahlscheine",
      onlyForWahlbezirksart: WahlbezirksArtEnum.UWB,
      onlyForWahlen: [
        WahlWahlartEnum.Btw,
        WahlWahlartEnum.Beb,
        WahlWahlartEnum.Euw,
        WahlWahlartEnum.Obw,
        WahlWahlartEnum.Srw,
        WahlWahlartEnum.Baw,
      ],
      onlyForAllWVaehlerverzeichnisse: undefined,
      callback: () => initUngueltigeWahlscheine(false),
    },
    {
      name: "Kopfdaten-xy",
      onlyForWahlbezirksart: undefined,
      onlyForWahlen: undefined,
      onlyForAllWVaehlerverzeichnisse: undefined,
      callback: () => {
        return initKopfdaten();
      },
    },
  ];

  return {
    getTaskList,
  };
}
