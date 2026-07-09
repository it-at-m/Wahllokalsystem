import type { ExtendedWahlMetaData } from "@/composables/tasks/ExtendedWahlMetaData.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";

import { storeToRefs } from "pinia";

import { useAWerteTaskFactory } from "@/composables/tasks/taskFactories/aWerteTaskFactory.ts";
import { useBeanstandeteWahlbriefeTaskFactory } from "@/composables/tasks/taskFactories/beanstandeteWahlbriefeTaskFactory.ts";
import { useBegruendungTaskFactory } from "@/composables/tasks/taskFactories/begruendungTaskFactory.ts";
import { useEreignisseTaskFactory } from "@/composables/tasks/taskFactories/ereignisseTaskFactory.ts";
import { useErgebnisseTaskFactory } from "@/composables/tasks/taskFactories/ergebnisseTaskFactory.ts";
import { useEroeffnungsuhrzeitTaskFactory } from "@/composables/tasks/taskFactories/eroeffnungsuhrzeitTaskFactory.ts";
import { useHandbuchTaskFactory } from "@/composables/tasks/taskFactories/handbuchTaskFactory.ts";
import { useKonfigurationsparameterTaskFactory } from "@/composables/tasks/taskFactories/konfigurationsparameterTaskFactory.ts";
import { useMBWWahlvorschlaegeAndErgebnisseTaskFactory } from "@/composables/tasks/taskFactories/mbwWahlvorschlaegeAndErgebnisseTaskFactory.ts";
import { useStapelETaskFactory } from "@/composables/tasks/taskFactories/stapelETaskFactory.ts";
import { useStatusTaskFactory } from "@/composables/tasks/taskFactories/statusTaskFactory.ts";
import { useStimmabgabevermerkeTaskFactory } from "@/composables/tasks/taskFactories/stimmabgabevermerkeTaskFactory.ts";
import { useStimmzettelumschlaegeTaskFactory } from "@/composables/tasks/taskFactories/stimmzettelumschlaegeTaskFactory.ts";
import { useUngueltigeWahlscheineTaskFactory } from "@/composables/tasks/taskFactories/ungueltigeWahlscheineTaskFactory.ts";
import { useUrnenwahlSchliessungsuhrzeitTaskFactory } from "@/composables/tasks/taskFactories/urnenwahlSchliessungsuhrzeitTaskFactory.ts";
import { useWaehlerTaskFactory } from "@/composables/tasks/taskFactories/waehlerTaskFactory.ts";
import { useWaehlverzeichnisTaskFactory } from "@/composables/tasks/taskFactories/waehlverzeichnisTaskFactory.ts";
import { useWahlbriefeTaskFactory } from "@/composables/tasks/taskFactories/wahlbriefeTaskFactory.ts";
import { useWahlscheineTaskFactory } from "@/composables/tasks/taskFactories/wahlscheineTaskFactory.ts";
import { useWahlvorbereitungTaskFactory } from "@/composables/tasks/taskFactories/wahlvorbereitungTaskFactory.ts";
import { useWahlvorschlaegeTaskFactory } from "@/composables/tasks/taskFactories/wahlvorschlaegeTaskFactory.ts";
import { useWahlvorstandTaskFactory } from "@/composables/tasks/taskFactories/wahlvorstandTaskFactory.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

