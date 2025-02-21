import type { ExtendedContext, RunnerTestCase, RunnerTestSuite } from "vitest";

import { createTestingPinia } from "@pinia/testing";
import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
import { createPinia } from "pinia";
import {
  afterEach,
  beforeAll,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { nextTick } from "vue";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

import TheWahlvorstandAnwesenheitRequirementCard from "@/components/wahlvorstand/TheWahlvorstandAnwesenheitRequirementCard.vue";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";

describe("TheWahlvorstandAnwesenheitRequirementCard.vue", () => {
  let vuetify: ReturnType<typeof createVuetify>;
  let wrapper: VueWrapper;

  beforeAll(() => {
    createPinia();
    createVuetify();
  });

  beforeEach(() => {
    vuetify = createVuetify({
      components,
      directives,
    });

    wrapper = mount(TheWahlvorstandAnwesenheitRequirementCard, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
          }),
          vuetify,
        ],
      },
    });
    // Löscht die mocking-History
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe("DataBinding", () => {
    it("should_showNoErrorTexts_when_allRequirementsAreSatisfied", async (context) => {
      const wahlvorstandStore = useWahlvorstandStore();

      // @ts-expect-error: cannot set readonly
      wahlvorstandStore.isSchriftfuehrerAnwesend = true;
      // @ts-expect-error: cannot set readonly
      wahlvorstandStore.isWahlvorsteherAnwesend = true;

      await nextTick(); //changes of stores are handled in component

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
    it("should_showErrorText_when_schriftfuehrerIsNotAnwesend", async (context) => {
      const wahlvorstandStore = useWahlvorstandStore();

      // @ts-expect-error: cannot set readonly
      wahlvorstandStore.isSchriftfuehrerAnwesend = false;
      // @ts-expect-error: cannot set readonly
      wahlvorstandStore.isWahlvorsteherAnwesend = true;

      await nextTick(); //changes of stores are handled in component

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
    it("should_showErrorText_when_wahlvorsteherIsNotAnwesend", async (context) => {
      const wahlvorstandStore = useWahlvorstandStore();

      // @ts-expect-error: cannot set readonly
      wahlvorstandStore.isSchriftfuehrerAnwesend = true;
      // @ts-expect-error: cannot set readonly
      wahlvorstandStore.isWahlvorsteherAnwesend = false;

      await nextTick(); //changes of stores are handled in component

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});

function getSnapshotFilename(context: ExtendedContext<RunnerTestCase>): string {
  const path = `./__snapshots__/${getSuitPath(context.task.suite)}/`;
  console.debug(`snapshot path: ${path}`);
  return `${path}/${context.task.name}.html`;
}

function getSuitPath(suite?: RunnerTestSuite): string {
  const pathElementsBottomUp: string[] = [];

  let suitForPath = suite;
  while (
    suitForPath !== undefined &&
    suitForPath.suite !== suitForPath &&
    !pathElementsBottomUp.includes(suitForPath.name)
  ) {
    pathElementsBottomUp.push(suitForPath.name);
    suitForPath = suitForPath.suite;
  }

  return pathElementsBottomUp.reverse().join("/");
}
