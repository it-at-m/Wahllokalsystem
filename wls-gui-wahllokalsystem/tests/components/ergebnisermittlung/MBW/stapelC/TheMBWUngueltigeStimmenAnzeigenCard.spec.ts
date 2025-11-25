import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import {
  enableAutoUnmount,
  flushPromises,
  mount,
  VueWrapper,
} from "@vue/test-utils";
import { afterEach, describe, expect, it, vi } from "vitest";

import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnisermittlung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";
import pinia from "@/plugins/pinia.ts";
import vuetify from "@/plugins/vuetify.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    ungueltigeStimmen: number;
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

describe("TheMBWUngueltigeStimmenAnzeigenCard.vue", () => {
  const { prepareErgebnis, prepareErgebnisse } = useErgebnisseTestDataFactory();

  let wrapper: VueWrapper<
    InstanceType<typeof TheMBWUngueltigeStimmenAnzeigenCard>
  >;

  const wahlId = "wahlId";
  const wahlbezirkId = "wahlbezirkId";

  enableAutoUnmount(afterEach);

  it("should_getErgebnisseStapelD_when_mountedAndRequestReturnsErgebnisse", async () => {
    mockDefinitions.getErgebnisse.mockReturnValue(
      prepareErgebnisse()
        .ergebnisse([prepareErgebnis().ergebnis(3).build()])
        .build()
    );

    wrapper = mount(TheMBWUngueltigeStimmenAnzeigenCard, {
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
      StapelArtEnum.MbwDUngueltig,
      false
    );
    expect(wrapper.vm.ungueltigeStimmen).toBe(3);
  });

  it("should_setErgebnisTo0_when_mountedAndRequestReturnsNoErgebnisse", async () => {
    mockDefinitions.getErgebnisse.mockReturnValue(null);

    wrapper = mount(TheMBWUngueltigeStimmenAnzeigenCard, {
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
      StapelArtEnum.MbwDUngueltig,
      false
    );
    expect(wrapper.vm.ungueltigeStimmen).toBe(0);
  });
});
