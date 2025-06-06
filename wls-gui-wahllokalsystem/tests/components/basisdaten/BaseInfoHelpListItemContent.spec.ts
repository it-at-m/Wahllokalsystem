import type { VueWrapper } from "@vue/test-utils";

import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount } from "@vue/test-utils";
import { afterEach, describe, expect, it } from "vitest";

import BaseInfoHelpListItemContent from "@/components/basisdaten/BaseInfoHelpListItemContent.vue";
import vuetify from "@/plugins/vuetify.ts";

describe("BaseInfoHelpListItemContent.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseInfoHelpListItemContent>>;

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderListItemContentWithMinimumRequiredProps_when_mountedWithTitleAndIconProp", async (context) => {
      wrapper = mount(BaseInfoHelpListItemContent, {
        global: {
          plugins: [vuetify],
        },
        props: { title: "Beispiel", icon: "icon" },
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderListItemWithAllProps_when_mountedWithTitleTextAndIconProp", async (context) => {
      wrapper = mount(BaseInfoHelpListItemContent, {
        global: {
          plugins: [vuetify],
        },
        props: { title: "Beispiel", text: "Ich bin ein Text", icon: "icon" },
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
