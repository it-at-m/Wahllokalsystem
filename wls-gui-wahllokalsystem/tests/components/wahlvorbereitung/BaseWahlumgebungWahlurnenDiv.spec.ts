import type { TestingPinia } from "@pinia/testing";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
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

  const twoWahlenWahlVorbereitung = {
    wahlbezirkID: "wahlbezirkID1",
    urneVersiegelt: false,
    urnenAnzahl: [
      { wahlID: "wahlID1", anzahl: 0 },
      { wahlID: "wahlID2", anzahl: 0 },
    ],
  };

  const noWahlenWahlVorbereitung = {
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
          wahlVorbereitung: noWahlenWahlVorbereitung,
        },
      });

      expect(wrapper.findAllComponents(VNumberInput).length).toBe(0);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithTwoInputFields_when_twoWahlenAreGiven", async (context) => {
      const wrapper = mount(BaseWahlumgebungWahlurnenDiv, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlVorbereitung: twoWahlenWahlVorbereitung,
        },
      });

      expect(wrapper.findAllComponents(VNumberInput).length).toBe(2);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_updateAnzahlForIndex_when_anzahlIsEnteredForIndex", async () => {
      const wrapper = mount(BaseWahlumgebungWahlurnenDiv, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlVorbereitung: twoWahlenWahlVorbereitung,
        },
      });

      const anzahlNumberInput = wrapper.findComponent(
        '[data-test="textFieldUrnenAnzahl_1"]'
      );
      const enteredAnzahl = 3;

      await anzahlNumberInput.setValue(enteredAnzahl);

      expect(wrapper.vm.wahlVorbereitung.urnenAnzahl[1].anzahl).toStrictEqual(
        enteredAnzahl
      );
    });
  });
});
