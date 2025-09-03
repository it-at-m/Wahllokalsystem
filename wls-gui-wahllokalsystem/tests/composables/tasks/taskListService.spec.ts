import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { setActivePinia, storeToRefs } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useTaskListService } from "@/composables/tasks/taskListService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  createTasksKopfdaten: vi.fn(),
  createTasksKonfigurationsparameter: vi.fn(),
  createTasksUngueltigeWahlscheine: vi.fn(),
  createTasksWahlvorstand: vi.fn(),
  createTasksWahlscheine: vi.fn(),
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

vi.mock(
  "@/composables/tasks/taskFactories/stimmabgabevermerkeTaskFactory.ts",
  () => ({
    useStimmabgabevermerkeTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksStimmabgabevermerke,
    })),
  })
);

vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: () => ({
    wahlenActions: {
      getWahlOrUndefinedById: mockDefinitions.getWahlOrUndefinedById,
    },
    getWaehlerverzeichnisNummerOrUndefinedById:
      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById,
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
      mockDefinitions.createTasksStimmabgabevermerke.mockReturnValue([
        {
          name: `Stimmabgabevermerke-${wahlMedata.wahlbezirkID}-WVZ-${mockedWaehlerverzeichnisNummer}-${mockedWahl.nummer}`,
          callback: () => Promise.resolve(),
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
    });
  });
});
