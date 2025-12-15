import type { VueWrapper } from "@vue/test-utils";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { enableAutoUnmount, flushPromises, mount } from "@vue/test-utils";
import { afterEach, describe, expect, it, vi } from "vitest";
import { defineComponent, h, KeepAlive } from "vue";

import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";
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

vi.mock("@/composables/ergebnismeldung/common/ergebnisService.ts", () => ({
  useErgebnisService: () => ({
    getErgebnisse: mockDefinitions.getErgebnisse,
  }),
}));

describe("TheMBWUngueltigeStimmenAnzeigenCard.vue", () => {
  const { prepareErgebnis, prepareErgebnisse } = useErgebnisseTestDataFactory();

  let wrapper: VueWrapper<
    InstanceType<ReturnType<typeof createKeepAliveComponent>>
  >;

  const wahlId = "wahlId";
  const wahlbezirkId = "wahlbezirkId";

  enableAutoUnmount(afterEach);

  it("should_getErgebnisseStapelD_when_mountedAndRequestReturnsErgebnisse", async () => {
    const keepAliveWrapperComponent = createKeepAliveComponent(
      wahlId,
      wahlbezirkId
    );

    mockDefinitions.getErgebnisse.mockReturnValue(
      prepareErgebnisse()
        .ergebnisse([prepareErgebnis().ergebnis(3).build()])
        .build()
    );

    wrapper = mount(keepAliveWrapperComponent, {
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
    expect(
      wrapper.findComponent(TheMBWUngueltigeStimmenAnzeigenCard).vm
        .ungueltigeStimmen
    ).toBe(3);
  });

  it("should_setErgebnisTo0_when_mountedAndRequestReturnsNoErgebnisse", async () => {
    const keepAliveWrapperComponent = createKeepAliveComponent(
      wahlId,
      wahlbezirkId
    );

    mockDefinitions.getErgebnisse.mockReturnValue(null);

    wrapper = mount(keepAliveWrapperComponent, {
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
    expect(
      wrapper.findComponent(TheMBWUngueltigeStimmenAnzeigenCard).vm
        .ungueltigeStimmen
    ).toBe(0);
  });
});

function createKeepAliveComponent(wahlId: string, wahlbezirkId: string) {
  return defineComponent({
    render() {
      return h(KeepAlive, null, {
        default: () =>
          h(TheMBWUngueltigeStimmenAnzeigenCard, {
            wahlId,
            wahlbezirkId,
          }),
      });
    },
  });
}
