import type { Wahlvorstandsmitglied } from "@/types/wahlvorstand/Wahlvorstandsmitglied";

import { createTestingPinia } from "@pinia/testing";
import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
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
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

import TheWahlvorstandMitgliederTable from "@/components/wahlvorstand/TheWahlvorstandMitgliederTable.vue";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";
import { WahlvorstandsmitgliedBuilder } from "@/types/wahlvorstand/Wahlvorstandsmitglied";
import { getSnapshotFilename } from "../../utils/testutils";

describe("TheWahlvorstandMitgliederTable.vue", () => {
  let vuetify: ReturnType<typeof createVuetify>;
  let wrapper: VueWrapper;

  beforeAll(() => {
    createPinia();
  });

  beforeEach(() => {
    vuetify = createVuetify({
      components,
      directives,
    });

    wrapper = mount(TheWahlvorstandMitgliederTable, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
          }),
          vuetify,
        ],
      },
    });
    // Löscht die mocking-History
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe("visual logic", () => {
    it("should_showMultipleLines_when_multipleWahlvorstandsmitgliederAreGiven", async (context) => {
      const wahlvorstandStore = useWahlvorstandStore();

      const wahlvorstandsmitglieder = [] as Wahlvorstandsmitglied[];
      for (let i = 0; i < 5; i++) {
        wahlvorstandsmitglieder.push(
          WahlvorstandsmitgliedBuilder.createComplete()
            .withFamilienname(`famname ${i}`)
            .withVorname(`vorname ${i}`)
            .withAnwesend(i % 2 === 0)
        );
      }

      wahlvorstandStore.wahlvorstand.wahlvorstandsmitglieder =
        wahlvorstandsmitglieder;

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showOnlyHeaders_when_noWahlvorstandsmitgliederAreGiven", async (context) => {
      const wahlvorstandStore = useWahlvorstandStore();

      wahlvorstandStore.wahlvorstand.wahlvorstandsmitglieder = [];

      await nextTick();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe("behavioral logic", () => {
    describe("update:model-value", () => {
      it("should_setAnwesendTrue_when_checkBoxForMitgliedThatIsNotAnwesendWasClicked", async () => {
        const wahlvorstandStore = useWahlvorstandStore();

        wahlvorstandStore.wahlvorstand.wahlvorstandsmitglieder = [
          WahlvorstandsmitgliedBuilder.createMinimal()
            .withIdentifikator("id1")
            .withAnwesend(false),
          WahlvorstandsmitgliedBuilder.createMinimal()
            .withIdentifikator("id2")
            .withAnwesend(false),
        ];

        await nextTick();

        const secondCheckBox = wrapper.findAllComponents(
          '[data-test="checkboxAnwesend"]'
        )[1];

        await secondCheckBox.setValue(true);

        expect(
          wahlvorstandStore.changeAnwesendOfMitglied
        ).toHaveBeenCalledTimes(1);
        expect(wahlvorstandStore.changeAnwesendOfMitglied).toHaveBeenCalledWith(
          true,
          "id2"
        );
      });

      it("should_setAnwesendFalse_when_checkBoxForMitgliedThatChangedToFalse", async () => {
        const wahlvorstandStore = useWahlvorstandStore();

        wahlvorstandStore.wahlvorstand.wahlvorstandsmitglieder = [
          WahlvorstandsmitgliedBuilder.createMinimal()
            .withIdentifikator("id1")
            .withAnwesend(true),
          WahlvorstandsmitgliedBuilder.createMinimal()
            .withIdentifikator("id2")
            .withAnwesend(true),
        ];

        await nextTick();

        const secondCheckBox = wrapper.findAllComponents(
          '[data-test="checkboxAnwesend"]'
        )[1];

        await secondCheckBox.setValue(false);

        expect(
          wahlvorstandStore.changeAnwesendOfMitglied
        ).toHaveBeenCalledTimes(1);
        expect(wahlvorstandStore.changeAnwesendOfMitglied).toHaveBeenCalledWith(
          false,
          "id2"
        );
      });
    });
  });
});
