import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";

import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { beforeEach, describe, expect, it } from "vitest";

import BaseDialogConfigParameterDisplay from "@/components/config-parameter/BaseDialogConfigParameterDisplay.vue";
import vuetify from "@/plugins/vuetify.ts";

// Testdaten für Event und Mounted
const configParameter: InfomanagementConfigParameter[] = [
  {
    name: "Willkommenstext",
    beschreibung: "Begrüßungstext auf der Anmeldemaske",
    wert: "Herzlich willkommen zur Wahl!",
    defaultValue: "Herzlich willkommen zur Testwahl!",
  },
];

describe("BaseDialogConfigParameterDisplay.vue", () => {
  let wrapper: VueWrapper;
  const mountComponent = (config: InfomanagementConfigParameter[]) => {
    wrapper = mount(BaseDialogConfigParameterDisplay, {
      props: { configParameter: config[0] },
      global: { plugins: [vuetify] },
    });
  };

  //ToDo Rendertest
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

  //ToDo Event Test cancelEdit, commitEdit und resetValue
  describe(COMPONENT_EVENT_TESTS, () => {
    beforeEach(() => {
      mountComponent(configParameter);
    });
    describe("cancelEdit", () => {
      it("should_emitCancelEditConfigParameterName_when_CancelButtonIsClicked", async () => {
        // Button per data-test selektieren
        const button = wrapper.find('[data-test="cancel-edit-button"]');
        await cancelButton.trigger("click");
        const emitted = wrapper.emitted("cancelEdit");
        expect(emitted).toBeDefined();
        expect(wrapper.emitted("cancelDelete")).toEqual([[]]);
      });
    });

    it("should_emitCommitEditConfigParameterValue_when_CommitButtonIsClicked", async () => {
      // Button per data-test selektieren
      const button = wrapper.find('[data-test="commit-edit-button"]');
      await button.trigger("click");
      const emitted = wrapper.emitted("commitEdit");
      expect(emitted).toBeDefined();
      expect(emitted?.[0]?.[0]).toBe(configParameter[0].name);
    });
  });
});
