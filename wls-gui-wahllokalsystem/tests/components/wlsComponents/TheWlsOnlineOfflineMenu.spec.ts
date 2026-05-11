import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { flushPromises, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import TheWlsOnlineOfflineMenu from "@/components/wlsComponents/TheWlsOnlineOfflineMenu.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useOnlineOfflineStore } from "@/stores/onlineOfflineStore.ts";

describe("TheWlsOnlineOfflineMenu.vue", () => {
  let wrapper: VueWrapper;

  vi.stubGlobal("visualViewport", new EventTarget());
  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  beforeEach(() => {
    wrapper = mount(TheWlsOnlineOfflineMenu, {
      global: {
        plugins: [
          vuetify,
          createTestingPinia({
            createSpy: vi.fn,
          }),
        ],
        stubs: {
          OfflineSyncerDialog: true,
          OfflineSyncerButton: true,
        },
      },
    });
  });

  afterEach(() => {
    document.body.innerHTML = "";
    document.head.innerHTML = "";
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderActivatorButton_when_mounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );

      expect(document.body.innerHTML).toStrictEqual("");
    });

    it("should_renderMenuInOfflineMode_when_activatorButtonWasClicked", async (context) => {
      useOnlineOfflineStore().isCheckingStatus = false;
      useOnlineOfflineStore().isOnline = false;

      const activatorButton = wrapper.findComponent(
        '[data-test="button-activator-menu-online-offline"]'
      );
      await activatorButton.trigger("click");

      await flushPromises();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderMenuInOnlineMode_when_activatorButtonWasClicked", async (context) => {
      useOnlineOfflineStore().isCheckingStatus = false;
      useOnlineOfflineStore().isOnline = true;

      const activatorButton = wrapper.findComponent(
        '[data-test="button-activator-menu-online-offline"]'
      );
      await activatorButton.trigger("click");

      await flushPromises();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderMenuInCheckingStateMode_when_activatorButtonWasClicked", async (context) => {
      useOnlineOfflineStore().isCheckingStatus = true;

      const activatorButton = wrapper.findComponent(
        '[data-test="button-activator-menu-online-offline"]'
      );
      await activatorButton.trigger("click");

      await flushPromises();

      await expect(document.body.innerHTML).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
