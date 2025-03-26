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
            beschreibung: "Sebastian",
            nummer: "1.1",
          },
          {
            wahltagID: "2",
            wahltag: "31.01.1998",
            beschreibung: "Daniel",
            nummer: "2.1",
          },
          {
            wahltagID: "3",
            wahltag: "04.04.2000",
            beschreibung: "Viviane",
            nummer: "3.1",
          },
          {
            wahltagID: "4",
            wahltag: "31.10.1999",
            beschreibung: "Gerhard",
            nummer: "4.1",
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

  describe("behavioral logic", () => {
    it("should_emitWahltag_when_elementIsSelected", async (context) => {
      await wrapper.setProps({
        modelValue: {
          wahltagID: "2",
          wahltag: "31.01.1998",
          beschreibung: "Daniel",
          nummer: "2.1",
        },
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
