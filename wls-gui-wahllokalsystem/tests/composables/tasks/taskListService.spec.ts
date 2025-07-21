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
  getWahlOrUndefinedById: vi.fn(),
}));

vi.mock(
  "@/composables/tasks/taskFactories/KonfigurationsparameterTaskFactory.ts",
  () => ({
    useKonfigurationsparameterTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksKonfigurationsparameter,
    })),
  })
);

vi.mock("@/composables/tasks/taskFactories/KopfdatenTaskFactory.ts", () => ({
  useKopfdatenTaskFactory: vi.fn().mockImplementation(() => ({
    createTasks: mockDefinitions.createTasksKopfdaten,
  })),
}));

vi.mock(
  "@/composables/tasks/taskFactories/UngueltigeWahlscheineTaskFactory.ts",
  () => ({
    useUngueltigeWahlscheineTaskFactory: vi.fn().mockImplementation(() => ({
      createTasks: mockDefinitions.createTasksUngueltigeWahlscheine,
    })),
  })
);

vi.mock("@/composables/tasks/taskFactories/WahlvorstandTaskFactory.ts", () => ({
  useWahlvorstandTaskFactory: vi.fn().mockImplementation(() => ({
    createTasks: mockDefinitions.createTasksWahlvorstand,
  })),
}));

vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: () => ({
    getWahlOrUndefinedById: mockDefinitions.getWahlOrUndefinedById,
  }),
}));
describe("taskListService.ts", () => {
  let unitUnderTest: ReturnType<typeof useTaskListService>;
  const { generateRandomString } = useCommonTestDataFactory();
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

      // @ts-expect-error: cannot set readonly
      currentUserWahlbezirksArt.value = WahlbezirksArtEnum.UWB;
      // @ts-expect-error: cannot set readonly
      currentUserWahlMetadata.value = [
        {
          wahlbezirkID: generateRandomString(10),
          wahlnummer: generateRandomString(10),
          wahlID: generateRandomString(10),
        },
      ];

      const mockedWahl = createWahl();

      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(mockedWahl);
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

      const result = unitUnderTest.initTasklist();

      const taskNames = result.map((task) => task.name);

      const expectedTaskNames = [
        "Konfigurationsparameter",
        "Wahlvorstand",
        "UngültigeWahlscheine",
        "Kopfdaten - " + mockedWahl.name,
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
    });
  });
});
