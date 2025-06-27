import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlvorstandTestDataFactory } from "@tests/utils/wahlvorstand/WahlvorstandTestDataFactory.ts";
import { enableAutoUnmount, flushPromises, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick, ref } from "vue";
import { VBtn } from "vuetify/components";

import TheNachbesetzungDruckenButton from "@/components/wahlvorstand/TheNachbesetzungDruckenButton.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadWahlvorstand: vi.fn(),
  sendWahlvorstand: vi.fn(),
}));
vi.mock("@/stores/wahlvorstandStore.ts", () => ({
  useWahlvorstandStore: () => ({
    wahlvorstand: ref(createWahlvorstand()),
    sendWahlvorstand: mockDefinitions.sendWahlvorstand,
    loadWahlvorstand: mockDefinitions.loadWahlvorstand,
  }),
}));

const { prepareUser } = useUserTestDataFactory();
const { createWahlvorstand } = useWahlvorstandTestDataFactory();

describe("TheNachbesetzungDruckenButton.vue", () => {
  let wrapper: VueWrapper;

  beforeEach(() => {
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
        userStore.setUser(
          prepareUser()
            .wahlbezirkID("fhrtf")
            .wahlbezirksArt(WahlbezirksArtEnum.BWB)
            .build()
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

        mockDefinitions.sendWahlvorstand.mockResolvedValue(Promise.resolve());
        mockDefinitions.loadWahlvorstand.mockResolvedValue(
          createWahlvorstand()
        );

        expect(mockDefinitions.sendWahlvorstand).toHaveBeenCalled();
        expect(mockDefinitions.loadWahlvorstand).toHaveBeenCalled();
        expect(window.open).toHaveBeenCalled();
        expect(mockedWindow.print).toHaveBeenCalled();

        window.open = originalWindow; // restore previous state
      });
    });
  });
});
