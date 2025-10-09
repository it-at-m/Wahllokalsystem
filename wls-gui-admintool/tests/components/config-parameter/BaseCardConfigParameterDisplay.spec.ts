import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";

import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { beforeEach, describe, expect, it } from "vitest";

import BaseCardConfigParameterDisplay from "@/components/config-parameter/BaseCardConfigParameterDisplay.vue";
import vuetify from "@/plugins/vuetify.ts";

const configParameter: InfomanagementConfigParameter[] = [
  {
    name: "Willkommenstext",
    beschreibung: "Begrüßungstext auf der Anmeldemaske",
    wert: "Herzlich willkommen zur Wahl!",
    defaultValue: "Herzlich willkommen zur Testwahl!",
  },
];

describe("BaseCardConfigParameterDisplay.vue", () => {
  let wrapper: VueWrapper;
  const mountComponent = (config: InfomanagementConfigParameter[]) => {
    wrapper = mount(BaseCardConfigParameterDisplay, {
      // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
      props: { configParameter: config[0]! },
      global: { plugins: [vuetify] },
    });
  };

  describe(COMPONENT_RENDER_TESTS, () => {
    beforeEach(() => {
      mountComponent(configParameter);
    });

    it("should_renderCorrectly_when_componentIsMounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    beforeEach(() => {
      mountComponent(configParameter);
    });

    it("should_emitConfigParameterName_when_ButtonIsClicked", async () => {
      const button = wrapper.find('[data-test="click-edit-button"]');
      await button.trigger("click");
      const emitted = wrapper.emitted("clickEdit");
      expect(emitted).toBeDefined();
      // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
      expect(emitted?.[0]?.[0]).toBe(configParameter[0]?.name);
    });
  });
});
