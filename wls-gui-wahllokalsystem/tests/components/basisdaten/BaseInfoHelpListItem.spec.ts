import type { VueWrapper } from "@vue/test-utils";

import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it } from "vitest";
import { nextTick } from "vue";
import { VHover, VListItem } from "vuetify/components";

import BaseInfoHelpListItem from "@/components/basisdaten/BaseInfoHelpListItem.vue";
import vuetify from "@/plugins/vuetify.ts";

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
          console.log("callback executed");
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
});
