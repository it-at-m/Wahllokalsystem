import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { setActivePinia, storeToRefs } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useTaskListService } from "@/composables/tasks/taskListService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  createTasksKopfdaten: vi.fn(),
  createTasksKonfigurationsparameter: vi.fn(),
  createTasksUngueltigeWahlscheine: vi.fn(),
  createWaehlerverzeichnisTasks: vi.fn(),
  createTasksWahlvorstand: vi.fn(),
  createTasksEroeffnungsuhrzeit: vi.fn(),
  createTasksUrnenwahlSchliessungsuhrzeit: vi.fn(),
  createTasksWahlscheine: vi.fn(),
  getWahlOrUndefinedById: vi.fn(),
  createTasksStimmabgabevermerke: vi.fn(),
  createTasksStimmzettelumschlaege: vi.fn(),
  getWaehlerverzeichnisNummerOrUndefinedById: vi.fn(),
  createTasksWaehler: vi.fn(),
  createTasksEreignisse: vi.fn(),
  createTasksBegruendung: vi.fn(),
  createTasksAWerte: vi.fn(),
  createTasksHandbuch: vi.fn(),
  createTasksStatus: vi.fn(),
  createTasksWahlvorbereitung: vi.fn(),
  createTasksWahlbriefe: vi.fn(),
  createTasksBeanstandeteWahlbriefe: vi.fn(),
  createTasksWahlvorschlaege: vi.fn(),
  createTasksErgebnisse: vi.fn(),
  createMbwWahlvorschlaegeAndErgebnisseTasks: vi.fn(),
}));

