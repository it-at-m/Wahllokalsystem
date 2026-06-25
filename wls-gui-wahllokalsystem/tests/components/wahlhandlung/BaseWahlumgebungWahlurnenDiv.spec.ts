import type { TestingPinia } from "@pinia/testing";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { flushPromises, mount } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { VNumberInput } from "vuetify/components";

import BaseWahlumgebungWahlurnenDiv from "@/components/wahlhandlung/BaseWahlumgebungWahlurnenDiv.vue";
import vuetify from "@/plugins/vuetify.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlen: vi.fn(),
}));

vi.mock(import("@/composables/wahl/wahlService.ts"), () => ({
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

    it("should_renderNoErrorMessage_when_anzahlWahlurnenIsValid", async (context) => {
      const twoWahlenWahlVorbereitungAnzahlNull = {
        wahlbezirkID: "wahlbezirkID1",
        urneVersiegelt: false,
        urnenAnzahl: [
          { wahlID: "wahlID1", anzahl: null },
          { wahlID: "wahlID2", anzahl: null },
        ],
      };

      const wrapper = mount(BaseWahlumgebungWahlurnenDiv, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlVorbereitung: twoWahlenWahlVorbereitungAnzahlNull,
        },
      });

      const inputs = wrapper.findAllComponents(VNumberInput);
      expect(inputs.length).toBe(2);

      // Teste die Validierung mit ungültigem Wert
      await inputs[0]?.setValue(1);
      await inputs[1]?.setValue(99);
      await flushPromises();
      expect(wrapper.vm.wahlVorbereitung.urnenAnzahl[0]?.anzahl).toBe(1);
      expect(wrapper.vm.wahlVorbereitung.urnenAnzahl[1]?.anzahl).toBe(99);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      // Überprüfen, dass keine Fehlermeldung angezeigt wird
      expect(wrapper.findAll(".v-messages__message").length).toBe(0);
    });

    it("should_renderErrorMessage_when_ruleMinIsViolated", async (context) => {
      const errorMessage = "Eingabe darf nicht kleiner als 1 sein.";
      const twoWahlenWahlVorbereitungAnzahlNull = {
        wahlbezirkID: "wahlbezirkID1",
        urneVersiegelt: false,
        urnenAnzahl: [
          { wahlID: "wahlID1", anzahl: null },
          { wahlID: "wahlID2", anzahl: null },
        ],
      };

      const wrapper = mount(BaseWahlumgebungWahlurnenDiv, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlVorbereitung: twoWahlenWahlVorbereitungAnzahlNull,
        },
      });

      const inputs = wrapper.findAllComponents(VNumberInput);
      expect(inputs.length).toBe(2);

      // Teste die Validierung mit ungültigem Wert
      await inputs[0]?.setValue(0);
      await flushPromises();
      expect(wrapper.vm.wahlVorbereitung.urnenAnzahl[0]?.anzahl).toBe(0);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain(errorMessage);
    });

    it("should_renderErrorMessage_when_ruleMaxIsViolated", async (context) => {
      const errorMessage = "Eingabe darf nicht größer als 99 sein.";
      const twoWahlenWahlVorbereitungAnzahlNull = {
        wahlbezirkID: "wahlbezirkID1",
        urneVersiegelt: false,
        urnenAnzahl: [
          { wahlID: "wahlID1", anzahl: null },
          { wahlID: "wahlID2", anzahl: null },
        ],
      };

      const wrapper = mount(BaseWahlumgebungWahlurnenDiv, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlVorbereitung: twoWahlenWahlVorbereitungAnzahlNull,
        },
      });

      const inputs = wrapper.findAllComponents(VNumberInput);
      expect(inputs.length).toBe(2);

      // Teste die Validierung mit ungültigem Wert
      await inputs[0]?.setValue(100);
      await flushPromises();
      expect(wrapper.vm.wahlVorbereitung.urnenAnzahl[0]?.anzahl).toBe(100);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain(errorMessage);
    });

    //runs unstable. Sometimes "style" attribute is set with no property set
    it.skip("should_renderErrorMessage_when_ruleRequiredIsViolated", async (context) => {
      const twoWahlenWahlVorbereitungAnzahlNull = {
        wahlbezirkID: "wahlbezirkID1",
        urneVersiegelt: false,
        urnenAnzahl: [
          { wahlID: "wahlID1", anzahl: null },
          { wahlID: "wahlID2", anzahl: null },
        ],
      };

      const wrapper = mount(BaseWahlumgebungWahlurnenDiv, {
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          wahlVorbereitung: twoWahlenWahlVorbereitungAnzahlNull,
        },
      });

      const inputs = wrapper.findAllComponents(VNumberInput);
      expect(inputs.length).toBe(2);
      await flushPromises();

      // Teste die Validierung mit gültigem Wert (z.B. 77)
      await inputs[0]?.setValue(77);
      await flushPromises();
      expect(wrapper.vm.wahlVorbereitung.urnenAnzahl[0]?.anzahl).toBe(77);

      // zurück auf null
      await inputs[0]?.setValue(null);
      await inputs[0]?.vm.validate();
      await flushPromises();
      expect(wrapper.vm.wahlVorbereitung.urnenAnzahl[0]?.anzahl).toBe(null);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      // Überprüfen, ob eine Fehlermeldung angezeigt wird
      expect(wrapper.findAll(".v-messages__message").length).toBeGreaterThan(0);
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

      expect(wrapper.vm.wahlVorbereitung.urnenAnzahl[1]?.anzahl).toStrictEqual(
        enteredAnzahl
      );
    });
  });
});
