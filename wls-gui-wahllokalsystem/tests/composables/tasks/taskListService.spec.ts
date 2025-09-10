import { mock } from "node:test";

import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { setActivePinia, storeToRefs } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useErgebnisseTaskFactory } from "@/composables/tasks/taskFactories/ergebnisseTaskFactory.ts";
import { useWahlvorschlaegeTaskFactory } from "@/composables/tasks/taskFactories/wahlvorschlaegeTaskFactory.ts";
import { useTaskListService } from "@/composables/tasks/taskListService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  createTasksErgebnisse: vi.fn(),
  createTasksKopfdaten: vi.fn(),
  createTasksKonfigurationsparameter: vi.fn(),
  createTasksUngueltigeWahlscheine: vi.fn(),
  createTasksWahlvorstand: vi.fn(),
  createTasksWahlscheine: vi.fn(),
  createTasksWahlvorschlaege: vi.fn(),
  getWahlOrUndefinedById: vi.fn(),
  createTasksStimmabgabevermerke: vi.fn(),
  getWaehlerverzeichnisNummerOrUndefinedById: vi.fn(),
}));

vi.mock(
  "@/composables/tasks/taskFactories/konfigurationsparameterTaskFactory.ts",
  () => ({
    useKonfigurationsparameterTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksKonfigurationsparameter,
    })),
  })
);

vi.mock("@/composables/tasks/taskFactories/kopfdatenTaskFactory.ts", () => ({
  useKopfdatenTaskFactory: vi.fn().mockImplementation(() => ({
    createTasks: mockDefinitions.createTasksKopfdaten,
  })),
}));

vi.mock(
  "@/composables/tasks/taskFactories/ungueltigeWahlscheineTaskFactory.ts",
  () => ({
    useUngueltigeWahlscheineTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksUngueltigeWahlscheine,
    })),
  })
);

vi.mock("@/composables/tasks/taskFactories/wahlvorstandTaskFactory.ts", () => ({
  useWahlvorstandTaskFactory: vi.fn().mockImplementation(() => ({
    createTasks: mockDefinitions.createTasksWahlvorstand,
  })),
}));

vi.mock("@/composables/tasks/taskFactories/wahlscheineTaskFactory.ts", () => ({
  useWahlscheineTaskFactory: vi.fn().mockImplementation(() => ({
    createTasks: mockDefinitions.createTasksWahlscheine,
  })),
}));

vi.mock("@/composables/tasks/taskFactories/ergebnisseTaskFactory.ts", () => ({
  useErgebnisseTaskFactory: vi.fn().mockImplementation(() => ({
    createTasks: mockDefinitions.createTasksStimmabgabevermerke,
  })),
}));

vi.mock(
  "@/composables/tasks/taskFactories/wahlvorschlaegeTaskFactory.ts",
  () => ({
    useWahlvorschlaegeTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksWahlvorschlaege,
    })),
  })
);

vi.mock(
  "@/composables/tasks/taskFactories/stimmabgabevermerkeTaskFactory.ts",
  () => ({
    useStimmabgabevermerkeTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksErgebnisse,
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

  describe("getTaskList", () => {
    it("should_containListWithTasks_when_initTaskListIsCalled", () => {
      const { currentUserWahlMetadata, currentUserWahlbezirksArt } =
        storeToRefs(useUserStore());
      const wahlMedata = {
        wahlbezirkID: generateRandomString(10),
        wahlnummer: generateRandomString(10),
        wahlID: generateRandomString(10),
      };
      // @ts-expect-error: cannot set readonly
      currentUserWahlbezirksArt.value = WahlbezirksArtEnum.UWB;
      // @ts-expect-error: cannot set readonly
      currentUserWahlMetadata.value = [wahlMedata];

      const mockedWahl = createWahl();
      const mockedWaehlerverzeichnisNummer = generateRandomNumber(2);

      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(mockedWahl);
      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById.mockReturnValue(
        mockedWaehlerverzeichnisNummer
      );
      const taskNameErgebnisse = "ergebnisse";
      mockDefinitions.createTasksErgebnisse.mockReturnValue([
        {
          name: taskNameErgebnisse,
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksKonfigurationsparameter.mockReturnValue([
        {
          name: "Konfigurationsparameter",
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksKopfdaten.mockReturnValue([
        {
          name: "Kopfdaten - " + mockedWahl.name,
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksUngueltigeWahlscheine.mockReturnValue([
        {
          name: "UngültigeWahlscheine",
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksWahlvorstand.mockReturnValue([
        {
          name: "Wahlvorstand",
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksWahlscheine.mockReturnValue([
        {
          name: "Wahlscheine - " + mockedWahl.name,
          callback: () => Promise.resolve(),
        },
      ]);
      const taskNameWahlvorschlaege = "Wahlvorschlaege";
      mockDefinitions.createTasksWahlvorschlaege.mockReturnValue([
        {
          name: taskNameWahlvorschlaege,
          callback: () => Promise.resolve(),
        },
      ]);
      mockDefinitions.createTasksStimmabgabevermerke.mockReturnValue([
        {
          name: `Stimmabgabevermerke-${wahlMedata.wahlbezirkID}-WVZ-${mockedWaehlerverzeichnisNummer}-${mockedWahl.nummer}`,
          callback: () => {
            expect(1).toBe(2);
            return Promise.resolve();
          },
        },
      ]);

      const result = unitUnderTest.initTasklist();

      const taskNames = result.map((task) => task.name);

      const expectedTaskNames = [
        "Konfigurationsparameter",
        "Wahlvorstand",
        "UngültigeWahlscheine",
        "Kopfdaten - " + mockedWahl.name,
        "Wahlscheine - " + mockedWahl.name,
        taskNameWahlvorschlaege,
        taskNameErgebnisse,
        `Stimmabgabevermerke-${wahlMedata.wahlbezirkID}-WVZ-${mockedWaehlerverzeichnisNummer}-${mockedWahl.nummer}`,
      ];

      expect(taskNames).toEqual(expect.arrayContaining(expectedTaskNames));

      expect(
        mockDefinitions.createTasksKonfigurationsparameter
      ).toHaveBeenCalled();
      expect(mockDefinitions.createTasksKopfdaten).toHaveBeenCalled();
      expect(
        mockDefinitions.createTasksUngueltigeWahlscheine
      ).toHaveBeenCalled();
      expect(mockDefinitions.createTasksWahlvorstand).toHaveBeenCalled();
      expect(mockDefinitions.createTasksWahlscheine).toHaveBeenCalled();
      expect(mockDefinitions.createTasksStimmabgabevermerke).toHaveBeenCalled();
      expect(mockDefinitions.createTasksWahlvorschlaege).toHaveBeenCalled();
      expect(mockDefinitions.createTasksErgebnisse).toHaveBeenCalled();

      const indexOfErgebnisseTask = taskNames.findIndex(
        (name) => name === taskNameErgebnisse
      );
      const indexOfWahlvorschlaegeTask = taskNames.findIndex(
        (name) => name === taskNameWahlvorschlaege
      );
      expect(indexOfErgebnisseTask).toBeGreaterThanOrEqual(0);
      expect(indexOfWahlvorschlaegeTask).toBeGreaterThanOrEqual(0);
      expect(indexOfWahlvorschlaegeTask).toBeLessThan(indexOfErgebnisseTask);
    });
  });
});
