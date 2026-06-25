import type { TestingPinia } from "@pinia/testing";
import type { VueWrapper } from "@vue/test-utils";
import type { VAutocomplete } from "vuetify/components";

import { createTestingPinia } from "@pinia/testing";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
  mockAndStubResizeObserver,
  stubVisualViewport,
} from "@tests/utils/testutils.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { enableAutoUnmount, mount } from "@vue/test-utils";
import { createPinia } from "pinia";
import {
  afterEach,
  beforeAll,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { nextTick } from "vue";
import { VBtn } from "vuetify/components";

import TheBeanstandeteWahlbriefeTable from "@/components/wahlhandlung/beanstandeteWahlbriefe/TheBeanstandeteWahlbriefeTable.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    rowIndexToDelete: number;
    wahlscheinGruende: string[];
  }
}

describe("TheBeanstandeteWahlbriefeTable", () => {
  let wrapper: VueWrapper;
  let pinia: TestingPinia;
  const { prepareWahl } = useWahlTestDataFactory();

  mockAndStubResizeObserver();
  stubVisualViewport();

  beforeAll(() => {
    createPinia();
  });

  beforeEach(async () => {
    pinia = createTestingPinia({ createSpy: vi.fn, stubActions: false });
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_onlyShowWahlscheinHeader_when_noWahlenGiven", async (context) => {
      wrapper = mount(TheBeanstandeteWahlbriefeTable, {
        global: {
          plugins: [pinia, vuetify],
        },
      });

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain("Wahlschein");
    });

    it("should_onlyShowHeaders_when_noBeanstandeteWahlbriefeGiven", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .name("Wahl1")
          .wahlID("id1")
          .beanstandeteWahlbriefe([])
          .build(),
        prepareWahl()
          .name("Wahl2")
          .wahlID("id2")
          .beanstandeteWahlbriefe([])
          .build(),
      ];

      wrapper = mount(TheBeanstandeteWahlbriefeTable, {
        global: {
          plugins: [pinia, vuetify],
        },
      });

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain("Wahlschein");
      expect(wrapper.html()).toContain("Wahl1");
      expect(wrapper.html()).toContain("Wahl2");
    });

    it("should_showOneRow_when_oneZurueckweisungsgrundGiven", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .name("Wahl1")
          .wahlID("id1")
          .beanstandeteWahlbriefe(["GEGENSTAND_IM_UMSCHLAG"])
          .build(),
        prepareWahl()
          .name("Wahl2")
          .wahlID("id2")
          .beanstandeteWahlbriefe(["GEGENSTAND_IM_UMSCHLAG"])
          .build(),
      ];

      wrapper = mount(TheBeanstandeteWahlbriefeTable, {
        global: {
          plugins: [pinia, vuetify],
        },
      });

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain("Gegenstand im Stimmzettelumschlag");
    });

    it("should_showMultipleRows_when_multipleZurueckweisungsgruendeGiven", async (context) => {
      const wahlenStore = useWahlenStore();
      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          .name("Wahl1")
          .wahlID("id1")
          .beanstandeteWahlbriefe([
            "GEGENSTAND_IM_UMSCHLAG",
            "ZUGELASSEN",
            "UNTERSCHRIFT_FEHLT",
          ])
          .build(),
        prepareWahl()
          .name("Wahl2")
          .wahlID("id2")
          .beanstandeteWahlbriefe([
            "GEGENSTAND_IM_UMSCHLAG",
            "ZUGELASSEN",
            "NICHT_WAHLBERECHTIGT",
          ])
          .build(),
      ];

      wrapper = mount(TheBeanstandeteWahlbriefeTable, {
        global: {
          plugins: [pinia, vuetify],
        },
      });

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain("Gegenstand im Stimmzettelumschlag");
      expect(wrapper.html()).toContain("Zugelassen");
      expect(wrapper.html()).toContain("Unterschrift auf Wahlschein fehlt");
      expect(wrapper.html()).toContain("Für diese Wahl nicht wahlberechtigt");
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    describe("onMounted", () => {
      it.each([
        ZurueckweisungsgrundEnum.Zugelassen,
        ZurueckweisungsgrundEnum.UmschlagFehlt,
        ZurueckweisungsgrundEnum.WahlbriefUndUmschlagOffen,
        ZurueckweisungsgrundEnum.ScheineUngleichUmschlaege,
        ZurueckweisungsgrundEnum.UmschlagNichtAmtlich,
        ZurueckweisungsgrundEnum.UmschlagGefaehrdetWahlgeheimnis,
        ZurueckweisungsgrundEnum.GegenstandImUmschlag,
        ZurueckweisungsgrundEnum.NichtWahlberechtigt,
      ])(
        "should_setWahlscheinColumnModelValueToZugelassen_when_beanstandeteWahlbriefeLoadedFromWahlenAndValueIsStimmzettelGrund'%s'",
        async (input) => {
          const wahlenStore = useWahlenStore();
          wahlenStore.wahlenState.wahlen = [
            prepareWahl()
              .name("Wahl1")
              .wahlID("id1")
              .beanstandeteWahlbriefe([input])
              .build(),
          ];

          wrapper = mount(TheBeanstandeteWahlbriefeTable, {
            global: {
              plugins: [pinia, vuetify],
            },
          });

          await nextTick();

          const wahlscheineInput0 = wrapper.findComponent<typeof VAutocomplete>(
            '[data-test="wahlscheingruende-input-0"]'
          );

          expect(wahlscheineInput0.props("modelValue")).toBe("Zugelassen");
        }
      );

      it.each([
        {
          grund: ZurueckweisungsgrundEnum.ScheinUngueltig,
          expected: "Wahlschein ungültig laut Liste",
        },
        {
          grund: ZurueckweisungsgrundEnum.KeinOriginalSchein,
          expected: "Kein Original-Wahlschein",
        },
        {
          grund: ZurueckweisungsgrundEnum.UnterschriftFehlt,
          expected: "Unterschrift auf Wahlschein fehlt",
        },
      ])(
        "should_setWahlscheinColumnModelValueToWahlscheingrund_when_beanstandeteWahlbriefeLoadedFromWahlenAndValueIsWahlscheinGrund'$grund'",
        async ({ grund, expected }) => {
          const wahlenStore = useWahlenStore();
          wahlenStore.wahlenState.wahlen = [
            prepareWahl()
              .name("Wahl1")
              .wahlID("id1")
              .beanstandeteWahlbriefe([grund])
              .build(),
          ];

          wrapper = mount(TheBeanstandeteWahlbriefeTable, {
            global: {
              plugins: [pinia, vuetify],
            },
          });

          await nextTick();

          const wahlscheineInput0 = wrapper.findComponent<typeof VAutocomplete>(
            '[data-test="wahlscheingruende-input-0"]'
          );

          expect(wahlscheineInput0.props("modelValue")).toBe(expected);
        }
      );
    });

    describe("onZulassungsgrundChanged", () => {
      describe("onZulassungsgrundWahlscheinChanged", () => {
        it("should_disableWahlColumnInputsAndSetModelValue_when_wahlscheinColumnInputIsValidWahlscheinGrund", async () => {
          const wahlenStore = useWahlenStore();
          wahlenStore.wahlenState.wahlen = [
            prepareWahl()
              .name("Wahl1")
              .wahlID("id1")
              .beanstandeteWahlbriefe(["KEIN_ORIGINAL_SCHEIN"])
              .build(),
            prepareWahl()
              .name("Wahl2")
              .wahlID("id2")
              .beanstandeteWahlbriefe(["KEIN_ORIGINAL_SCHEIN"])
              .build(),
          ];

          wrapper = mount(TheBeanstandeteWahlbriefeTable, {
            global: {
              plugins: [pinia, vuetify],
            },
          });

          await nextTick();

          const expectedZurueckweisungsGrund = "Kein Original-Wahlschein";

          const wahlscheineInputRow0 = wrapper.findComponent<
            typeof VAutocomplete
          >('[data-test="wahlscheingruende-input-0"]');

          const stimmzettelInputWahl1Row0 = wrapper.findComponent<
            typeof VAutocomplete
          >(`[data-test="stimmzettelgruende-input-id1-0"]`);
          const stimmzettelInputWahl2Row0 = wrapper.findComponent<
            typeof VAutocomplete
          >(`[data-test="stimmzettelgruende-input-id2-0"]`);

          expect(wahlscheineInputRow0.props("modelValue")).toBe(
            expectedZurueckweisungsGrund
          );
          expect(stimmzettelInputWahl1Row0.props("modelValue")).toBe(
            expectedZurueckweisungsGrund
          );
          expect(stimmzettelInputWahl1Row0.props("disabled")).toBe(true);
          expect(stimmzettelInputWahl2Row0.props("modelValue")).toBe(
            expectedZurueckweisungsGrund
          );
          expect(stimmzettelInputWahl2Row0.props("disabled")).toBe(true);
        });

        it("should_enableWahlColumnInputsAndSetValueToNull_when_wahlscheinColumnInputIsSetToZugelassen", async () => {
          const wahlenStore = useWahlenStore();
          wahlenStore.wahlenState.wahlen = [
            prepareWahl()
              .name("Wahl1")
              .wahlID("id1")
              .beanstandeteWahlbriefe(["KEIN_ORIGINAL_SCHEIN"])
              .build(),
            prepareWahl()
              .name("Wahl2")
              .wahlID("id2")
              .beanstandeteWahlbriefe(["KEIN_ORIGINAL_SCHEIN"])
              .build(),
          ];

          wrapper = mount(TheBeanstandeteWahlbriefeTable, {
            global: {
              plugins: [pinia, vuetify],
            },
          });

          await nextTick();

          const wahlscheineInputRow0 = wrapper.findComponent<
            typeof VAutocomplete
          >('[data-test="wahlscheingruende-input-0"]');

          wahlscheineInputRow0.vm.$emit("update:modelValue", "Zugelassen");
          await nextTick();

          const stimmzettelInputWahl1Row0 = wrapper.findComponent<
            typeof VAutocomplete
          >(`[data-test="stimmzettelgruende-input-id1-0"]`);
          const stimmzettelInputWahl2Row0 = wrapper.findComponent<
            typeof VAutocomplete
          >(`[data-test="stimmzettelgruende-input-id2-0"]`);

          expect(wahlscheineInputRow0.props("modelValue")).toBe("Zugelassen");
          expect(stimmzettelInputWahl1Row0.props("modelValue")).toBeFalsy();
          expect(stimmzettelInputWahl1Row0.props("disabled")).toBe(false);
          expect(stimmzettelInputWahl2Row0.props("modelValue")).toBeFalsy();
          expect(stimmzettelInputWahl2Row0.props("disabled")).toBe(false);
        });
      });

      describe("onZulassungsgrundStimmzettelChanged", () => {
        it("should_setWahlColumnValues_when_anyWahlColumnInputExceptNichtWahlberechtigtIsSet", async () => {
          const wahlenStore = useWahlenStore();
          wahlenStore.wahlenState.wahlen = [
            prepareWahl()
              .name("Wahl1")
              .wahlID("id1")
              .beanstandeteWahlbriefe(["UMSCHLAG_NICHT_AMTLICH"])
              .build(),
            prepareWahl()
              .name("Wahl2")
              .wahlID("id2")
              .beanstandeteWahlbriefe(["UMSCHLAG_NICHT_AMTLICH"])
              .build(),
          ];

          wrapper = mount(TheBeanstandeteWahlbriefeTable, {
            global: {
              plugins: [pinia, vuetify],
            },
          });

          await nextTick();

          const oldZurueckweisungsgrund = "Nicht-amtlicher Stimmzettelumschlag";
          const newZurueckweisungsgrund = "Gegenstand im Stimmzettelumschlag";

          const stimmzettelInputWahl1Row0 = wrapper.findComponent<
            typeof VAutocomplete
          >(`[data-test="stimmzettelgruende-input-id1-0"]`);
          const stimmzettelInputWahl2Row0 = wrapper.findComponent<
            typeof VAutocomplete
          >(`[data-test="stimmzettelgruende-input-id2-0"]`);

          expect(stimmzettelInputWahl1Row0.props("modelValue")).toBe(
            oldZurueckweisungsgrund
          );
          expect(stimmzettelInputWahl2Row0.props("modelValue")).toBe(
            oldZurueckweisungsgrund
          );

          stimmzettelInputWahl1Row0.vm.$emit(
            "update:modelValue",
            newZurueckweisungsgrund
          );
          await nextTick();

          expect(stimmzettelInputWahl1Row0.props("modelValue")).toBe(
            newZurueckweisungsgrund
          );
          expect(stimmzettelInputWahl2Row0.props("modelValue")).toBe(
            newZurueckweisungsgrund
          );
        });

        it("should_notSetAnyValues_when_anyWahlColumnInputIsSetToNichtWahlberechtigt", async () => {
          const wahlenStore = useWahlenStore();
          wahlenStore.wahlenState.wahlen = [
            prepareWahl()
              .name("Wahl1")
              .wahlID("id1")
              .beanstandeteWahlbriefe(["UMSCHLAG_NICHT_AMTLICH"])
              .build(),
            prepareWahl()
              .name("Wahl2")
              .wahlID("id2")
              .beanstandeteWahlbriefe(["UMSCHLAG_NICHT_AMTLICH"])
              .build(),
          ];

          wrapper = mount(TheBeanstandeteWahlbriefeTable, {
            global: {
              plugins: [pinia, vuetify],
            },
          });

          await nextTick();

          const oldZurueckweisungsgrund = "Nicht-amtlicher Stimmzettelumschlag";
          const newZurueckweisungsgrund = "Für diese Wahl nicht wahlberechtigt";

          const stimmzettelInputWahl1Row0 = wrapper.findComponent<
            typeof VAutocomplete
          >(`[data-test="stimmzettelgruende-input-id1-0"]`);
          const stimmzettelInputWahl2Row0 = wrapper.findComponent<
            typeof VAutocomplete
          >(`[data-test="stimmzettelgruende-input-id2-0"]`);

          expect(stimmzettelInputWahl1Row0.props("modelValue")).toBe(
            oldZurueckweisungsgrund
          );
          expect(stimmzettelInputWahl2Row0.props("modelValue")).toBe(
            oldZurueckweisungsgrund
          );

          stimmzettelInputWahl1Row0.vm.$emit(
            "update:modelValue",
            newZurueckweisungsgrund
          );
          await nextTick();

          expect(stimmzettelInputWahl1Row0.props("modelValue")).toBe(
            newZurueckweisungsgrund
          );
          expect(stimmzettelInputWahl2Row0.props("modelValue")).toBe(
            oldZurueckweisungsgrund
          );
        });
      });
    });

    describe("onDeleteBeanstandeteWahlbriefeRowClicked", () => {
      it("should_deleteStoreValuesWithDialog_when_deleteRowClicked", async () => {
        const wahlenStore = useWahlenStore();
        wahlenStore.wahlenState.wahlen = [
          prepareWahl()
            .name("Wahl1")
            .wahlID("id1")
            .beanstandeteWahlbriefe(["GEGENSTAND_IM_UMSCHLAG"])
            .build(),
        ];

        const deleteBeanstandeterWahlbriefEntrySpy = vi.spyOn(
          useWahlenStore().beanstandeteWahlbriefeActions,
          "deleteBeanstandeterWahlbriefEntry"
        );

        wrapper = mount(TheBeanstandeteWahlbriefeTable, {
          global: {
            plugins: [pinia, vuetify],
          },
        });

        // delete first row
        const deleteButton = wrapper.findComponent<typeof VBtn>(
          `[data-test="delete-btn-0"]`
        );
        await deleteButton.trigger("click");

        expect(wrapper.vm.rowIndexToDelete).equals(0);

        const confirmButton = wrapper.findComponent(
          '[data-test="basedialog-btn-confirm"]'
        );
        await confirmButton.trigger("click");

        expect(deleteBeanstandeterWahlbriefEntrySpy).toHaveBeenCalled();
      });

      it("should_deleteEmptyRow_when_deleteRowClicked", async () => {
        const wahlenStore = useWahlenStore();
        wahlenStore.wahlenState.wahlen = [
          prepareWahl()
            .name("Wahl1")
            .wahlID("id1")
            .beanstandeteWahlbriefe([null])
            .build(),
        ];

        const deleteBeanstandeterWahlbriefEntrySpy = vi.spyOn(
          useWahlenStore().beanstandeteWahlbriefeActions,
          "deleteBeanstandeterWahlbriefEntry"
        );

        wrapper = mount(TheBeanstandeteWahlbriefeTable, {
          global: {
            plugins: [pinia, vuetify],
          },
        });

        wrapper.vm.wahlscheinGruende = [];

        // delete first row
        const deleteButton = wrapper.findComponent<typeof VBtn>(
          `[data-test="delete-btn-0"]`
        );
        await deleteButton.trigger("click");

        expect(deleteBeanstandeterWahlbriefEntrySpy).toHaveBeenCalled();
      });
    });
  });
});
