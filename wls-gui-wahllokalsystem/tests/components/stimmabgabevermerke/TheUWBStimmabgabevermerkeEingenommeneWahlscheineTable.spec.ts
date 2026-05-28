import type { TestingPinia } from "@pinia/testing";

import { createTestingPinia } from "@pinia/testing";
import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { VNumberInput } from "vuetify/components";

import TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable.vue";
import vuetify from "@/plugins/vuetify.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlNameOrBlankStringById: vi.fn(),
}));
vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: () => ({
    wahlenActions: {
      getWahlNameOrBlankStringById:
        mockDefinitions.getWahlNameOrBlankStringById,
    },
  }),
}));

describe("TheUwbStimmabgabevermerkeEingenommeneWahlscheineTable", () => {
  const { createStimmabgabevermerke, prepareStimmabgabevermerke } =
    useStimmabgabevermerkeTestDataFactory();
  let stimmabgabevermerkeStore: ReturnType<typeof useStimmabgabevermerkeStore>;
  let testPinia: TestingPinia;

  beforeEach(() => {
    testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderTableWithThreeEntries_when_stimmabgabevermerkeHasThreeWahldatenEntries", async (context) => {
      const stimmabgabevermerkeOne = prepareStimmabgabevermerke()
        .eingenommeneWahlscheine(
          new Map([[EingenommenerWahlscheinStimmzettelartEnum.Klein, 30]])
        )
        .build();
      const stimmabgabevermerkeTwo = prepareStimmabgabevermerke()
        .eingenommeneWahlscheine(
          new Map([[EingenommenerWahlscheinStimmzettelartEnum.Klein, 50]])
        )
        .build();
      const stimmabgabevermerkeThree = prepareStimmabgabevermerke()
        .eingenommeneWahlscheine(
          new Map([[EingenommenerWahlscheinStimmzettelartEnum.Klein, 60]])
        )
        .build();
      stimmabgabevermerkeStore = useStimmabgabevermerkeStore(testPinia);
      stimmabgabevermerkeStore.stimmabgabevermerke = [
        stimmabgabevermerkeOne,
        stimmabgabevermerkeTwo,
        stimmabgabevermerkeThree,
      ];

      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("Wahlname");

      const wrapper = mount(
        TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable,
        {
          global: {
            plugins: [testPinia, vuetify],
          },
        }
      );

      expect(wrapper.findAllComponents(VNumberInput).length).toBe(3);
      expect(wrapper.findAll("th").length).toBe(4);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    it("should_changeValuesInStore_when_numberInputValueChanges", () => {
      const stimmabgabevermerke = createStimmabgabevermerke();
      stimmabgabevermerkeStore = useStimmabgabevermerkeStore(testPinia);
      stimmabgabevermerkeStore.stimmabgabevermerke = [stimmabgabevermerke];

      mockDefinitions.getWahlNameOrBlankStringById.mockReturnValue("Wahlname");
      const newNumberInputValue = 42;

      const wrapper = mount(
        TheUWBStimmabgabevermerkeEingenommeneWahlscheineTable,
        {
          global: {
            plugins: [testPinia, vuetify],
          },
        }
      );

      const numberInputs = wrapper.findAllComponents(VNumberInput);
      numberInputs.forEach((numberInput) => {
        numberInput.setValue(newNumberInputValue);
      });

      stimmabgabevermerkeStore.stimmabgabevermerke.forEach(
        (stimmabgabevermerkeEntries) => {
          expect(
            stimmabgabevermerkeEntries.eingenommeneWahlscheine.get(
              EingenommenerWahlscheinStimmzettelartEnum.Klein
            )
          ).toBe(newNumberInputValue);
        }
      );
    });
  });
});
