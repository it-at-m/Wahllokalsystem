import type { TestingPinia } from "@pinia/testing";
import type { VueWrapper } from "@vue/test-utils";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
  mockAndStubResizeObserver,
  stubVisualViewport,
} from "@tests/utils/testutils.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { enableAutoUnmount, flushPromises, mount } from "@vue/test-utils";
import { createPinia } from "pinia";
import {
  afterEach,
  beforeAll,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { nextTick } from "vue";
import { VBtn } from "vuetify/components";

import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BeanstandeteWahlbriefeCard from "@/components/wahlhandlung/beanstandeteWahlbriefe/BeanstandeteWahlbriefeCard.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  postBeanstandeteWahlbriefe: vi.fn(),
}));
vi.mock(
  import("@/composables/briefwahl/briefwahlService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useBriefwahlService: () => ({
        ...mod.useBriefwahlService(),
        postBeanstandeteWahlbriefe: mockDefinitions.postBeanstandeteWahlbriefe,
      }),
    };
  }
);

describe("BeanstandeteWahlbriefeCard", () => {
  let wrapper: VueWrapper;
  let pinia: TestingPinia;

  const { prepareWahl } = useWahlTestDataFactory();

  mockAndStubResizeObserver();
  stubVisualViewport();

  beforeAll(() => {
    createPinia();
  });

  beforeEach(async () => {
    pinia = createTestingPinia({ createSpy: vi.fn, stubActions: false });
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWithEnabledSaveButton_when_noRowsGiven", async (context) => {
      wrapper = mount(BeanstandeteWahlbriefeCard, {
        global: {
          plugins: [pinia, vuetify],
        },
        props: {
          triggersNavigation: true,
        },
      });

      const saveButton = wrapper.findComponent<typeof VBtn>(BaseWlsButtonSave);

      expect(saveButton.props("disabled")).toBe(false);
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithDisabledSaveButton_when_rowsAreInvalid", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .name("Wahl1")
          .wahlID("id1")
          .beanstandeteWahlbriefe(["KEIN_ORIGINAL_SCHEIN"])
          .build(),
        prepareWahl()
          .name("Wahl2")
          .wahlID("id2")
          .beanstandeteWahlbriefe([null])
          .build(),
      ];

      wrapper = mount(BeanstandeteWahlbriefeCard, {
        global: {
          plugins: [pinia, vuetify],
        },
        props: {
          triggersNavigation: false,
        },
      });

      await flushPromises();

      const saveButton = wrapper.findComponent<typeof VBtn>(BaseWlsButtonSave);

      expect(saveButton.props("disabled")).toBe(true);
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithEnabledSaveButton_when_rowsAreValid", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .name("Wahl1")
          .wahlID("id1")
          .beanstandeteWahlbriefe(["KEIN_ORIGINAL_SCHEIN"])
          .build(),
        prepareWahl()
          .name("Wahl2")
          .wahlID("id2")
          .beanstandeteWahlbriefe(["KEIN_ORIGINAL_SCHEIN"])
          .build(),
      ];

      wrapper = mount(BeanstandeteWahlbriefeCard, {
        global: {
          plugins: [pinia, vuetify],
        },
        props: {
          triggersNavigation: true,
        },
      });

      await flushPromises();

      const saveButton = wrapper.findComponent<typeof VBtn>(BaseWlsButtonSave);

      expect(saveButton.props("disabled")).toBe(false);
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithSaveButtonInLoadingState_when_isSavingIsTrue", async (context) => {
      wrapper = mount(BeanstandeteWahlbriefeCard, {
        global: {
          plugins: [pinia, vuetify],
        },
        props: {
          triggersNavigation: false,
        },
      });

      const wahlenStore = useWahlenStore();
      wahlenStore.beanstandeteWahlbriefeState.isBeanstandeteWahlbriefeSaving = true;

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_callSendBeanstandeteWahlbriefe_when_saveButtonIsClicked", async () => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .name("Wahl1")
          .wahlID("id1")
          .beanstandeteWahlbriefe(["ZUGELASSEN"])
          .build(),
        prepareWahl()
          .name("Wahl2")
          .wahlID("id2")
          .beanstandeteWahlbriefe(["ZUGELASSEN"])
          .build(),
      ];

      wrapper = mount(BeanstandeteWahlbriefeCard, {
        global: {
          plugins: [pinia, vuetify],
        },
        props: {
          triggersNavigation: true,
        },
      });

      await flushPromises(); // wait for save button to be enabled

      const saveButton = wrapper.findComponent<typeof VBtn>(BaseWlsButtonSave);
      expect(saveButton.props("disabled")).toBe(false);

      await saveButton.trigger("click");

      mockDefinitions.postBeanstandeteWahlbriefe.mockReturnValue(
        Promise.resolve()
      );

      expect(mockDefinitions.postBeanstandeteWahlbriefe).toHaveBeenCalled();
    });

    it("should_callAddBeanstandeteWahlbriefe_when_addRowButtonIsClicked", async () => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .name("Wahl1")
          .wahlID("id1")
          .beanstandeteWahlbriefe(["ZUGELASSEN"])
          .build(),
      ];

      const addBeanstandeterWahlbriefEntrySpy = vi.spyOn(
        useWahlenStore().beanstandeteWahlbriefeActions,
        "addBeanstandeterWahlbriefEntry"
      );

      wrapper = mount(BeanstandeteWahlbriefeCard, {
        global: {
          plugins: [pinia, vuetify],
        },
        props: {
          triggersNavigation: true,
        },
      });

      const addRowButton = wrapper.findComponent<typeof VBtn>(
        `[data-test="addBedenklicherWahlbriefRow"]`
      );
      await addRowButton.trigger("click");

      expect(addBeanstandeterWahlbriefEntrySpy).toHaveBeenCalled();
    });
  });
});
