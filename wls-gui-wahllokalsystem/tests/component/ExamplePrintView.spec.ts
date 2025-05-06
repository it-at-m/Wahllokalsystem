import { COMPONENT_EVENT_TESTS } from "@tests/utils/testutils.ts";
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
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import { VBtn } from "vuetify/components";
import * as directives from "vuetify/directives";

import pinia from "@/plugins/pinia";
import ExamplePrintView from "@/views/ExamplePrintView.vue";

describe("ExamplePrintView.vue", () => {
  let vuetify: ReturnType<typeof createVuetify>;
  let wrapper: VueWrapper;

  beforeAll(() => {
    createPinia();
    createVuetify();
  });

  beforeEach(() => {
    vuetify = createVuetify({
      components,
      directives,
    });

    wrapper = mount(ExamplePrintView, {
      global: { plugins: [pinia, vuetify] },
    });
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe(COMPONENT_EVENT_TESTS, () => {
    vi.stubGlobal("open", vi.fn());

    it("should_openNewPrintWindow_when_printButtonIsClicked", async () => {
      const printSpy = vi.spyOn(window, "open");

      const printButton = wrapper.findComponent(VBtn);
      await printButton.trigger("click");

      expect(printSpy).toHaveBeenCalled();
    });
  });
});
