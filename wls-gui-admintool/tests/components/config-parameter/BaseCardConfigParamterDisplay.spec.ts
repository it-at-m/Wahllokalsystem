import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { beforeEach, describe, expect, it } from "vitest";
import vuetify from "@/plugins/vuetify.ts";
import BaseCardConfigParameterDisplay from "@/components/config-parameter/BaseCardConfigParameterDisplay.vue";
import { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";

// Testdaten für Event
const configParameter: InfomanagementConfigParameter[] = [
  {
    name: "Willkommenstext",
    beschreibung: "Begrüßungstext auf der Anmeldemaske",
    wert: "Herzlich willkommen zur Wahl!",
    defaultValue: "Herzlich willkommen zur Testwahl!",
  },
];

describe("BaseCardConfigParameterDisplay.vue", () => {
  let wrapper: VueWrapper<typeof BaseCardConfigParameterDisplay>;
  const mountComponent = (config: InfomanagementConfigParameter) => {
    wrapper = mount(BaseCardConfigParameterDisplay, {
      props: { configParameter: config[0] },
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

  //Payload Ausgabe
  describe(COMPONENT_EVENT_TESTS, () => {
    beforeEach(() => {
      mountComponent(configParameter);
    });

    it("should_emitConfigParameterName_when_ButtonIsClicked", async () => {
      // Button per data-test selektieren
      const button = wrapper.find('[data-test="confirm-edit-button"]');
      await button.trigger("click");
      expect(wrapper.emitted()).toHaveProperty("confirmEdit");
      expect(wrapper.emitted("confirmEdit")![0]).toEqual([configParameter[0].name]);
    });
  });
});


