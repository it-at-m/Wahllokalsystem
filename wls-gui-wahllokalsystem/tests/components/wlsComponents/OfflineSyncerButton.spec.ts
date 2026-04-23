import type { VueWrapper } from "@vue/test-utils";

import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import OfflineSyncerButton from "@/components/wlsComponents/OfflineSyncerButton.vue";
import vuetify from "@/plugins/vuetify.ts";

describe("OfflineSyncerButton", () => {
  let wrapper: VueWrapper;

  vi.stubGlobal("visualViewport", new EventTarget());
  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  beforeEach(() => {
    wrapper = mount(OfflineSyncerButton, {
      global: {
        plugins: [vuetify],
      },
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
    document.body.innerHTML = "";
    document.head.innerHTML = "";
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderActivatorButton_when_mounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_emitEvent_when_buttonIsClicked", async () => {
      const syncButton = wrapper.findComponent(
        '[data-test="button-sync-offline-data"]'
      );

      await syncButton.trigger("click");

      const events = wrapper.emitted("click");
      expect(events).toBeTruthy();
      if (events) {
        expect(events.length).toStrictEqual(1);
      }
    });
  });
});
