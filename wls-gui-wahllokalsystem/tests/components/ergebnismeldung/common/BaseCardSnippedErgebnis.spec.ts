import type { TestingPinia } from "@pinia/testing";

import { createTestingPinia } from "@pinia/testing";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { COMPONENT_EVENT_TESTS } from "@tests/utils/testutils.ts";
import {
  enableAutoUnmount,
  flushPromises,
  mount,
  VueWrapper,
} from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseCardSnippedErgebnis from "@/components/ergebnismeldung/common/BaseCardSnippedErgebnis.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";

const { prepareErgebnis } = useErgebnisseTestDataFactory();

describe("BaseCardSnippedErgebnis.vue", () => {
  let wrapper: VueWrapper;
  let pinia: TestingPinia;

  beforeEach(() => {
    pinia = createTestingPinia({ createSpy: vi.fn, stubActions: false });

    // @ts-expect-error: cannot set readonly
    useWorkflowStore().areAllElectionsFinished = false;
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_disableSaveButton_when_formIsInvalidDueToDefaultLimit", async () => {
      wrapper = mount(BaseCardSnippedErgebnis, {
        global: { plugins: [pinia, vuetify] },
        props: {
          modelValue: prepareErgebnis().ergebnis(10000).build(),
          snippedTitle: "BaseCard",
          isWahlFinished: false,
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
          isWahlFinished: false,
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
          isWahlFinished: false,
        },
      });

      await flushPromises();

      await wrapper.findComponent(BaseButtonSave).trigger("click");

      expect(wrapper.emitted()).toHaveProperty("save");
    });
  });
});
