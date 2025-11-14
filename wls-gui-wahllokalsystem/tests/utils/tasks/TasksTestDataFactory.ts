import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";

import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";
import { useCommonTestDataFactory } from "../common/CommonTestDataFactory";

const { generateRandomString, generateRandomNumber } =
  useCommonTestDataFactory();

export function useTasksTestDataFactory() {
  function createTask(name: string): Task {
    return {
      name: name,
      callback: () => {
        return Promise.resolve();
      },
    };
  }

  function createTaskFactoryContext(): TaskFactoryContext {
    return {
      extendedWahlMetaData: [createExtendedWahlMetaData()],
      wahlbezirkArt: WahlbezirksArtEnum.BWB,
    };
  }

  function createExtendedWahlMetaData(): ExtendedWahlMetaData {
    return {
      wahlArt: WahlWahlartEnum.Baw,
      wahlnummer: generateRandomString(10),
      wahlName: generateRandomString(5),
      wahlID: generateRandomString(20),
      wahlbezirkID: generateRandomString(20),
      waehlerverzeichnisNummer: generateRandomNumber(2),
    };
  }

  function prepareTask() {
    return proxyBuilder<Task>(createTask(generateRandomString(10)));
  }

  function prepareTaskFactoryContext(): Builder<TaskFactoryContext> {
    return proxyBuilder<TaskFactoryContext>(createTaskFactoryContext());
  }

  function prepareExtendedWahlMetaData(): Builder<ExtendedWahlMetaData> {
    return proxyBuilder<ExtendedWahlMetaData>(createExtendedWahlMetaData());
  }

  return {
    createTask,
    createTaskFactoryContext,
    prepareTaskFactoryContext,
    createExtendedWahlMetaData,
    prepareTask,
    prepareExtendedWahlMetaData,
  };
}
