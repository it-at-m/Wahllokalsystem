import { COMPONENT_EVENT_TESTS } from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { createRouter, createWebHistory } from "vue-router";

import BaseCardSnippedErgebnis from "@/components/ergebnisermittlung/BaseCardSnippedErgebnis.vue";
import { EXAMPLE_ROUTES_NOTFOUND } from "@/constants.ts";
import pinia from "@/plugins/pinia.ts";
import vuetify from "@/plugins/vuetify";
import MBWStapelDView from "@/views/auszaehlung/mbw/MBWStapelDView.vue";
import HomeView from "@/views/HomeView.vue";

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisse: vi.fn(),
  postErgebnisse: vi.fn(),
}));

vi.mock("@/composables/ergebnismeldung/common/ergebnisService.ts", () => ({
  useErgebnisService: () => ({
    getErgebnisse: mockDefinitions.getErgebnisse,
    postErgebnisse: mockDefinitions.postErgebnisse,
  }),
}));

vi.mock("@/stores/userStore.ts", () => ({
  useUserStore: () => ({
    getWahlbezirkIdFromWahlMetaDataByWahlId: vi.fn(() => "mockWahlbezirkId"),
  }),
}));

describe("MBWStapelDView", () => {
  let wrapper: VueWrapper;

  const ResizeObserverMock = vi.fn(() => ({
    observe: vi.fn(),
    unobserve: vi.fn(),
    disconnect: vi.fn(),
  }));
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);

  const router = createRouter({
    history: createWebHistory(),
    routes: [
      {
        path: "/",
        name: EXAMPLE_ROUTES_NOTFOUND,
        component: HomeView,
        meta: {},
      },
      {
        path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelD",
        component: MBWStapelDView,
      },
    ],
  });

  beforeEach(() => {
    wrapper = mount(MBWStapelDView, {
      global: { plugins: [pinia, vuetify, router] },
    });
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_callGetErgebnisse_when_componentIsMounted", () => {
      expect(mockDefinitions.getErgebnisse).toHaveBeenCalled();
    });

    it("should_saveErgebnis_when_saveEventIsEmmited", () => {
      const baseCardSnippedErgebnis = wrapper.findComponent(
        BaseCardSnippedErgebnis
      );
      baseCardSnippedErgebnis.vm.$emit("save");

      expect(mockDefinitions.postErgebnisse).toHaveBeenCalled();
    });
  });
});
