import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
import { createPinia, storeToRefs } from "pinia";
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
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_showNoErrorTexts_when_allRequirementsAreSatisfied", async (context) => {
      const {
        isSchriftfuehrerAnwesend,
        isWahlvorsteherAnwesend,
        isMindestanwesenheitErreicht,
      } = storeToRefs(useWahlvorstandStore());

      // @ts-expect-error: cannot set readonly
      isSchriftfuehrerAnwesend.value = true;
      // @ts-expect-error: cannot set readonly
      isWahlvorsteherAnwesend.value = true;
      // @ts-expect-error: cannot set readonly
      isMindestanwesenheitErreicht.value = true;

      await nextTick(); //changes of stores are handled in component

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showErrorText_when_schriftfuehrerIsNotAnwesend", async (context) => {
      const {
        isSchriftfuehrerAnwesend,
        isWahlvorsteherAnwesend,
        isMindestanwesenheitErreicht,
      } = storeToRefs(useWahlvorstandStore());

      // @ts-expect-error: cannot set readonly
      isSchriftfuehrerAnwesend.value = false;
      // @ts-expect-error: cannot set readonly
      isWahlvorsteherAnwesend.value = true;
      // @ts-expect-error: cannot set readonly
      isMindestanwesenheitErreicht.value = true;

      await nextTick(); //changes of stores are handled in component

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showErrorText_when_wahlvorsteherIsNotAnwesend", async (context) => {
      const {
        isSchriftfuehrerAnwesend,
        isWahlvorsteherAnwesend,
        isMindestanwesenheitErreicht,
      } = storeToRefs(useWahlvorstandStore());

      // @ts-expect-error: cannot set readonly
      isSchriftfuehrerAnwesend.value = true;
      // @ts-expect-error: cannot set readonly
      isWahlvorsteherAnwesend.value = false;
      // @ts-expect-error: cannot set readonly
      isMindestanwesenheitErreicht.value = true;

      await nextTick(); //changes of stores are handled in component

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showErrorText_when_mindestAnwesenheitIsNotGiven", async (context) => {
      const {
        isSchriftfuehrerAnwesend,
        isWahlvorsteherAnwesend,
        isMindestanwesenheitErreicht,
      } = storeToRefs(useWahlvorstandStore());

      // @ts-expect-error: cannot set readonly
      isSchriftfuehrerAnwesend.value = true;
      // @ts-expect-error: cannot set readonly
      isWahlvorsteherAnwesend.value = true;
      // @ts-expect-error: cannot set readonly
      isMindestanwesenheitErreicht.value = false;

      await nextTick(); //changes of stores are handled in component

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
