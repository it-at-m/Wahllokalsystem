import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { flushPromises, mount, VueWrapper } from "@vue/test-utils";
import { storeToRefs } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { createRouter, createWebHistory } from "vue-router";

import App from "@/App.vue";
import { ROUTES_HOME } from "@/constants.ts";
import vuetify from "@/plugins/vuetify";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useMonitoringStore } from "@/stores/monitoringStore.ts";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";
import HomeView from "@/views/HomeView.vue";

const startBroadcastMessageIntervalMock = vi.fn();
const stopBroadcastMessageIntervalMock = vi.fn();

const mockDefinitions = vi.hoisted(() => ({
  getWahlen: vi.fn(),
}));

vi.mock("@/composables/wahl/wahlService.ts", () => ({
  useWahlService: () => ({
    getWahlen: mockDefinitions.getWahlen,
  }),
}));
vi.mock("@/composables/broadcast/broadcastCronjobService.ts", () => ({
  useBroadcastCronjobService: () => ({
    startBroadcastMessageInterval: startBroadcastMessageIntervalMock,
    stopBroadcastMessageInterval: stopBroadcastMessageIntervalMock,
  }),
}));

describe("App", () => {
  let wrapper: VueWrapper;

  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  vi.mock("@/components/wlsComponents/TheWlsAppBar.vue");
  vi.mock(
    "@/components/wahlvorstand/TheWahlvorstandAnwesenheitsCheckPopupDialog.vue",
    () => {
      return {
        default: {
          name: "TheWahlvorstandAnwesenheitsCheckPopupDialog",
          template: "<div>TheWahlvorstandAnwesenheitsCheckPopupDialog</div>",
        },
      };
    }
  );
  vi.mock("@/components/broadcast/TheBroadcastReadConfirmationDialog.vue");

  vi.mock("localforage");

  const router = createRouter({
    history: createWebHistory(),
    routes: [
      {
        path: "/",
        name: ROUTES_HOME,
        component: HomeView,
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
    it("should_renderWahlvorstandAnwesenheitsCheckPopupDialog_when_wahlbezirkArtUWB", async (context) => {
      const store = useUserStore();
      store.user.wahlbezirksArt = WahlbezirksArtEnum.UWB;

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
      const { initTasks } = useTaskManagerStore();

      await flushPromises();

      expect(initTasks).toHaveBeenCalled();
    });

    it("should_callLoadEreignisse_when_mounted", async () => {
      const { loadEreignisse } = useEreignisStore();

      await flushPromises();

      expect(loadEreignisse).toHaveBeenCalled();
    });

    it("should_callLoadWaehler_when_mounted", async () => {
      const { loadWaehler } = useMonitoringStore();

      await flushPromises();

      expect(loadWaehler).toHaveBeenCalled();
    });

    it("should_callInitBeanstandeteWahlbriefe_when_mountedAndWaehlerverzeichnisNummernAreGiven", async () => {
      const { initBeanstandeteWahlbriefe } = useWahlenStore();
      const { waehlerverzeichnisNummern } = storeToRefs(useWahlenStore());

      // @ts-expect-error: cannot set readonly
      waehlerverzeichnisNummern.value = [1];

      await flushPromises();

      expect(initBeanstandeteWahlbriefe).toHaveBeenCalled();
    });

    it("should_callStopBroadcastMessageInterval_when_unmounted", async () => {
      wrapper.unmount();

      await flushPromises();

      expect(stopBroadcastMessageIntervalMock).toHaveBeenCalled();
    });
  });
});
