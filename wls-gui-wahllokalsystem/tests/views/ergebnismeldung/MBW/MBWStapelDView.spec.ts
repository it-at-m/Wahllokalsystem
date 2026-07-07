import type { RouteLocationNormalizedLoaded } from "vue-router";

import { createKeepAliveComponent } from "@tests/utils/components/keepAliveComponent.ts";
import {
  COMPONENT_EVENT_TESTS,
  mockAndStubResizeObserver,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { createRouter, createWebHistory } from "vue-router";

import BaseCardUngueltigeStimmzettelErfassen from "@/components/ergebnismeldung/MBW/stapelD/BaseCardUngueltigeStimmzettelErfassen.vue";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import pinia from "@/plugins/pinia.ts";
import vuetify from "@/plugins/vuetify.ts";
import MBWStapelDView from "@/views/ergebnismeldung/MBW/MBWStapelDView.vue";
import HomeView from "@/views/HomeView.vue";
import WahlvorstandAnwesenheitView from "@/views/WahlvorstandAnwesenheitView.vue";

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisse: vi.fn(),
  postErgebnisse: vi.fn(),
  getNextRoute: vi.fn(),
  getBedenklicheStimmzettel: vi.fn(),
}));

vi.mock(import("vue-router"), async (importOriginal) => {
  const module = (await importOriginal()) as object;
  return {
    ...module,
    useRoute: () =>
      ({
        path: "path",
        name: "mocked route",
        query: {},
        fullPath: "",
        matched: [],
        params: {
          wahlId: "wahlID",
          wahlbezirkId: "wahlbezirkID",
        },
        hash: "",
        meta: {},
        redirectedFrom: undefined,
      }) as RouteLocationNormalizedLoaded,
  };
});

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

vi.mock(
  import("@/composables/ergebnismeldung/common/ergebnisService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useErgebnisService: () => ({
        ...mod.useErgebnisService(),
        postErgebnisse: mockDefinitions.postErgebnisse,
        getErgebnisse: mockDefinitions.getErgebnisse,
      }),
    };
  }
);
vi.mock(
  import("@/composables/ergebnismeldung/MBW/bedenklicheStimmzettelService.ts"),
  () => ({
    useBedenklicheStimmzettelService: () => ({
      getBedenklicheStimmzettel: mockDefinitions.getBedenklicheStimmzettel,
      saveBedenklicheStimmzettel: vi.fn(),
    }),
  })
);

vi.mock("@/stores/userStore.ts", () => ({
  useUserStore: () => ({
    getWahlbezirkIdFromWahlMetaDataByWahlId: vi.fn(() => "mockWahlbezirkId"),
  }),
}));

describe("MBWStapelDView", () => {
  let wrapper: VueWrapper;

  mockAndStubResizeObserver();

  const router = createRouter({
    history: createWebHistory(),
    routes: [
      {
        path: "/",
        name: ROUTE_NOTFOUND,
        component: HomeView,
        meta: {},
      },
      {
        path: "/wahlvorstand",
        name: "wahlvorstand",
        component: WahlvorstandAnwesenheitView,
      },
      {
        path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelD",
        component: MBWStapelDView,
      },
    ],
  });

  beforeEach(() => {
    const keepAliveWrapperComponent = createKeepAliveComponent(
      "wahlID",
      "wahlbezirkID",
      MBWStapelDView
    );
    wrapper = mount(keepAliveWrapperComponent, {
      global: { plugins: [pinia, vuetify, router] },
    });
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_callServices_when_componentIsMounted", () => {
      expect(mockDefinitions.getErgebnisse).toHaveBeenCalled();
      expect(
        mockDefinitions.getBedenklicheStimmzettel.mock.calls
      ).toStrictEqual([["wahlID", "mockWahlbezirkId", false]]);
    });

    it("should_saveErgebnis_when_saveEventIsEmmited", () => {
      mockDefinitions.getNextRoute.mockResolvedValue("");

      const baseCardSnippedErgebnis = wrapper.findComponent(
        BaseCardUngueltigeStimmzettelErfassen
      );
      baseCardSnippedErgebnis.vm.$emit("save");

      expect(mockDefinitions.postErgebnisse).toHaveBeenCalled();
    });
  });
});
