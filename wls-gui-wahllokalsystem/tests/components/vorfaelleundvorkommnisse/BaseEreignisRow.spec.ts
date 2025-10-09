import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { useVorfaelleundvorkommnisseTestDataFactory } from "@tests/utils/vorfaelleundvorkommnisse/VorfaelleundvorkommnisseTestDataFactory.ts";
import { flushPromises, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { VTextarea, VTextField } from "vuetify/components";

import BaseEreignisRow from "@/components/vorfaelleundvorkommnisse/BaseEreignisRow.vue";
import vuetify from "@/plugins/vuetify.ts";

const { createEreignis, prepareEreignis } =
  useVorfaelleundvorkommnisseTestDataFactory();

describe("BaseEreignisRow.vue", () => {
  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  // Stub the global ResizeObserver
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  let wrapper: VueWrapper;
  beforeEach(() => {
    wrapper = mount(BaseEreignisRow, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
          }),
          vuetify,
        ],
      },
      props: {
        lineNumber: 1,
        modelValue: createEreignis(),
      },
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
  });
  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderRowWithData_when_mounted", async (context) => {
      await wrapper.setProps({
        lineNumber: 42,
        modelValue: prepareEreignis()
          .beschreibung("dies ist eine Beschreibung")
          .uhrzeit(new Date("2025-07-29T15:36:42.23"))
          .build(),
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderRowWithErrorMessages_when_mountedWithoutAnyData", async (context) => {
      await wrapper.setProps({
        lineNumber: 42,
        modelValue: prepareEreignis()
          .beschreibung(undefined)
          .uhrzeit(undefined)
          .build(),
      });

      wrapper.findAllComponents(VTextField).forEach((component) => {
        component.vm.validate();
      });
      wrapper.findComponent(VTextarea).vm.validate();

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_emitDeleteEvent_when_deleteIconIsClicked", async () => {
      await wrapper.setProps({
        lineNumber: 1,
        modelValue: prepareEreignis()
          .beschreibung("Beschreibung")
          .uhrzeit(new Date("2025-07-29T15:36:42.23"))
          .build(),
      });
      const deleteIcon = wrapper.findComponent(
        '[data-test="delete-ereignis-icon"]'
      );
      expect(deleteIcon.exists()).toBe(true);

      await deleteIcon.trigger("click");
      expect(wrapper.emitted("delete")).toEqual([
        [
          {
            dateOnly: new Date("2025-07-29T15:36:42.23"),
            timeOnly: new Date("2025-07-29T15:36:42.23"),
            beschreibung: "Beschreibung",
          },
        ],
      ]);
    });
  });
});
