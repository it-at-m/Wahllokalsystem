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
import { VIcon } from "vuetify/components";
import * as directives from "vuetify/directives";

import BaseIconWahlbezirksart from "@/components/common/icons/BaseIconWahlbezirksart.vue";
import { useUserStore } from "@/stores/user";

describe("BaseIconWahlbezirksart.vue", () => {
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

    wrapper = mount(BaseIconWahlbezirksart, {
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
    vi.clearAllMocks();
  });

  enableAutoUnmount(afterEach);

  describe("visual logic", () => {
    it("should_displayUWB_when_storeVariableIsUWB", async (context) => {
      const userStore = useUserStore();
      console.log(wrapper.html);
      userStore.setWahlbezirksArt("UWB");

      await wrapper.vm.$nextTick();

      const icon = wrapper.findComponent(VIcon);
      expect(icon.attributes("class")).toContain("mdiVote");
    });
  });

  it("should_displayBWB_when_storeVariableIsBWB", async (context) => {
    const userStore = useUserStore();
    userStore.setWahlbezirksArt("BWB");

    await nextTick();

    const icon = wrapper.findComponent(VIcon);
    expect(icon.attributes("class")).toContain("mdiEmail");
  });

  it("should_displayBWB_when_storeVariableIsBWB", async (context) => {
    const userStore = useUserStore();
    userStore.setWahlbezirksArt(undefined);

    await nextTick();

    const icon = wrapper.findComponent(VIcon);
    expect(icon.attributes("class")).toContain("mdiEmail");
  });
});
