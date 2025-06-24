import { createTestingPinia } from "@pinia/testing";
import { storeToRefs } from "pinia";
import { describe, expect, it, vi } from "vitest";

import { useTaskListService } from "@/composables/tasks/taskListService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

describe("taskListService.ts", () => {
  const testPinia = createTestingPinia({
    createSpy: vi.fn,
  });

  const unitUnderTest = useTaskListService();

  describe("getTaskList", () => {
    it("should_returnListOfTaskForUWB_when_tasksAndFiltersAreGiven", () => {
      const { currentUserWahlbezirksArt } = storeToRefs(
        useUserStore(testPinia)
      );

      // @ts-expect-error: cannot set readonly
      currentUserWahlbezirksArt.value = WahlbezirksArtEnum.UWB;

      const taskNames = unitUnderTest.getTaskList().map((task) => task.name);

      const expectedTaskNames = [
        "Konfigurationsparameter",
        "Wahlen",
        "Wahlvorstand",
        "UngültigeWahlscheine",
      ];

      expect(taskNames).toStrictEqual(expectedTaskNames);
    });

    it("should_returnListOfTaskForBWB_when_tasksAndFiltersAreGiven", () => {
      const { currentUserWahlbezirksArt } = storeToRefs(
        useUserStore(testPinia)
      );

      // @ts-expect-error: cannot set readonly
      currentUserWahlbezirksArt.value = WahlbezirksArtEnum.BWB;

      const taskNames = unitUnderTest.getTaskList().map((task) => task.name);

      const expectedTaskNames = [
        "Konfigurationsparameter",
        "Wahlen",
        "Wahlvorstand",
      ];

      expect(taskNames).toStrictEqual(expectedTaskNames);
    });
  });
});
