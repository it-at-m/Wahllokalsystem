import type { TaskFactory } from "@/composables/tasks/TaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import type { Task } from "@/types/tasks/Task.ts";

import { useBedenklicheStimmzettelService } from "@/composables/ergebnismeldung/MBW/bedenklicheStimmzettelService.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const { getBedenklicheStimmzettel } = useBedenklicheStimmzettelService();

export function useStapelETaskFactory(): TaskFactory {
  function createTasks(taskFactoryContext: TaskFactoryContext): Task[] {
    if (!taskFactoryContext.isSchriftfuehrung) {
      return [];
    }

    const mbws = taskFactoryContext.extendedWahlMetaData.filter(
      (metaData) => metaData.wahlArt === WahlWahlartEnum.Mbw
    );

    return mbws.map((mbwMetaData) => ({
      name: `Stapel E für ${mbwMetaData.wahlName}`,
      callback: () =>
        getBedenklicheStimmzettel(
          mbwMetaData.wahlID,
          mbwMetaData.wahlbezirkID,
          false
        ),
    }));
  }

  return {
    createTasks,
  };
}
