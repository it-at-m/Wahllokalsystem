import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
import { createPinia } from "pinia";
import { afterEach, beforeAll, beforeEach, describe, expect, it } from "vitest";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import { VAutocomplete } from "vuetify/components";
import * as directives from "vuetify/directives";

import BaseAutocompleteWahltag from "@/components/common/BaseAutocompleteWahltag.vue";
import pinia from "@/plugins/pinia";
import { getSnapshotFilename } from "../../utils/testutils.ts";

const wahltage = [
  {
    wahltagID: "1",
    wahltag: "24.12.2025",
    beschreibung: "Weihnachten",
    nummer: "1.1",
  },
  {
    wahltagID: "2",
    wahltag: "01.01.2025",
    beschreibung: "Neujahr",
    nummer: "2.1",
  },
  {
    wahltagID: "3",
    wahltag: "20.03.2025",
    beschreibung: "Frühlingsanfang",
    nummer: "3.1",
  },
];

describe("BaseAutocompleteWahltag.vue", () => {
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

    wrapper = mount(BaseAutocompleteWahltag, {
      global: { plugins: [pinia, vuetify] },
      props: {
        items: wahltage,
      },
    });
  });

  enableAutoUnmount(afterEach);

  describe("visual logic", () => {
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
      expect(wrapper.html()).toContain(tag.wahltag);
      expect(wrapper.html()).not.toContain(tag.beschreibung);
    });
  });

  describe("behavioral logic", () => {
    it("should_emitWahltag_when_elementIsSelected", async () => {
      const tag = wahltage[2];

      const autocomplete = wrapper.findComponent(VAutocomplete);
      await autocomplete.setValue(tag);

      expect(wrapper.emitted()).toHaveProperty("update:modelValue");
      expect(wrapper.emitted("update:modelValue")).toEqual([[tag]]);
    });
  });
});
