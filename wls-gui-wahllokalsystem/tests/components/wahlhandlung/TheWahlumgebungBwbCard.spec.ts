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
import TheWahlumgebungBwbCard from "@/components/wahlhandlung/TheWahlumgebungBwbCard.vue";
import router from "@/plugins/router.ts";
import vuetify from "@/plugins/vuetify.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlen: vi.fn(),
  routerPush: vi.fn(),
}));

vi.mock("@/composables/wahl/wahlservice", () => ({
  useWahlService: () => ({
    getWahlen: mockDefinitions.getWahlen,
  }),
}));

router.push = mockDefinitions.routerPush;

describe("TheWahlumgebungBwbCard.vue", () => {
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
    it("should_renderWithZeroInputFieldsAndDisabledSaveButton_when_noWahlenAreGiven", async (context) => {
      wahlenStore = useWahlenStore(testPinia);
      wahlenStore.wahlenState.wahlen = [];

      const wrapper = mount(TheWahlumgebungBwbCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
      });

      expect(wrapper.findAllComponents(VNumberInput).length).toBe(0);
      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithTwoInputFieldsAndDisabledSaveButton_when_twoWahlenAreGivenAndInputsAreEmpty", async (context) => {
      wahlbezirkStore = useWahlbezirkStore(testPinia);
      wahlenStore = useWahlenStore(testPinia);
      wahlenStore.wahlenState.wahlen = validWahlen;
      wahlbezirkStore.briefwahlVorbereitungState.briefwahlVorbereitung =
        validBriefwahlVorbereitung;

      const wrapper = mount(TheWahlumgebungBwbCard, {
        global: {
          plugins: [testPinia, vuetify],
        },
      });

      expect(wrapper.findAllComponents(VNumberInput).length).toBe(2);
      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderWithTwoInputFieldsAndSaveButtonEnabled_when_twoWahlenAreGivenAndAllInputsAreValid", async (context) => {
      const wrapper = mount(TheWahlumgebungBwbCard, {
        attachTo: document.body,
        global: {
          plugins: [testPinia, vuetify],
        },
        props: {
          briefwahlVorbereitungState: {
            briefwahlVorbereitung: validBriefwahlVorbereitung,
          },
        },
      });

      const checkbox = wrapper
        .find('[data-test="checkboxAlleVersiegelt"]')
        .get("input");
      expect(checkbox).toBeDefined();
      expect(
        wrapper.vm.briefwahlVorbereitungState.briefwahlVorbereitung
          .urneVersiegelt
      ).toBe(false);

      const saveButton = wrapper.findComponent(BaseWlsButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);

      await checkbox.setValue(true);
      expect(
        wrapper.vm.briefwahlVorbereitungState.briefwahlVorbereitung
          .urneVersiegelt
      ).toBe(true);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(false);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
