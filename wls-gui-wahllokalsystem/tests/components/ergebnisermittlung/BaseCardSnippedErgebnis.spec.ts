import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { COMPONENT_EVENT_TESTS } from "@tests/utils/testutils.ts";
import {
  enableAutoUnmount,
  flushPromises,
  mount,
  VueWrapper,
} from "@vue/test-utils";
import { afterEach, describe, expect, it } from "vitest";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseCardSnippedErgebnis from "@/components/ergebnisermittlung/BaseCardSnippedErgebnis.vue";
import pinia from "@/plugins/pinia.ts";
import vuetify from "@/plugins/vuetify.ts";

const { prepareErgebnis } = useErgebnisseTestDataFactory();

describe("BaseCardSnippedErgebnis.vue", () => {
  let wrapper: VueWrapper;

  enableAutoUnmount(afterEach);

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_disableSaveButton_when_formIsInvalidDueToDefaultLimit", async () => {
      wrapper = mount(BaseCardSnippedErgebnis, {
        global: { plugins: [pinia, vuetify] },
        props: {
          modelValue: prepareErgebnis().ergebnis(10000).build(),
          snippedTitle: "BaseCard",
        },
      });

      const saveButton = wrapper.findComponent(BaseButtonSave);

      expect(saveButton.props("disabled")).toStrictEqual(true);
    });

    it("should_disableSaveButton_when_formIsInvalidDueToCustomLimit", async () => {
      wrapper = mount(BaseCardSnippedErgebnis, {
        global: { plugins: [pinia, vuetify] },
        props: {
          modelValue: prepareErgebnis().ergebnis(5).build(),
          snippedTitle: "BaseCard",
          minValue: 10,
        },
      });

      const saveButton = wrapper.findComponent(BaseButtonSave);

      expect(saveButton.props("disabled")).toStrictEqual(true);
    });

    it("should_emitSaveEvent_when_saveButtonIsClicked", async () => {
      wrapper = mount(BaseCardSnippedErgebnis, {
        global: { plugins: [pinia, vuetify] },
        props: {
          modelValue: prepareErgebnis().ergebnis(20).build(),
          snippedTitle: "BaseCard",
        },
      });

      await flushPromises();

      await wrapper.findComponent(BaseButtonSave).trigger("click");

      expect(wrapper.emitted()).toHaveProperty("save");
    });
  });
});
