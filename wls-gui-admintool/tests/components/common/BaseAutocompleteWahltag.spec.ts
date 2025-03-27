import { enableAutoUnmount, mount, VueWrapper } from "@vue/test-utils";
import { createPinia } from "pinia";
import { afterEach, beforeAll, beforeEach, describe, expect, it } from "vitest";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

import BaseAutocompleteWahltag from "@/components/common/BaseAutocompleteWahltag.vue";
import pinia from "@/plugins/pinia";
import { getSnapshotFilename } from "../../utils/testutils.ts";

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
        items: [
          {
            wahltagID: "1",
            wahltag: "27.02.1988",
            beschreibung: "S",
            nummer: "1.1",
          },
          {
            wahltagID: "2",
            wahltag: "31.01.1998",
            beschreibung: "D",
            nummer: "2.1",
          },
          {
            wahltagID: "3",
            wahltag: "04.04.2000",
            beschreibung: "V",
            nummer: "3.1",
          },
          {
            wahltagID: "4",
            wahltag: "31.10.1999",
            beschreibung: "G",
            nummer: "4.1",
          },
          {
            wahltagID: "5",
            wahltag: "05.11.",
            beschreibung: "R",
            nummer: "5.1",
          },
          {
            wahltagID: "6",
            wahltag: "14.05.1977",
            beschreibung: "N",
            nummer: "6.1",
          },
        ],
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
  });

  describe("visual logic", () => {
    it("should_renderDateOfWahltag_when_elementIsSelected", async (context) => {
      const wahltag = {
        wahltagID: "2",
        wahltag: "31.01.1998",
        beschreibung: "D",
        nummer: "2.1",
      };
      await wrapper.setProps({ modelValue: wahltag });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
      expect(wrapper.html()).toContain(wahltag.wahltag);
      expect(wrapper.html()).not.toContain(wahltag.beschreibung);
    });
  });

  describe("behavioral logic", () => {
    it("should_emitWahltag_when_elementIsSelected", async (context) => {
      await wrapper.setProps({
        modelValue: {
          wahltagID: "2",
          wahltag: "31.01.1998",
          beschreibung: "D",
          nummer: "2.1",
        },
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
