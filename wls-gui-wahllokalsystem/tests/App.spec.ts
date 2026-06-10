import { createTestingPinia } from "@pinia/testing";
import { useKonfigurationsparameterTestDataFactory } from "@tests/utils/infomanagement/KonfigurationsparameterTestDataFactory.ts";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
  mockAndStubResizeObserver,
  stubVisualViewport,
} from "@tests/utils/testutils.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { flushPromises, mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { defineComponent } from "vue";
import { createRouter, createWebHistory } from "vue-router";

import App from "@/App.vue";
import { ROUTE_WAHLVORSTAND, ROUTES_HOME } from "@/constants.ts";
import vuetify from "@/plugins/vuetify";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useInitTaskManagerStore } from "@/stores/initTaskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";
import HomeView from "@/views/HomeView.vue";
import WahlvorstandAnwesenheitView from "@/views/WahlvorstandAnwesenheitView.vue";

const startBroadcastMessageIntervalMock = vi.fn();
const stopBroadcastMessageIntervalMock = vi.fn();

const mockDefinitions = vi.hoisted(() => ({
  awaitServiceWorkerActive: vi.fn(),
  getWahlen: vi.fn(),
  postBeanstandeteWahlbriefe: vi.fn(),
  getBeanstandeteWahlbriefe: vi.fn(),
  syncPin: vi.fn(),
}));

vi.mock(import("@/composables/wahl/wahlService.ts"), () => ({
  useWahlService: () => ({
    getWahlen: mockDefinitions.getWahlen,
  }),
}));
vi.mock(
  import("@/composables/briefwahl/briefwahlService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useBriefwahlService: () => ({
        ...mod.useBriefwahlService(),
        postBeanstandeteWahlbriefe: mockDefinitions.postBeanstandeteWahlbriefe,
        getBeanstandeteWahlbriefe: mockDefinitions.getBeanstandeteWahlbriefe,
      }),
    };
  }
);
vi.mock(import("@/composables/broadcast/broadcastCronjobService.ts"), () => ({
  useBroadcastCronjobService: () => ({
    startBroadcastMessageInterval: startBroadcastMessageIntervalMock,
    stopBroadcastMessageInterval: stopBroadcastMessageIntervalMock,
  }),
}));
vi.mock(
  import("@/composables/serviceWorker/serviceWorkerPinSyncer.ts"),
  () => ({
    useServiceWorkerPinSyncer: () => ({
      syncPin: mockDefinitions.syncPin,
    }),
  })
);
vi.mock(
  import("@/composables/serviceWorker/serviceWorkerUtils.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useServiceWorkerUtils: () => ({
        ...mod.useServiceWorkerUtils(),
        awaitServiceWorkerActive: mockDefinitions.awaitServiceWorkerActive,
      }),
    };
  }
);
vi.mock(import("@/components/wlsComponents/TheWlsAppBar.vue"));
vi.mock(
  import("@/components/wahlvorstand/TheWahlvorstandAnwesenheitsCheckPopupDialog.vue"),
  () => {
    return {
      default: defineComponent({
        name: "TheWahlvorstandAnwesenheitsCheckPopupDialog",
        template: "<div>TheWahlvorstandAnwesenheitsCheckPopupDialog</div>",
      }),
    };
  }
);
vi.mock(
  import("@/components/broadcast/TheBroadcastReadConfirmationDialog.vue")
);
vi.mock(import("localforage"));

const { prepareKonfigurationsparameter } =
  useKonfigurationsparameterTestDataFactory();

describe("App", () => {
  let wrapper: VueWrapper;

  mockAndStubResizeObserver();
  stubVisualViewport();

  const router = createRouter({
    history: createWebHistory(),
    routes: [
      {
        path: "/",
        name: ROUTES_HOME,
        component: HomeView,
        meta: {},
      },
      {
        path: "/wahlvorstand",
        name: ROUTE_WAHLVORSTAND,
        component: WahlvorstandAnwesenheitView,
        meta: {},
      },
    ],
  });

  beforeEach(() => {
    wrapper = mount(App, {
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

    const { createWahl } = useWahlTestDataFactory();

    const mockedWahlArrayFromService = [createWahl(), createWahl()];

    mockDefinitions.getWahlen.mockReturnValue(
      Promise.resolve(mockedWahlArrayFromService)
    );
  });

  afterEach(() => {
    vi.clearAllMocks();
    if (wrapper) wrapper.unmount();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWahlvorstandAnwesenheitsCheckPopupDialog_when_wahlbezirkArtUWBAndCheckTimeIsInFuture", async (context) => {
      router.push = vi.fn();

      const store = useUserStore();
      store.user.wahlbezirksArt = WahlbezirksArtEnum.UWB;
      const now = new Date();
      // @ts-expect-error: cannot set readonly
      store.currentUserWahltag = `${now.getFullYear() + 1}-12-31`;
      useInfomanagementStore().konfigurationsparameter = [
        prepareKonfigurationsparameter()
          .schluessel("MELDUNGSZEIT_ANWESENHEIT_CHECK")
          .wert("23:59:59")
          .build(),
      ];

      await flushPromises();

      expect(
        wrapper
          .findComponent(
            '[data-test="wahlvorstand-anwesenheits-check-popup-dialog"]'
          )
          .exists()
      ).toBe(true);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_notRenderWahlvorstandAnwesenheitsCheckPopupDialog_when_wahlbezirkArtBWB", async (context) => {
      router.push = vi.fn();

      const store = useUserStore();
      store.user.wahlbezirksArt = WahlbezirksArtEnum.BWB;

      await flushPromises();
      expect(
        wrapper
          .findComponent(
            '[data-test="wahlvorstand-anwesenheits-check-popup-dialog"]'
          )
          .exists()
      ).toBe(false);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_callLoadUser_when_mounted", async () => {
      const { loadUser } = useUserStore();

      expect(loadUser).toHaveBeenCalled();
    });

    it("should_callStartBroadcastMessageInterval_when_mounted", async () => {
      await flushPromises();

      expect(startBroadcastMessageIntervalMock).toHaveBeenCalled();
    });

    it("should_callInitTasks_when_mounted", async () => {
      const { initTasks } = useInitTaskManagerStore();

      await flushPromises();

      expect(initTasks).toHaveBeenCalled();
    });

    it("should_callStopBroadcastMessageInterval_when_unmounted", async () => {
      wrapper.unmount();

      await flushPromises();

      expect(stopBroadcastMessageIntervalMock).toHaveBeenCalled();
    });

    it("should_callAwaitServiceWorkerActive_when_mounted", async () => {
      wrapper.unmount();

      await flushPromises();

      expect(mockDefinitions.awaitServiceWorkerActive).toHaveBeenCalled();
    });

    it("should_callSyncPin_when_mounted", async () => {
      wrapper.unmount();

      await flushPromises();

      expect(mockDefinitions.syncPin).toHaveBeenCalled();
    });
  });
});
