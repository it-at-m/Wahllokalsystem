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
        items: [],
        multiple: true,
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
});
