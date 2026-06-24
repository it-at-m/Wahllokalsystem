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
import { VTextField } from "vuetify/components";

import BaseListWahllokalBenutzer from "@/components/wahltag/BaseListWahllokalBenutzer.vue";
import vuetify from "@/plugins/vuetify.ts";

const ResizeObserverMock = vi.fn(
  class MockedResizeObserverMock {
    observe = vi.fn();
    unobserve = vi.fn();
    disconnect = vi.fn();
  } as never
);
vi.stubGlobal("ResizeObserver", ResizeObserverMock);

const clipboardWriteMock = vi.fn().mockResolvedValue(undefined);

const csvWithUsers = "kueh-0002\nmfpz-0001\nqrla-0003";

describe("BaseListWahllokalBenutzer.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseListWahllokalBenutzer>>;
  vi.stubGlobal("visualViewport", new EventTarget());

  beforeEach(() => {
    Object.defineProperty(navigator, "clipboard", {
      value: { writeText: clipboardWriteMock },
      configurable: true,
    });
    wrapper = mount(BaseListWahllokalBenutzer, {
      global: { plugins: [vuetify] },
      props: {
        csv: csvWithUsers,
      },
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
    wrapper.unmount();
    document.body.innerHTML = "";
    document.head.innerHTML = "";
  });

  afterAll(() => {
    vi.unstubAllGlobals();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderLedgerSortedByWahlbezirk_when_csvProvided", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderEmptyState_when_csvIsNoUsersMessage", async (context) => {
      await wrapper.setProps({
        csv: "Keine Nutzer zum angegebenen Wahltag gefunden.",
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_decodeWahlbezirkAndCountDistinct_when_csvProvided", () => {
      const rows = wrapper.findAll('[data-test="benutzer-row"]');
      expect(rows).toHaveLength(3);

      // sortiert nach Wahlbezirksnummer (Suffix), nicht nach Eingabereihenfolge
      expect(rows[0].text()).toContain("0001");
      expect(rows[1].text()).toContain("0002");
      expect(rows[2].text()).toContain("0003");

      expect(wrapper.find('[data-test="benutzer-summary"]').text()).toContain(
        "3"
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_filterRows_when_searchTermEntered", async () => {
      const searchField = wrapper.findComponent(VTextField);

      await searchField.setValue("0001");

      const rows = wrapper.findAll('[data-test="benutzer-row"]');
      expect(rows).toHaveLength(1);
      expect(rows[0].text()).toContain("mfpz-0001");
    });

    it("should_showEmptyState_when_searchTermMatchesNothing", async () => {
      const searchField = wrapper.findComponent(VTextField);

      await searchField.setValue("does-not-exist");

      expect(wrapper.findAll('[data-test="benutzer-row"]')).toHaveLength(0);
      expect(wrapper.find('[data-test="benutzer-empty"]').exists()).toBe(true);
    });

    it("should_copyCsvToClipboard_when_copyButtonClicked", async () => {
      const copyButton = wrapper.findComponent('[data-test="copy-benutzer"]');

      await copyButton.trigger("click");

      expect(clipboardWriteMock).toHaveBeenCalledWith(csvWithUsers);
    });
  });
});
