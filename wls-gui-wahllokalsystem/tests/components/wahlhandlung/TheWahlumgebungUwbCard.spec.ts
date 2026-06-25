import type { TestingPinia } from "@pinia/testing";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { VNumberInput } from "vuetify/components";

import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import TheWahlumgebungUwbCard from "@/components/wahlhandlung/TheWahlumgebungUwbCard.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlen: vi.fn(),
}));

vi.mock(import("@/composables/wahl/wahlService.ts"), () => ({
  useWahlService: () => ({
    getWahlen: mockDefinitions.getWahlen,
  }),
}));

describe("TheWahlumgebungUwbCard.vue", () => {
  let wahlenStore: ReturnType<typeof useWahlenStore>;
  let wahlbezirkStore: ReturnType<typeof useWahlbezirkStore>;
  let testPinia: TestingPinia;

  const validWahlen = [
    {
      wahlID: "wahlID1",
      name: "Bundestagswahl",
      reihenfolge: 1,
      waehlerverzeichnisNummer: 33,
      wahltag: "25.05.2026",
      wahlart: WahlWahlartEnum.Btw,
      farbe: undefined,
      nummer: undefined,
      beanstandeteWahlbriefe: [],
      stimmzettelumschlaege: { anzahlWaehler: null },
    },
    {
      wahlID: "wahlID2",
      name: "Oberbürgermeisterwahl",
      reihenfolge: 2,
      waehlerverzeichnisNummer: 34,
      wahltag: "25.05.2026",
      wahlart: WahlWahlartEnum.Obw,
      farbe: undefined,
      nummer: undefined,
      beanstandeteWahlbriefe: [],
      stimmzettelumschlaege: { anzahlWaehler: null },
    },
  ];

  const validUrnenwahlVorbereitung = {
    wahlbezirkID: "wahlbezirkID1",
    anzahlWahltische: 0,
    anzahlNebenraeume: 0,
    anzahlWahlkabinen: 0,
    urneVersiegelt: false,
    urnenAnzahl: [
      { wahlID: "wahlID1", anzahl: 0 },
      { wahlID: "wahlID2", anzahl: 0 },
    ],
  };

  beforeEach(() => {
    testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWithThreeInputFieldsAndDisabledSaveButton_when_noWahlenAreGiven", async (context) => {
      wahlenStore = useWahlenStore(testPinia);
      wahlenStore.wahlenState.wahlen = [];

      const wrapper = mount(TheWahlumgebungUwbCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
      });

      expect(wrapper.findAllComponents(VNumberInput).length).toBe(3);
      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithFiveInputFieldsAndDisabledSaveButton_when_twoWahlenAreGiven", async (context) => {
      wahlbezirkStore = useWahlbezirkStore(testPinia);
      wahlenStore = useWahlenStore(testPinia);
      wahlenStore.wahlenState.wahlen = validWahlen;
      wahlbezirkStore.urnenwahlVorbereitungState.urnenwahlVorbereitung =
        validUrnenwahlVorbereitung;

      const wrapper = mount(TheWahlumgebungUwbCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
      });

      expect(wrapper.findAllComponents(VNumberInput).length).toBe(5);
      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
