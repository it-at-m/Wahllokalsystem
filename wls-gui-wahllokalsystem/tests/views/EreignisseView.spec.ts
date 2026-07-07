import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
  mockAndStubResizeObserver,
} from "@tests/utils/testutils.ts";
import { flushPromises, mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
import { createRouter, createWebHistory } from "vue-router";

import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import {
  CONTINUE_QUERY_PARAM,
  ROUTE_EREIGNISSE,
  ROUTES_HOME,
  SAVE_CONTINUE,
} from "@/constants.ts";
import vuetify from "@/plugins/vuetify";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";
import EreignisseView from "@/views/EreignisseView.vue";
import HomeView from "@/views/HomeView.vue";

const mockDefinitions = vi.hoisted(() => ({
  getNextRoute: vi.fn(),
}));

vi.mock(
  import("@/composables/navigation/navigationService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useNavigationService: () => ({
        ...mod.useNavigationService(),
        getNextRoute: mockDefinitions.getNextRoute,
      }),
    };
  }
);

describe("TheEreignisseView", () => {
  let wrapper: VueWrapper<InstanceType<typeof EreignisseView>>;

  mockAndStubResizeObserver();

  const routes = [
    {
      path: "/",
      name: ROUTES_HOME,
      component: HomeView,
      meta: {},
    },
    {
      path: "/ereignisse",
      name: ROUTE_EREIGNISSE,
      component: EreignisseView,
    },
  ];

  const router = createRouter({
    history: createWebHistory(),
    routes,
  });

  beforeEach(() => {
    wrapper = mount(EreignisseView, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
          }),
          vuetify,
          router,
        ],
      },
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderSaveButtonEnabled_when_hasEintraegeIsFalseAndEreignisFlagsAndEreigniseintraegeConsistent", async () => {
      const ereignisStore = useEreignisStore();

      // @ts-expect-error: cannot set readonly
      ereignisStore.isEreignisFlagsAndEreigniseintraegeInconsistent = false;
      // @ts-expect-error: cannot set readonly
      ereignisStore.hasEintraege = false;

      await flushPromises();

      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(false);
    });

    it("should_renderSaveButtonEnabled_when_hasEintraegeIsTrueWithValidDataAndEreignisFlagsAndEreigniseintraegeConsistent", async () => {
      const ereignisStore = useEreignisStore();
      const userStore = useUserStore();

      // @ts-expect-error: cannot set readonly
      userStore.currentUserWahltag = "2025-01-01";

      const validEreignis: Ereignis = {
        ereignisart: EreignisartEnum.Vorfall,
        uhrzeit: new Date(),
        beschreibung: "beschreibung",
      };
      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = [validEreignis];

      // @ts-expect-error: cannot set readonly
      ereignisStore.isEreignisFlagsAndEreigniseintraegeInconsistent = false;
      // @ts-expect-error: cannot set readonly
      ereignisStore.hasEintraege = true;

      await flushPromises();

      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(false);
    });

    it("should_renderSaveButtonDisabled_when_hasEintraegeIsTrueWithInvalidDataAndEreignisFlagsAndEreigniseintraegeConsistent", async () => {
      const ereignisStore = useEreignisStore();

      const invalidEreignis: Ereignis = {
        ereignisart: EreignisartEnum.Vorfall,
        uhrzeit: new Date(),
        beschreibung: "",
      }; //pseudo event to set form invalid
      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = [invalidEreignis];

      // @ts-expect-error: cannot set readonly
      ereignisStore.isEreignisFlagsAndEreigniseintraegeInconsistent = false;
      // @ts-expect-error: cannot set readonly
      ereignisStore.hasEintraege = true;

      await flushPromises();

      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);
    });

    it("should_renderSaveButtonDisabled_when_hasEintraegeIsTrueWithValidDataAndEreignisFlagsAndEreigniseintraegeInconsistent", async () => {
      const ereignisStore = useEreignisStore();

      const validEreignis: Ereignis = {
        ereignisart: EreignisartEnum.Vorfall,
        uhrzeit: new Date(),
        beschreibung: "beschreibung",
      }; //pseudo event to set form invalid
      ereignisStore.wahlbezirkEreignisse.ereigniseintraege = [validEreignis];

      // @ts-expect-error: cannot set readonly
      ereignisStore.isEreignisFlagsAndEreigniseintraegeInconsistent = true;
      // @ts-expect-error: cannot set readonly
      ereignisStore.hasEintraege = true;

      await flushPromises();

      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);
    });

    it("should_renderSaveButtonInLoadingState_when_isSavingIsTrue", async (context) => {
      const ereignisStore = useEreignisStore();
      ereignisStore.isSaving = true;

      await nextTick();
      await flushPromises();

      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      await expect(saveButton.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_navigateToNextRoute_when_parameterIsSet", async () => {
      await router.push("/ereignisse?" + CONTINUE_QUERY_PARAM + "=1");
      await nextTick();

      const ereignisStore = useEreignisStore();

      // @ts-expect-error: cannot set readonly
      ereignisStore.isEreignisFlagsAndEreigniseintraegeInconsistent = false;
      // @ts-expect-error: cannot set readonly
      ereignisStore.hasEintraege = false;

      await flushPromises();

      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      expect(saveButton.text()).toStrictEqual(SAVE_CONTINUE);

      mockDefinitions.getNextRoute.mockReturnValue({
        name: ROUTES_HOME,
      });

      await saveButton.trigger("click");

      expect(mockDefinitions.getNextRoute).toHaveBeenCalled();
    });
  });
});
