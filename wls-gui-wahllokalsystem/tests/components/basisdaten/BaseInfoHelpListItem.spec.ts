import type { VueWrapper } from "@vue/test-utils";

import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
import { VHover, VListItem } from "vuetify/components";

import BaseInfoHelpListItem from "@/components/basisdaten/BaseInfoHelpListItem.vue";
import BaseInfoHelpListItemContent from "@/components/basisdaten/BaseInfoHelpListItemContent.vue";
import { useLogging } from "@/composables/common/logging.ts";
import vuetify from "@/plugins/vuetify.ts";

const { logDebug } = useLogging("BaseInfoHelpItemList.spec.ts");

describe("BaseInfoHelpListItem.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseInfoHelpListItem>>;

  beforeEach(() => {
    wrapper = mount(BaseInfoHelpListItem, {
      global: {
        plugins: [vuetify],
        stubs: {
          // definiert den stub für v-hover, um das hover-event korrekt zu simulieren
          VHover: {
            template: `
                <div @mouseenter="handleMouseEnter">
                  <slot :isHovering="isHovering" :props="props" />
                </div>
              `,
            data() {
              return {
                isHovering: false,
                props: {},
              };
            },
            methods: {
              handleMouseEnter() {
                this.isHovering = true;
              },
            },
          },
        },
      },
      props: { title: "Anderer Titel", icon: "icon" },
    });
  });
  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_notChangeBgColorOnHover_when_itemHasNoCallback", async (context) => {
      const vHover = wrapper.findComponent(VHover);
      await vHover.trigger("mouseenter");

      await nextTick();

      const vListItem = wrapper.findComponent(VListItem);

      expect(vListItem.classes()).not.toContain("bg-grey-lighten-3");
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_changeBgColorOnHover_when_itemHasCallback", async (context) => {
      await wrapper.setProps({
        title: "Neuer Titel",
        icon: "icon",
        callback: () => {
          logDebug("callback executed");
        },
      });

      const vHover = wrapper.findComponent(VHover);
      await vHover.trigger("mouseenter");

      await nextTick();

      const vListItem = wrapper.findComponent(VListItem);

      expect(vListItem.classes()).toContain("bg-grey-lighten-3");
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_executeCallbackFunction_when_listItemWasClicked", async () => {
      const mockCallback = vi.fn();
      await wrapper.setProps({
        title: "Titel",
        icon: "icon",
        callback: mockCallback,
      });

      const item = wrapper.findComponent(BaseInfoHelpListItemContent);
      await item.trigger("click");
      await nextTick();

      expect(mockCallback).toHaveBeenCalled();
    });
  });
});
