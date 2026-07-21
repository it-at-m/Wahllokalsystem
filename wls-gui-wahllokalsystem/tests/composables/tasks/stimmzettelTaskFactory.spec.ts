import { useStimmzettelTaskFactory } from "@/composables/tasks/taskFactories/stimmzettelTaskFactory.ts";
import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";
import { describe, expect, it, vi } from "vitest";

describe("stimmzettelTaskFactory.ts", () => {
  const { createTasks } = useStimmzettelTaskFactory();

  describe("createTasks", () => {
    it("should_createOneTaskPerWahl_when_contextHasMultipleWahlen", () => {
      const mockTaskFactoryContext: TaskFactoryContext = {
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        isErfassungsteam: false,
        isSchriftfuehrung: true,
        extendedWahlMetaData: [
          {
            wahlID: "wahl-1",
            wahlArt: "OBW" as any,
            wahlbezirkID: "bezirk-1",
            wahlName: "Oberbürgermeister",
            wahlnummer: "1",
            waehlerverzeichnisNummer: 1,
          },
          {
            wahlID: "wahl-2",
            wahlArt: "KW" as any,
            wahlbezirkID: "bezirk-1",
            wahlName: "Kreistag",
            wahlnummer: "2",
            waehlerverzeichnisNummer: 1,
          },
        ],
      };

      const tasks = createTasks(mockTaskFactoryContext);

      expect(tasks).toHaveLength(2);
      expect(tasks[0].name).toContain("Oberbürgermeister");
      expect(tasks[1].name).toContain("Kreistag");
    });

    it("should_returnEmptyArray_when_userIsNotSchriftfuehrung", () => {
      const mockTaskFactoryContext: TaskFactoryContext = {
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        isErfassungsteam: true,
        isSchriftfuehrung: false,
        extendedWahlMetaData: [
          {
            wahlID: "wahl-1",
            wahlArt: "OBW" as any,
            wahlbezirkID: "bezirk-1",
            wahlName: "Oberbürgermeister",
            wahlnummer: 1,
            waehlerverzeichnisNummer: 1,
          },
        ],
      };

      const tasks = createTasks(mockTaskFactoryContext);

      expect(tasks).toHaveLength(0);
    });

    it("should_callLoadStimmabgabevermerke_when_taskCallbackIsExecuted", async () => {
      const { loadStimmabgabevermerke } = useStimmabgabevermerkeStore();
      const mockLoad = vi.spyOn(
        useStimmabgabevermerkeStore(),
        "loadStimmabgabevermerke"
      );

      const mockTaskFactoryContext: TaskFactoryContext = {
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        isErfassungsteam: false,
        isSchriftfuehrung: true,
        extendedWahlMetaData: [
          {
            wahlID: "wahl-1",
            wahlArt: "OBW" as any,
            wahlbezirkID: "bezirk-1",
            wahlName: "Oberbürgermeister",
            wahlnummer: "1",
            waehlerverzeichnisNummer: 1,
          },
        ],
      };

      const tasks = createTasks(mockTaskFactoryContext);

      expect(tasks).toHaveLength(1);
      expect(tasks[0].name).toBe("Stimmzettel für Oberbürgermeister");

      mockLoad.mockRestore();
    });

    it("should_setCorrectTaskName_when_taskIsCreated", () => {
      const mockTaskFactoryContext: TaskFactoryContext = {
        wahlbezirkArt: WahlbezirksArtEnum.BWB,
        isErfassungsteam: false,
        isSchriftfuehrung: true,
        extendedWahlMetaData: [
          {
            wahlID: "wahl-123",
            wahlArt: "KW" as any,
            wahlbezirkID: "bezirk-456",
            wahlName: "Gemeinderat 2024",
            wahlnummer: "3",
            waehlerverzeichnisNummer: 42,
          },
        ],
      };

      const tasks = createTasks(mockTaskFactoryContext);

      expect(tasks[0].name).toBe("Stimmzettel für Gemeinderat 2024");
    });

    it("should_returnEmptyArray_when_extendedWahlMetaDataIsEmpty", () => {
      const mockTaskFactoryContext: TaskFactoryContext = {
        wahlbezirkArt: WahlbezirksArtEnum.UWB,
        isErfassungsteam: false,
        isSchriftfuehrung: true,
        extendedWahlMetaData: [],
      };

      const tasks = createTasks(mockTaskFactoryContext);

      expect(tasks).toHaveLength(0);
    });
  });
});
