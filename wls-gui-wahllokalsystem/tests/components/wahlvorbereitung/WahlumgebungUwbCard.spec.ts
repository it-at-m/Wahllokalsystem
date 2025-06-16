import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { flushPromises, mount, VueWrapper } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
import { VNumberInput } from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import WahlumgebungUwbCard from "@/components/wahlvorbereitung/WahlumgebungUwbCard.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

describe("WahlumgebungUwbCard.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof WahlumgebungUwbCard>>;
  let testPinia;

  const validWahlen = [
    {
      wahlID: "wahlID1",
      name: "Bundestagswahl",
      reihenfolge: 1,
      waehlerverzeichnisnummer: 33,
      wahltag: "25.05.2026",
      wahlart: WahlWahlartEnum.Btw,
      farbe: undefined,
      nummer: undefined,
    },
    {
      wahlID: "wahlID2",
      name: "Oberbürgermeisterwahl",
      reihenfolge: 2,
      waehlerverzeichnisnummer: 34,
      wahltag: "25.05.2026",
      wahlart: WahlWahlartEnum.Obw,
      farbe: undefined,
      nummer: undefined,
    },
  ];

  const validUrnenwahlVorbereitung = {
    wahlbezirkID: "wahlbezirkID1",
    anzahlWahltische: 0,
    anzahlNebenraeume: 0,
    anzahlWahlkabinen: 0,
    urnenAnzahl: [
      { wahlID: "wahlID1", anzahl: 0, urneVersiegelt: false },
      { wahlID: "wahlID2", anzahl: 0, urneVersiegelt: false },
    ],
  };

  beforeEach(() => {
    testPinia = createTestingPinia({
      createSpy: vi.fn,
    });
    wrapper = mount(WahlumgebungUwbCard, {
      global: {
        plugins: [testPinia, vuetify],
      },
    });
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderWithThreeInputFieldsAndDisabledSaveButton_when_NoWahlenAreGiven", async (context) => {
      await flushPromises();
      expect(wrapper.findAllComponents(VNumberInput).length).toBe(3);
      const saveButton = wrapper.findComponent(BaseButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);
      expect(wrapper.html()).toMatchFileSnapshot(getSnapshotFilename(context));
    });

    it("should_renderWithFiveInputFieldsAndDisabledSaveButton_when_TwoWahlenAreGiven", async (context) => {
      const wahlenStore = useWahlenStore(testPinia);
      wahlenStore.wahlen = validWahlen;
      const wahlbezirkStore = useWahlbezirkStore(testPinia);
      await nextTick();
      wrapper = mount(WahlumgebungUwbCard, {
        global: {
          plugins: [
            createTestingPinia({
              createSpy: vi.fn,
              stubActions: false,
            }),
            vuetify,
          ],
        },
      });
      wahlbezirkStore.urnenwahlVorbereitung = validUrnenwahlVorbereitung;
      await flushPromises();

      expect(wrapper.findAllComponents(VNumberInput).length).toBe(5);

      const saveButton = wrapper.findComponent(BaseButtonSave);
      expect(saveButton.element.hasAttribute("disabled")).toStrictEqual(true);

      expect(wrapper.html()).toMatchFileSnapshot(getSnapshotFilename(context));
    });
  });
});
