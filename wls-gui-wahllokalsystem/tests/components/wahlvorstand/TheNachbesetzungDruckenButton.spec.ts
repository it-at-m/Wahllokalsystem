import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import { useUserTestDataFactory } from "@tests/utils/common/UserTestDataFactory.ts";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount } from "@vue/test-utils";
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

import TheNachbesetzungDruckenButton from "@/components/wahlvorstand/TheNachbesetzungDruckenButton.vue";
import { useUserStore } from "@/stores/userStore.ts";

const {
  createUserWithUndefinedWahlbezirksArt,
  createUserWithBwbWahlbezirksArt,
  createUserWithUwbWahlbezirksArt,
} = useUserTestDataFactory();

describe("TheNachbesetzungDruckenButton.vue", () => {
  let vuetify: ReturnType<typeof createVuetify>;
  let wrapper: VueWrapper;

  beforeAll(() => {
    createPinia();
  });

  beforeEach(() => {
    vuetify = createVuetify({ components, directives });

    wrapper = mount(TheNachbesetzungDruckenButton, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
            stubActions: false,
          }),
          vuetify,
        ],
      },
    });
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderButton_when_usersWahlbezirksArtIsUndefined", async (context) => {
      const userStore = useUserStore();
      userStore.setUser(createUserWithUndefinedWahlbezirksArt());

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderButton_when_usersWahlbezirksArtIsBwb", async (context) => {
      const userStore = useUserStore();
      userStore.setUser(createUserWithBwbWahlbezirksArt());

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_notRenderButton_when_usersWahlbezirksArtIsUwb", async (context) => {
      const userStore = useUserStore();
      userStore.setUser(createUserWithUwbWahlbezirksArt());

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
