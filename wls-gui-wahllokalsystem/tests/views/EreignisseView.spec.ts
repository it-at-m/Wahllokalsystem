import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";

import { createTestingPinia } from "@pinia/testing";
import { COMPONENT_RENDER_TESTS } from "@tests/utils/testutils.ts";
import { flushPromises, mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import vuetify from "@/plugins/vuetify";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";
import EreignisseView from "@/views/EreignisseView.vue";

describe("TheEreignisseView", () => {
  let wrapper: VueWrapper<InstanceType<typeof EreignisseView>>;

  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  beforeEach(() => {
    wrapper = mount(EreignisseView, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
          }),
          vuetify,
        ],
      },
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderSaveButtonEnabled_when_hasEintraegeIsFalseAndKeineEreignisseFlagsAreValid", async () => {
      const ereignisStore = useEreignisStore();

      // @ts-expect-error: cannot set readonly
      ereignisStore.areKeineEreignisseFlagsValid = true;
      // @ts-expect-error: cannot set readonly
      ereignisStore.hasEintraege = false;

      await flushPromises();

      const saveButton = wrapper.findComponent(BaseButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(false);
    });

    it("should_renderSaveButtonEnabled_when_hasEintraegeIsTrueWithValidDataAndKeineEreignisseFlagsAreValid", async () => {
      const ereignisStore = useEreignisStore();

      const validEreignis: Ereignis = {
        ereignisart: EreignisartEnum.Vorfall,
        uhrzeit: new Date(),
        beschreibung: "beschreibung",
      };
      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = [validEreignis];

      // @ts-expect-error: cannot set readonly
      ereignisStore.areKeineEreignisseFlagsValid = true;
      // @ts-expect-error: cannot set readonly
      ereignisStore.hasEintraege = true;

      await flushPromises();

      const saveButton = wrapper.findComponent(BaseButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(false);
    });

    it("should_renderSaveButtonDisabled_when_hasEintraegeIsTrueWithInvalidDataAndKeineEreignisseFlagsAreValid", async () => {
      const ereignisStore = useEreignisStore();

      const invalidEreignis: Ereignis = {
        ereignisart: EreignisartEnum.Vorfall,
        uhrzeit: new Date(),
        beschreibung: "",
      }; //pseudo event to set form invalid
      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = [invalidEreignis];

      // @ts-expect-error: cannot set readonly
      ereignisStore.areKeineEreignisseFlagsValid = true;
      // @ts-expect-error: cannot set readonly
      ereignisStore.hasEintraege = true;

      await flushPromises();

      const saveButton = wrapper.findComponent(BaseButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);
    });

    it("should_renderSaveButtonDisabled_when_hasEintraegeIsTrueWithValidDataAndKeineEreignisseFlagsAreInvalid", async () => {
      const ereignisStore = useEreignisStore();

      const validEreignis: Ereignis = {
        ereignisart: EreignisartEnum.Vorfall,
        uhrzeit: new Date(),
        beschreibung: "beschreibung",
      }; //pseudo event to set form invalid
      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = [validEreignis];

      // @ts-expect-error: cannot set readonly
      ereignisStore.areKeineEreignisseFlagsValid = false;
      // @ts-expect-error: cannot set readonly
      ereignisStore.hasEintraege = true;

      await flushPromises();

      const saveButton = wrapper.findComponent(BaseButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);
    });
  });
});
