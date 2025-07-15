import type { TestingPinia } from "@pinia/testing";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { VNumberInput } from "vuetify/components";

import BaseWahlumgebungWahlurnenDiv from "@/components/wahlvorbereitung/BaseWahlumgebungWahlurnenDiv.vue";
import vuetify from "@/plugins/vuetify.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlen: vi.fn(),
}));

vi.mock("@/composables/wahl/wahlservice", () => ({
  useWahlService: () => ({
    getWahlen: mockDefinitions.getWahlen,
  }),
}));

describe("BaseWahlumgebungWahlurnenDiv.vue", () => {
  let testPinia: TestingPinia;

  const twoWahlenBriefwahlVorbereitung = {
    wahlbezirkID: "wahlbezirkID1",
    urneVersiegelt: false,
    urnenAnzahl: [
      { wahlID: "wahlID1", anzahl: 0 },
      { wahlID: "wahlID2", anzahl: 0 },
    ],
  };

  const noWahlenBriefwahlVorbereitung = {
    wahlbezirkID: "wahlbezirkID1",
    urneVersiegelt: false,
    urnenAnzahl: [],
  };

  beforeEach(() => {
    testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWithZeroInputFields_when_noWahlenAreGiven", async (context) => {
      const wrapper = mount(BaseWahlumgebungWahlurnenDiv, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlVorbereitung: noWahlenBriefwahlVorbereitung,
        },
      });

      expect(wrapper.findAllComponents(VNumberInput).length).toBe(0);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithTwoInputFields_when_TwoWahlenAreGiven", async (context) => {
      const wrapper = mount(BaseWahlumgebungWahlurnenDiv, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlVorbereitung: twoWahlenBriefwahlVorbereitung,
        },
      });

      expect(wrapper.findAllComponents(VNumberInput).length).toBe(2);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