export function useTaskListService() {
  const { wahlenActions, waehlerverzeichnisActions } = useWahlenStore();
  const {
    currentUserWahlMetadata,
    currentUserWahlbezirksArt,
    hasRoleSchriftfuehrung,
    hasRoleErfassungsteam,
  } = storeToRefs(useUserStore());

  const { createTasks: createWahlvorstandTasks } = useWahlvorstandTaskFactory();
  const { createTasks: createEroeffnungsuhrzeitTasks } =
    useEroeffnungsuhrzeitTaskFactory();
  const { createTasks: createUrnenwahlSchliessungsuhrzeitTasks } =
    useUrnenwahlSchliessungsuhrzeitTaskFactory();
  const { createTasks: createKonfigurationsparameterTasks } =
    useKonfigurationsparameterTaskFactory();
  const { createTasks: createUngueltigeWahlscheineTasks } =
    useUngueltigeWahlscheineTaskFactory();
  const { createTasks: createWahlscheineTasks } = useWahlscheineTaskFactory();
  const { createTasks: createWaehlerverzeichnisTasks } =
    useWaehlverzeichnisTaskFactory();
  const { createTasks: createWahlvorschlaegeTasks } =
    useWahlvorschlaegeTaskFactory();
  const { createTasks: createStimmabgabevermerkeTasks } =
    useStimmabgabevermerkeTaskFactory();
  const { createTasks: createErgebnisseTasks } = useErgebnisseTaskFactory();
  const { createTasks: createStimmzettelumschlaegeTasks } =
    useStimmzettelumschlaegeTaskFactory();
  const { createTasks: createWaehlerTasks } = useWaehlerTaskFactory();
  const { createTasks: createEreignisseTasks } = useEreignisseTaskFactory();
  const { createTasks: createBegruendungTasks } = useBegruendungTaskFactory();
  const { createTasks: createAWerteTasks } = useAWerteTaskFactory();
  const { createTasks: createHandbuchTasks } = useHandbuchTaskFactory();
  const { createTasks: createStatusTasks } = useStatusTaskFactory();
  const { createTasks: createWahlvorbereitungTasks } =
    useWahlvorbereitungTaskFactory();
  const { createTasks: createWahlbriefeTasks } = useWahlbriefeTaskFactory();
  const { createTasks: createBeanstandeteWahlbriefeTask } =
    useBeanstandeteWahlbriefeTaskFactory();
  const { createTasks: createMbwWahlvorschlaegeAndErgebnisseTasks } =
    useMBWWahlvorschlaegeAndErgebnisseTaskFactory();
  const { createTasks: createStapelETasks } = useStapelETaskFactory();

  function initTasklist() {
    const taskFactoryData = _createTaskFactoryData();
    return [
      ...createWaehlerverzeichnisTasks(taskFactoryData),
      ...createUngueltigeWahlscheineTasks(taskFactoryData),
      ...createWahlvorstandTasks(taskFactoryData),
      ...createEroeffnungsuhrzeitTasks(taskFactoryData),
      ...createUrnenwahlSchliessungsuhrzeitTasks(taskFactoryData),
      ...createKonfigurationsparameterTasks(taskFactoryData),
      ...createWahlscheineTasks(taskFactoryData),
      ...createWahlvorschlaegeTasks(taskFactoryData),
      ...createErgebnisseTasks(taskFactoryData),
      ...createEreignisseTasks(taskFactoryData),
      ...createStimmabgabevermerkeTasks(taskFactoryData),
      ...createStimmzettelumschlaegeTasks(taskFactoryData),
      ...createWaehlerTasks(taskFactoryData),
      ...createBegruendungTasks(taskFactoryData),
      ...createAWerteTasks(taskFactoryData),
      ...createHandbuchTasks(taskFactoryData),
      ...createStatusTasks(taskFactoryData),
      ...createWahlvorbereitungTasks(taskFactoryData),
      ...createWahlbriefeTasks(taskFactoryData),
      ...createBeanstandeteWahlbriefeTask(taskFactoryData),
      ...createMbwWahlvorschlaegeAndErgebnisseTasks(taskFactoryData),
      ...createStapelETasks(taskFactoryData),
    ];
  }

  function _createTaskFactoryData(): TaskFactoryContext {
    const extendedWahlMetaData: ExtendedWahlMetaData[] =
      currentUserWahlMetadata.value.map((wahlMetadata) => {
        const wahl = wahlenActions.getWahlOrUndefinedById(wahlMetadata.wahlID);
        const waehlerverzeichnisNummer =
          waehlerverzeichnisActions.getWaehlerverzeichnisNummerOrUndefinedById(
            wahlMetadata.wahlID
          );
        if (!wahl || !waehlerverzeichnisNummer) {
          throw new Error(`Wahl not found for wahlID: ${wahlMetadata.wahlID}`);
        }
        const extendedWahlMetaData: ExtendedWahlMetaData = {
          wahlID: wahlMetadata.wahlID,
          wahlArt: wahl.wahlart,
          wahlbezirkID: wahlMetadata.wahlbezirkID,
          wahlName: wahl.name,
          wahlnummer: wahlMetadata.wahlnummer,
          waehlerverzeichnisNummer: waehlerverzeichnisNummer,
        };
        return extendedWahlMetaData;
      });
    return {
      wahlbezirkArt: currentUserWahlbezirksArt.value,
      isErfassungsteam: hasRoleErfassungsteam.value,
      isSchriftfuehrung: hasRoleSchriftfuehrung.value,
      extendedWahlMetaData: extendedWahlMetaData,
    };
  }

  return {
    initTasklist,
  };
}
