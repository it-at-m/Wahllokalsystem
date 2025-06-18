import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { enableAutoUnmount, flushPromises, mount } from "@vue/test-utils";
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
import { VBtn } from "vuetify/components";
import * as directives from "vuetify/directives";

import TheNachbesetzungDruckenButton from "@/components/wahlvorstand/TheNachbesetzungDruckenButton.vue";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { prepareUser } = useUserTestDataFactory();

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
    it("should_notRenderButton_when_usersWahlbezirksArtIsUwb", async (context) => {
      const userStore = useUserStore();
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderButton_when_usersWahlbezirksArtIsBwb", async (context) => {
      const userStore = useUserStore();
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    describe("onNachbesetzungDruckenClicked", () => {
      it("should_saveAndLoadWahlvorstandAndOpenNewWindow_when_clicked", async () => {
        const userStore = useUserStore();
        const wahlvorstandStore = useWahlvorstandStore();

        userStore.setUser(
          prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
        );
        await nextTick();

        const originalWindow = window.open;
        const mockedWindow = {
          document: { body: { innerHTML: "" } },
          print: vi.fn(),
          close: vi.fn(),
        };
        window.open = vi.fn().mockReturnValue(mockedWindow);

        const button = wrapper.findComponent(VBtn);
        await button.trigger("click");
        await flushPromises(); // wait for all async operations to be executed

        expect(wahlvorstandStore.sendWahlvorstand).toHaveBeenCalled();
        expect(wahlvorstandStore.loadWahlvorstand).toHaveBeenCalled();
        expect(window.open).toHaveBeenCalled();
        expect(mockedWindow.print).toHaveBeenCalled();

        window.open = originalWindow; // restore previous state
      });
    });
  });
});