vi.mock(
  import("@/composables/tasks/taskFactories/konfigurationsparameterTaskFactory.ts"),
  () => ({
    useKonfigurationsparameterTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksKonfigurationsparameter,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/kopfdatenTaskFactory.ts"),
  () => ({
    useKopfdatenTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksKopfdaten,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/ungueltigeWahlscheineTaskFactory.ts"),
  () => ({
    useUngueltigeWahlscheineTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksUngueltigeWahlscheine,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/waehlerTaskFactory.ts"),
  () => ({
    useWaehlerTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksWaehler,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/waehlverzeichnisTaskFactory.ts"),
  () => ({
    useWaehlverzeichnisTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createWaehlerverzeichnisTasks,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/wahlvorstandTaskFactory.ts"),
  () => ({
    useWahlvorstandTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksWahlvorstand,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/eroeffnungsuhrzeitTaskFactory.ts"),
  () => ({
    useEroeffnungsuhrzeitTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksEroeffnungsuhrzeit,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/urnenwahlSchliessungsuhrzeitTaskFactory.ts"),
  () => ({
    useUrnenwahlSchliessungsuhrzeitTaskFactory: vi
      .fn()
      .mockImplementation(() => ({
        createTasks: mockDefinitions.createTasksUrnenwahlSchliessungsuhrzeit,
      })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/wahlscheineTaskFactory.ts"),
  () => ({
    useWahlscheineTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksWahlscheine,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/stimmabgabevermerkeTaskFactory.ts"),
  () => ({
    useStimmabgabevermerkeTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksStimmabgabevermerke,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/stimmzettelumschlaegeTaskFactory.ts"),
  () => ({
    useStimmzettelumschlaegeTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksStimmzettelumschlaege,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/ereignisseTaskFactory.ts"),
  () => ({
    useEreignisseTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksEreignisse,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/begruendungTaskFactory.ts"),
  () => ({
    useBegruendungTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksBegruendung,
    })),
  })
);

vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: () => ({
    wahlenActions: {
      getWahlOrUndefinedById: mockDefinitions.getWahlOrUndefinedById,
    },
    waehlerverzeichnisActions: {
      getWaehlerverzeichnisNummerOrUndefinedById:
        mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById,
    },
  }),
}));

vi.mock(
  import("@/composables/tasks/taskFactories/aWerteTaskFactory.ts"),
  () => ({
    useAWerteTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksAWerte,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/handbuchTaskFactory.ts"),
  () => ({
    useHandbuchTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksHandbuch,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/statusTaskFactory.ts"),
  () => ({
    useStatusTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksStatus,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/wahlvorbereitungTaskFactory.ts"),
  () => ({
    useWahlvorbereitungTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksWahlvorbereitung,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/wahlbriefeTaskFactory.ts"),
  () => ({
    useWahlbriefeTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksWahlbriefe,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/beanstandeteWahlbriefeTaskFactory.ts"),
  () => ({
    useBeanstandeteWahlbriefeTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksBeanstandeteWahlbriefe,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/wahlvorschlaegeTaskFactory.ts"),
  () => ({
    useWahlvorschlaegeTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksWahlvorschlaege,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/ergebnisseTaskFactory.ts"),
  () => ({
    useErgebnisseTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksErgebnisse,
    })),
  })
);

vi.mock(
  import("@/composables/tasks/taskFactories/mbwWahlvorschlaegeAndErgebnisseTaskFactory.ts"),
  () => ({
    useMBWWahlvorschlaegeAndErgebnisseTaskFactory: vi
      .fn()
      .mockImplementation(() => ({
        createTasks: mockDefinitions.createMbwWahlvorschlaegeAndErgebnisseTasks,
      })),
  })
);

describe("taskListService.ts", () => {
  let unitUnderTest: ReturnType<typeof useTaskListService>;
  const { generateRandomString, generateRandomNumber } =
    useCommonTestDataFactory();
  const { createWahl } = useWahlTestDataFactory();
  beforeEach(() => {
    setActivePinia(
      createTestingPinia({
        createSpy: vi.fn,
      })
    );
    unitUnderTest = useTaskListService();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("getTaskList", () => {
    const wahlMedata = {
      wahlbezirkID: generateRandomString(10),
      wahlnummer: generateRandomString(10),
      wahlID: generateRandomString(10),
    };
    const mockedWahl = createWahl();
    const mockedWaehlerverzeichnisNummer = generateRandomNumber(2);

    it("should_containListWithUWBTasks_when_initTaskListIsCalled", () => {
      const { currentUserWahlMetadata, currentUserWahlbezirksArt } =
        storeToRefs(useUserStore());
      // @ts-expect-error: cannot set readonly
      currentUserWahlbezirksArt.value = WahlbezirksArtEnum.UWB;
      // @ts-expect-error: cannot set readonly
      currentUserWahlMetadata.value = [wahlMedata];

      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(mockedWahl);
      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById.mockReturnValue(
        mockedWaehlerverzeichnisNummer
      );
      _mockReturnValuesForUWBTasks(currentUserWahlbezirksArt.value);

      const expectedTaskNames = [
        "Kopfdaten - " + mockedWahl.name,
        "Waehlerverzeichnis",
        "UngültigeWahlscheine",
        "Wahlvorstand",
        "Eröffnungsuhrzeit",
        "Urnenwahl Schließungsuhrzeit",
        "Konfigurationsparameter",
        "Wahlvorschläge - " + mockedWahl.name,
        "Stapel - XX für " + mockedWahl.name,
        "Ereignisse",
        `Stimmabgabevermerke-${wahlMedata.wahlbezirkID}-WVZ-${mockedWaehlerverzeichnisNummer}-${mockedWahl.nummer}`,
        "Stimmzettel " + mockedWahl.name,
        "Wahlbeteiligung",
        "Begruendung Stimmzettel für " + mockedWahl.name,
        "AWerte",
        "Handbuch",
        "Druckstatus - " + mockedWahl.name,
        "Wahlvorbereitung",
        "MBW Tasks",
      ];

      const result = unitUnderTest.initTasklist();
      const taskNames = result.map((task) => task.name);

      expect(taskNames).toStrictEqual(expectedTaskNames);
      _expectAllTaskFactoriesHaveBeenCalled();
    });

    it("should_containListWithBWBTasks_when_initTaskListIsCalled", () => {
      const { currentUserWahlMetadata, currentUserWahlbezirksArt } =
        storeToRefs(useUserStore());
      // @ts-expect-error: cannot set readonly
      currentUserWahlbezirksArt.value = WahlbezirksArtEnum.BWB;
      // @ts-expect-error: cannot set readonly
      currentUserWahlMetadata.value = [wahlMedata];

      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(mockedWahl);
      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById.mockReturnValue(
        mockedWaehlerverzeichnisNummer
      );
      _mockReturnValuesForBWBTasks(currentUserWahlbezirksArt.value);

      const expectedTaskNames = [
        "Kopfdaten - " + mockedWahl.name,
        "Wahlvorstand",
        "Eröffnungsuhrzeit",
        "Konfigurationsparameter",
        "Wahlscheine - " + mockedWahl.name,
        "Wahlvorschläge - " + mockedWahl.name,
        "Stapel - XX für " + mockedWahl.name,
        "Ereignisse",
        "Stimmzettelumschläge " + mockedWahl.name,
        "Begruendung Stimmzettelumschläge für " + mockedWahl.name,
        "Handbuch",
        "Druckstatus - " + mockedWahl.name,
        "Wahlvorbereitung",
        "Erfasste Wahlbriefe",
        "Zugelassene Wahlbriefe",
        "MBW Tasks",
      ];

      const result = unitUnderTest.initTasklist();
      const taskNames = result.map((task) => task.name);

      expect(taskNames).toStrictEqual(expectedTaskNames);
      _expectAllTaskFactoriesHaveBeenCalled();
    });

    function _mockReturnValuesForBWBTasks(wbzArt: WahlbezirksArtEnum) {
      // all wahlbezirke
      _mockReturnValuesForAllWahlbezirkeTasks(wbzArt);

      // only uwb
      mockDefinitions.createWaehlerverzeichnisTasks.mockReturnValue([]);
      mockDefinitions.createTasksUngueltigeWahlscheine.mockReturnValue([]);
      mockDefinitions.createTasksUrnenwahlSchliessungsuhrzeit.mockReturnValue(
        []
      );
      mockDefinitions.createTasksStimmabgabevermerke.mockReturnValue([]);
      mockDefinitions.createTasksWaehler.mockReturnValue([]);
      mockDefinitions.createTasksAWerte.mockReturnValue([]);

      // only bwb
      mockDefinitions.createTasksWahlscheine.mockReturnValue([
        {
          name: "Wahlscheine - " + mockedWahl.name,
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksWahlbriefe.mockReturnValue([
        {
          name: "Erfasste Wahlbriefe",
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksBeanstandeteWahlbriefe.mockReturnValue([
        {
          name: "Zugelassene Wahlbriefe",
          callback: () => Promise.resolve(),
        },
      ]);
    }

    function _mockReturnValuesForUWBTasks(wbzArt: WahlbezirksArtEnum) {
      // all wahlbezirke
      _mockReturnValuesForAllWahlbezirkeTasks(wbzArt);

      // only uwb
      mockDefinitions.createWaehlerverzeichnisTasks.mockReturnValue([
        {
          name: "Waehlerverzeichnis",
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksUngueltigeWahlscheine.mockReturnValue([
        {
          name: "UngültigeWahlscheine",
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksUrnenwahlSchliessungsuhrzeit.mockReturnValue([
        {
          name: "Urnenwahl Schließungsuhrzeit",
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksStimmabgabevermerke.mockReturnValue([
        {
          name: `Stimmabgabevermerke-${wahlMedata.wahlbezirkID}-WVZ-${mockedWaehlerverzeichnisNummer}-${mockedWahl.nummer}`,
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksWaehler.mockReturnValue([
        { name: "Wahlbeteiligung", callback: () => Promise.resolve() },
      ]);
      mockDefinitions.createTasksAWerte.mockReturnValue([
        {
          name: "AWerte",
          callback: () => Promise.resolve(),
        },
      ]);

      // only bwb
      mockDefinitions.createTasksWahlscheine.mockReturnValue([]);
      mockDefinitions.createTasksWahlbriefe.mockReturnValue([]);
      mockDefinitions.createTasksBeanstandeteWahlbriefe.mockReturnValue([]);
    }

    function _mockReturnValuesForAllWahlbezirkeTasks(
      wbzArt: WahlbezirksArtEnum
    ) {
      const stimmzettelTerm =
        wbzArt == WahlbezirksArtEnum.UWB
          ? "Stimmzettel"
          : "Stimmzettelumschläge";

      // all wahlbezirke
      mockDefinitions.createTasksKopfdaten.mockReturnValue([
        {
          name: "Kopfdaten - " + mockedWahl.name,
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksWahlvorstand.mockReturnValue([
        {
          name: "Wahlvorstand",
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksEroeffnungsuhrzeit.mockReturnValue([
        {
          name: "Eröffnungsuhrzeit",
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksKonfigurationsparameter.mockReturnValue([
        {
          name: "Konfigurationsparameter",
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksWahlvorschlaege.mockReturnValue([
        {
          name: `Wahlvorschläge - ${mockedWahl.name}`,
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksErgebnisse.mockReturnValue([
        {
          name: `Stapel - XX für ${mockedWahl.name}`,
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksEreignisse.mockReturnValue([
        { name: "Ereignisse", callback: () => Promise.resolve() },
      ]);
      mockDefinitions.createTasksStimmzettelumschlaege.mockReturnValue([
        {
          name: `${stimmzettelTerm} ${mockedWahl.name}`,
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksBegruendung.mockReturnValue([
        {
          name: `Begruendung ${stimmzettelTerm} für ${mockedWahl.name}`,
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksHandbuch.mockReturnValue([
        {
          name: "Handbuch",
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksStatus.mockReturnValue([
        {
          name: "Druckstatus - " + mockedWahl.name,
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksWahlvorbereitung.mockReturnValue([
        {
          name: "Wahlvorbereitung",
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createMbwWahlvorschlaegeAndErgebnisseTasks.mockReturnValue(
        [
          {
            name: "MBW Tasks",
            callback: () => Promise.resolve(),
          },
        ]
      );
    }

    function _expectAllTaskFactoriesHaveBeenCalled() {
      Object.entries(mockDefinitions).forEach(([name, mock]) => {
        expect(mock, `missing: ${name}`).toHaveBeenCalled();
      });
    }
  });
});
