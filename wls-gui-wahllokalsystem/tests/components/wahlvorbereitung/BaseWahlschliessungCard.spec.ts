import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { flushPromises, mount, VueWrapper } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import BaseWahlschliessungCard from "@/components/wahlvorbereitung/BaseWahlschliessungCard.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

describe("BaseWahlschliessungCard.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseWahlschliessungCard>>;

  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  beforeEach(() => {
    wrapper = mount(BaseWahlschliessungCard, {
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
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWithDisabledSaveButton_when_noUhrzeitIsEntered", async (context) => {
      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.schliessungsuhrzeit = undefined;

      await flushPromises(); //update databinding and keep button disabled

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithEnabledSaveButton_when_uhrzeitIsGiven", async (context) => {
      const date = new Date("2025-05-23T07:30:00");
      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.schliessungsuhrzeit = date;

      await flushPromises(); //update databinding and enabled button

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithSaveButtonInLoadingState_when_isSavingIsTrue", async (context) => {
      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.schliessungsuhrzeitIsSaving = true;

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_updateSchliessungsuhrzeitInStore_when_validDateIsEntered", async () => {
      const wahlbezirkStore = useWahlbezirkStore();

      expect(wahlbezirkStore.schliessungsuhrzeit).toBeUndefined();

      const schliessungsuhrzeitTimeInput = wrapper.findComponent(BaseTimeInput);
      const enteredTime = new Date();
      await schliessungsuhrzeitTimeInput.setValue(enteredTime);

      expect(wahlbezirkStore.schliessungsuhrzeit?.getTime()).toStrictEqual(
        enteredTime.getTime()
      );
    });

    it("should_callSendSchliessungsuhrzeit_when_saveButtonIsClicked", async () => {
      const wahlbezirkStore = useWahlbezirkStore();
      wahlbezirkStore.schliessungsuhrzeit = new Date("2025-05-23T07:30:00");

      await flushPromises();

      const saveButton = wrapper.findComponent(BaseButtonSave);
      await saveButton.trigger("click");

      expect(wahlbezirkStore.sendSchliessungsuhrzeit).toHaveBeenCalled();
    });
  });
});
