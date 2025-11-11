import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import {
  enableAutoUnmount,
  flushPromises,
  mount,
  VueWrapper,
} from "@vue/test-utils";
import { afterEach, describe, expect, it, vi } from "vitest";

import TheTableUngueltigeStimmen from "@/components/ergebnisermittlung/TheTableUngueltigeStimmen.vue";
import pinia from "@/plugins/pinia.ts";
import vuetify from "@/plugins/vuetify.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    c: number;
  }
}

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisse: vi.fn(),
}));

vi.mock("@/composables/ergebnismeldung/ergebnisService.ts", () => ({
  useErgebnisService: () => ({
    getErgebnisse: mockDefinitions.getErgebnisse,
  }),
}));

describe("TheTableUngueltigeStimmen.vue", () => {
  const { prepareErgebnis, prepareErgebnisse } = useErgebnisseTestDataFactory();

  let wrapper: VueWrapper<InstanceType<typeof TheTableUngueltigeStimmen>>;

  const wahlId = "wahlId";
  const wahlbezirkId = "wahlbezirkId";

  enableAutoUnmount(afterEach);

  it("should_getErgebnisseStapelD_when_mountedAndRequestReturnsErgebnisse", async () => {
    mockDefinitions.getErgebnisse.mockReturnValue(
      prepareErgebnisse()
        .ergebnisse([prepareErgebnis().ergebnis(3).build()])
        .build()
    );

    wrapper = mount(TheTableUngueltigeStimmen, {
      global: { plugins: [pinia, vuetify] },
      props: {
        wahlId,
        wahlbezirkId,
      },
    });

    await flushPromises();

    expect(mockDefinitions.getErgebnisse).toHaveBeenCalledWith(
      wahlbezirkId,
      wahlId,
      StapelArtEnum.MbwD,
      false
    );
    expect(wrapper.vm.c).toBe(3);
  });

  it("should_setErgebnisTo0_when_mountedAndRequestReturnsNoErgebnisse", async () => {
    mockDefinitions.getErgebnisse.mockReturnValue(null);

    wrapper = mount(TheTableUngueltigeStimmen, {
      global: { plugins: [pinia, vuetify] },
      props: {
        wahlId,
        wahlbezirkId,
      },
    });

    await flushPromises();

    expect(mockDefinitions.getErgebnisse).toHaveBeenCalledWith(
      wahlbezirkId,
      wahlId,
      StapelArtEnum.MbwD,
      false
    );
    expect(wrapper.vm.c).toBe(0);
  });
});
