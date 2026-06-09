import {
  COMPONENT_EVENT_TESTS,
  mockAndStubResizeObserver,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { createRouter, createWebHistory } from "vue-router";

import BaseCardSnippedErgebnis from "@/components/ergebnismeldung/common/BaseCardSnippedErgebnis.vue";
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
}));

vi.mock("@/composables/navigation/navigationUtils.ts", () => ({
  useNavigationUtils: () => ({
    getNextRoute: mockDefinitions.getNextRoute,
  }),
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
      mockDefinitions.getNextRoute.mockResolvedValue("");

      const baseCardSnippedErgebnis = wrapper.findComponent(
        BaseCardSnippedErgebnis
      );
      baseCardSnippedErgebnis.vm.$emit("save");

      expect(mockDefinitions.postErgebnisse).toHaveBeenCalled();
    });
  });
});
