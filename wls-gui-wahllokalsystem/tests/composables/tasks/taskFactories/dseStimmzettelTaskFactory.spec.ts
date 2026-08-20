import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useDSEStimmzettelTaskFactory } from "@/composables/tasks/taskFactories/dseStimmzettelTaskFactory.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadStimmzettel: vi.fn(),
}));

vi.mock(import("@/composables/dse/stimmzettelFetchService.ts"), () => ({
  useStimmzettelFetchService: vi.fn().mockImplementation(() => ({
    loadStimmzettel: mockDefinitions.loadStimmzettel,
  })),
}));

describe("dseStimmzettelTaskFactory.ts", () => {
  let unitUnderTest: ReturnType<typeof useDSEStimmzettelTaskFactory>;

  const { generateRandomString } = useCommonTestDataFactory();
  const { createTaskFactoryContext } = useTasksTestDataFactory();
  const { prepareUser } = useUserTestDataFactory();
  const { prepareWahl } = useWahlTestDataFactory();

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useDSEStimmzettelTaskFactory();
  });

  describe("createTasks", () => {
    it("should_returnTaskList_when_called", () => {
      const taskFactoryContext = createTaskFactoryContext();
      const wahlenStore = useWahlenStore();

      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
          .wahlID(taskFactoryContext.extendedWahlMetaData[0]!.wahlID)
          .build(),
      ];

      const result = unitUnderTest.createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
    });

    it("should_haveExpectedCallback_when_called", () => {
      const taskFactoryContext = createTaskFactoryContext();
      const wahlenStore = useWahlenStore();
      const userStore = useUserStore();

      const teamID = generateRandomString(10);
      userStore.user = prepareUser().teamName(teamID).build();

      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
          .wahlID(taskFactoryContext.extendedWahlMetaData[0]!.wahlID)
          .build(),
      ];

      mockDefinitions.loadStimmzettel.mockReturnValue(Promise.resolve());

      const result = unitUnderTest.createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);

      result[0]?.callback();
      expect(mockDefinitions.loadStimmzettel).toHaveBeenCalledOnce();
      expect(mockDefinitions.loadStimmzettel).toHaveBeenCalledWith(
        taskFactoryContext.extendedWahlMetaData[0].wahlID,
        taskFactoryContext.extendedWahlMetaData[0].wahlbezirkID,
        teamID,
        false
      );
      expect(result[0]?.name).toContain("Stimmzettel");
    });
  });
});
