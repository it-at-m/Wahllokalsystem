import { createTestingPinia } from "@pinia/testing";
import { setActivePinia, storeToRefs } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useTaskListService } from "@/composables/tasks/taskListService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

describe("taskListService.ts", () => {
  let unitUnderTest: ReturnType<typeof useTaskListService>;

  beforeEach(() => {
    setActivePinia(
      createTestingPinia({
        createSpy: vi.fn,
      })
    );
    unitUnderTest = useTaskListService();
  });

  describe("getTaskList", () => {
    it("should_returnListOfTaskForUWB_when_tasksAndFiltersAreGiven", () => {
      const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());

      // @ts-expect-error: cannot set readonly
      currentUserWahlbezirksArt.value = WahlbezirksArtEnum.UWB;

      const taskNames = unitUnderTest.getTaskList().map((task) => task.name);

      const expectedTaskNames = [
        "Konfigurationsparameter",
        "Wahlen",
        "Wahlvorstand",
        "UngültigeWahlscheine",
        "Kopfdaten",
      ];

      expect(taskNames).toStrictEqual(expectedTaskNames);
    });

    it("should_returnListOfTaskForBWB_when_tasksAndFiltersAreGiven", () => {
      const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());

      // @ts-expect-error: cannot set readonly
      currentUserWahlbezirksArt.value = WahlbezirksArtEnum.BWB;

      const taskNames = unitUnderTest.getTaskList().map((task) => task.name);

      const expectedTaskNames = [
        "Konfigurationsparameter",
        "Wahlen",
        "Wahlvorstand",
        "Kopfdaten",
      ];

      expect(taskNames).toStrictEqual(expectedTaskNames);
    });
  });
});
