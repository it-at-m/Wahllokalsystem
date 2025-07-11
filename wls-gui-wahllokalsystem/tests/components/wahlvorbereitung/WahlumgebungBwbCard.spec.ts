import type { TestingPinia } from "@pinia/testing";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { VNumberInput } from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import WahlumgebungBwbCard from "@/components/wahlvorbereitung/WahlumgebungBwbCard.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlen: vi.fn(),
}));

vi.mock("@/composables/wahl/wahlservice", () => ({
  useWahlService: () => ({
    getWahlen: mockDefinitions.getWahlen,
  }),
}));

describe("WahlumgebungBwbCard.vue", () => {
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
    },
  ];

  const validBriefwahlVorbereitung = {
    wahlbezirkID: "wahlbezirkID1",
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
    it("should_renderWithZeroInputFieldsAndDisabledSaveButton_when_NoWahlenAreGiven", async (context) => {
      wahlenStore = useWahlenStore(testPinia);
      wahlenStore.wahlen = [];

      const wrapper = mount(WahlumgebungBwbCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
      });

      expect(wrapper.findAllComponents(VNumberInput).length).toBe(0);
      const saveButton = wrapper.findComponent(BaseButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithTwoInputFieldsAndDisabledSaveButton_when_TwoWahlenAreGiven", async (context) => {
      wahlbezirkStore = useWahlbezirkStore(testPinia);
      wahlenStore = useWahlenStore(testPinia);
      wahlenStore.wahlen = validWahlen;
      wahlbezirkStore.briefwahlVorbereitung = validBriefwahlVorbereitung;

      const wrapper = mount(WahlumgebungBwbCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
      });

      expect(wrapper.findAllComponents(VNumberInput).length).toBe(2);
      const saveButton = wrapper.findComponent(BaseButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
