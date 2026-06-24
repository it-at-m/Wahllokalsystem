import { useKonfigurationTestDataFactory } from "@tests/types/config/KonfigurationTestDataFactory.ts";
import { flushPromises, mount, VueWrapper } from "@vue/test-utils";
import { afterEach, describe, expect, it, vi } from "vitest";

import BaseCardConfigParameterDisplay from "@/components/config-parameter/BaseCardConfigParameterDisplay.vue";
import BaseDialogConfigParameterDisplay from "@/components/config-parameter/BaseDialogConfigParameterDisplay.vue";
import vuetify from "@/plugins/vuetify.ts";
import KonfigurationParameterView from "@/views/KonfigurationParameterView.vue";

const mockDefinitions = vi.hoisted(() => ({
  getKonfigurations: vi.fn(),
  saveKonfiguration: vi.fn(),
}));

vi.mock(import("@/composables/konfiguration/konfigurationService.ts"), () => ({
  useKonfigurationService: () => ({
    getKonfigurations: mockDefinitions.getKonfigurations,
    saveKonfiguration: mockDefinitions.saveKonfiguration,
  }),
}));

const { createConfigParameterComplete } = useKonfigurationTestDataFactory();

describe("KonfigurationParameterView.vue", () => {
  vi.stubGlobal("visualViewport", new EventTarget());

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("loading", () => {
    it("should_renderOneCardPerConfigParameter_when_mounted", async () => {
      const configParameters = [
        createConfigParameterComplete(),
        createConfigParameterComplete(),
        createConfigParameterComplete(),
      ];
      mockDefinitions.getKonfigurations.mockResolvedValue(configParameters);

      const wrapper = setupWrapper();
      await flushPromises();

      expect(mockDefinitions.getKonfigurations).toHaveBeenCalledTimes(1);
      expect(
        wrapper.findAllComponents(BaseCardConfigParameterDisplay)
      ).toHaveLength(configParameters.length);
    });

    it("should_reloadConfigParameters_when_refreshButtonIsClicked", async () => {
      mockDefinitions.getKonfigurations.mockResolvedValue([]);

      const wrapper = setupWrapper();
      await flushPromises();

      await wrapper.find('[data-test="refreshButton"]').trigger("click");
      await flushPromises();

      expect(mockDefinitions.getKonfigurations).toHaveBeenCalledTimes(2);
    });
  });

  describe("editing", () => {
    it("should_openDialogForSelectedParameter_when_cardEmitsClickEdit", async () => {
      const configParameter = createConfigParameterComplete();
      mockDefinitions.getKonfigurations.mockResolvedValue([configParameter]);

      const wrapper = setupWrapper();
      await flushPromises();

      expect(
        wrapper.findComponent(BaseDialogConfigParameterDisplay).exists()
      ).toBe(false);

      await wrapper
        .findComponent(BaseCardConfigParameterDisplay)
        .vm.$emit("clickEdit", configParameter.name);
      await flushPromises();

      const dialog = wrapper.findComponent(BaseDialogConfigParameterDisplay);
      expect(dialog.exists()).toBe(true);
      expect(dialog.props("configParameter")).toStrictEqual(configParameter);
    });

    it("should_saveAndReload_when_dialogEmitsCommitEdit", async () => {
      const configParameter = createConfigParameterComplete();
      mockDefinitions.getKonfigurations.mockResolvedValue([configParameter]);
      mockDefinitions.saveKonfiguration.mockResolvedValue(true);

      const wrapper = setupWrapper();
      await flushPromises();

      await wrapper
        .findComponent(BaseCardConfigParameterDisplay)
        .vm.$emit("clickEdit", configParameter.name);
      await flushPromises();

      const editedConfigParameter = { ...configParameter, wert: "neuerWert" };
      await wrapper
        .findComponent(BaseDialogConfigParameterDisplay)
        .vm.$emit("commitEdit", editedConfigParameter);
      await flushPromises();

      expect(mockDefinitions.saveKonfiguration).toHaveBeenCalledWith(
        editedConfigParameter
      );
      expect(mockDefinitions.getKonfigurations).toHaveBeenCalledTimes(2);
      expect(
        wrapper.findComponent(BaseDialogConfigParameterDisplay).exists()
      ).toBe(false);
    });

    it("should_notSave_when_dialogEmitsCancelEdit", async () => {
      const configParameter = createConfigParameterComplete();
      mockDefinitions.getKonfigurations.mockResolvedValue([configParameter]);

      const wrapper = setupWrapper();
      await flushPromises();

      await wrapper
        .findComponent(BaseCardConfigParameterDisplay)
        .vm.$emit("clickEdit", configParameter.name);
      await flushPromises();

      await wrapper
        .findComponent(BaseDialogConfigParameterDisplay)
        .vm.$emit("cancelEdit");
      await flushPromises();

      expect(mockDefinitions.saveKonfiguration).not.toHaveBeenCalled();
      expect(
        wrapper.findComponent(BaseDialogConfigParameterDisplay).exists()
      ).toBe(false);
    });
  });
});

function setupWrapper(): VueWrapper {
  return mount(KonfigurationParameterView, {
    global: { plugins: [vuetify] },
  });
}
