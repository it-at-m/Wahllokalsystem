import type { VueWrapper } from "@vue/test-utils";

import { useWahltagTestDataFactory } from "@tests/types/wahltag/WahltagTestDataFactory.ts";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
import { VStepperActions } from "vuetify/components";

import BaseStepWahltagInit from "@/components/wahltag/BaseStepWahltagInit.vue";
import BaseWahltagEventStepper from "@/components/wahltag/BaseWahltagEventStepper.vue";
import vuetify from "@/plugins/vuetify.ts";

const mockDefinitions = vi.hoisted(() => ({
  addNotification: vi.fn(),
}));

vi.mock(
  import("@/composables/userNotification/userNotificationService.ts"),
  () => ({
    useUserNotificationService: () => ({
      addNotification: mockDefinitions.addNotification,
    }),
  })
);

const { prepareWahltagEvent } = useWahltagTestDataFactory();

describe("BaseWahltagEventStepper", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseWahltagEventStepper>>;

  beforeEach(() => {
    wrapper = mount(BaseWahltagEventStepper, {
      global: {
        plugins: [vuetify],
        stubs: {
          BaseStepWahltagInit: true,
        },
      },
      props: {
        wahltagEvents: [],
      },
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderNoContent_when_wahltagEventsIsEmptyArray", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_render_when_wahltagEventsHasOneItem", async (context) => {
      await wrapper.setProps({
        wahltagEvents: [
          prepareWahltagEvent().nummer("1").beschreibung("Testwahl").build(),
        ],
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_render_when_wahltagEventsHasMultipleItems", async (context) => {
      await wrapper.setProps({
        wahltagEvents: [
          prepareWahltagEvent().nummer("1").beschreibung("Wahl 1").build(),
          prepareWahltagEvent().nummer("2").beschreibung("Wahl 2").build(),
          prepareWahltagEvent().nummer("3").beschreibung("Wahl 3").build(),
        ],
      });

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_bindPropertiesCorrectly_when_wahltagEventsHasMultipleItems", async () => {
      wrapper = mount(BaseWahltagEventStepper, {
        global: {
          plugins: [vuetify],
        },
        props: {
          wahltagEvents: [],
        },
      });

      const props = {
        wahltagEvents: [
          prepareWahltagEvent().nummer("1").beschreibung("Wahl 1").build(),
          prepareWahltagEvent().nummer("2").beschreibung("Wahl 2").build(),
        ],
      };

      await wrapper.setProps(props);

      //Make components of both steps accessible; before click, only the 1st ist part of the dom
      wrapper.findComponent(VStepperActions).vm.$emit("click:next");
      await nextTick();

      const baseStepWahltagInitComponents =
        wrapper.findAllComponents(BaseStepWahltagInit);

      expect(
        baseStepWahltagInitComponents[0]?.props("wahltagEvent").nummer
      ).toStrictEqual(props.wahltagEvents[0]?.nummer);
      expect(
        baseStepWahltagInitComponents[1]?.props("wahltagEvent").nummer
      ).toStrictEqual(props.wahltagEvents[1]?.nummer);
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    describe("onImportWahlterminDatenDone", () => {
      it("should_triggerNotification_when_eventHandled", async () => {
        await wrapper.setProps({
          wahltagEvents: [prepareWahltagEvent().build()],
        });

        wrapper
          .findComponent(BaseStepWahltagInit)
          .vm.$emit("importWahlterminDatenDone");

        expect(mockDefinitions.addNotification).toHaveBeenCalledTimes(1);
      });
    });
  });
});
