import type { Wahltag } from "@/types/wahltag/Wahltag.ts";

import {
  COMPONENT_EVENT_TESTS,
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
import { createPinia } from "pinia";
import { afterEach, beforeAll, beforeEach, describe, expect, it } from "vitest";
import { VAutocomplete } from "vuetify/components";

import BaseAutocompleteWahltag from "@/components/common/BaseAutocompleteWahltag.vue";
import pinia from "@/plugins/pinia";
import vuetify from "@/plugins/vuetify";

const wahltage: Wahltag[] = [
  {
    wahltag: new Date("2025-12-24"),
    events: [
      {
        wahltagID: "1",
        beschreibung: "Weihnachten",
        nummer: "1.1",
      },
    ],
  },
  {
    wahltag: new Date("2025-01-01"),
    events: [
      {
        wahltagID: "2",
        beschreibung: "Neujahr",
        nummer: "2.1",
      },
    ],
  },
  {
    wahltag: new Date("2025-03-20"),
    events: [
      {
        wahltagID: "3",
        beschreibung: "Frühlingsanfang",
        nummer: "3.1",
      },
    ],
    active: true,
  },
];

describe("BaseAutocompleteWahltag.vue", () => {
  let wrapper: VueWrapper;

  beforeAll(() => {
    createPinia();
  });

  beforeEach(() => {
    wrapper = mount(BaseAutocompleteWahltag, {
      global: { plugins: [pinia, vuetify] },
      props: {
        items: wahltage,
      },
    });
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderAutocompleteWithCorrectLabel_when_componentIsMounted", async (context) => {
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderDateOfWahltag_when_elementIsSelected", async (context) => {
      const tag = wahltage[0];
      await wrapper.setProps({ modelValue: tag });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderActiveChip_when_activeWahltagIsSelected", async (context) => {
      const aktiverWahltag = wahltage[2];
      await wrapper.setProps({ modelValue: aktiverWahltag });

      expect(wrapper.find("[data-test='activeChip']").exists()).toBe(true);
      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });

  describe(COMPONENT_EVENT_TESTS, () => {
    describe("update:modelValue", () => {
      it("should_emitWahltag_when_elementIsSelected", async () => {
        const tag = wahltage[2];

        const autocomplete = wrapper.findComponent(VAutocomplete);
        await autocomplete.setValue(tag);

        expect(wrapper.emitted()).toHaveProperty("update:modelValue");
        expect(wrapper.emitted("update:modelValue")).toEqual([[tag]]);
      });
    });
  });
});
