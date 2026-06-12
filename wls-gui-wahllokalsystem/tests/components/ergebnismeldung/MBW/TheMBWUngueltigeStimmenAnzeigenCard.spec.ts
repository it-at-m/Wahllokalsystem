import type { VueWrapper } from "@vue/test-utils";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { useBedenklicherStimmzettelTestDataFactory } from "@tests/utils/ergebnismeldung/MBW/bedenklicherStimmzettelTestDataFactory.ts";
import { enableAutoUnmount, flushPromises, mount } from "@vue/test-utils";
import { afterEach, describe, expect, it, vi } from "vitest";
import { defineComponent, h, KeepAlive } from "vue";

import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";
import pinia from "@/plugins/pinia.ts";
import vuetify from "@/plugins/vuetify.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { ValidityEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/ValidityEnum.ts";

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    ungueltigeStimmen: number;
    ungueltigeStimmzettelNachBeschluss: number;
  }
}

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisse: vi.fn(),
  getBedenklicheStimmzettel: vi.fn(),
}));

vi.mock(
  import("@/composables/ergebnismeldung/common/ergebnisService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useErgebnisService: () => ({
        ...mod.useErgebnisService(),
        getErgebnisse: mockDefinitions.getErgebnisse,
      }),
    };
  }
);
vi.mock(
  import("@/composables/ergebnismeldung/MBW/bedenklicheStimmzettelService.ts"),
  () => ({
    useBedenklicheStimmzettelService: () => ({
      getBedenklicheStimmzettel: mockDefinitions.getBedenklicheStimmzettel,
      saveBedenklicheStimmzettel: vi.fn(),
    }),
  })
);

describe("TheMBWUngueltigeStimmenAnzeigenCard.vue", () => {
  const { prepareErgebnis, prepareErgebnisse } = useErgebnisseTestDataFactory();
  const { prepareBedenklicherStimmzettel } =
    useBedenklicherStimmzettelTestDataFactory();

  let wrapper: VueWrapper<
    InstanceType<ReturnType<typeof createKeepAliveComponent>>
  >;

  const wahlId = "wahlId";
  const wahlbezirkId = "wahlbezirkId";

  enableAutoUnmount(afterEach);

  it("should_LoadData_when_mountedAndRequestReturnsErgebnisse", async () => {
    const keepAliveWrapperComponent = createKeepAliveComponent(
      wahlId,
      wahlbezirkId
    );

    mockDefinitions.getErgebnisse.mockReturnValue(
      prepareErgebnisse()
        .ergebnisse([prepareErgebnis().ergebnis(3).build()])
        .build()
    );
    const mockedBedenklicheStimmzettel = [
      prepareBedenklicherStimmzettel().validity(ValidityEnum.INVALID).build(),
      prepareBedenklicherStimmzettel().validity(ValidityEnum.INVALID).build(),
      prepareBedenklicherStimmzettel().validity(ValidityEnum.VALID).build(),
      prepareBedenklicherStimmzettel().validity(ValidityEnum.VALID).build(),
      prepareBedenklicherStimmzettel()
        .validity(ValidityEnum.PARTIAL_VALID)
        .build(),
      prepareBedenklicherStimmzettel().validity(ValidityEnum.INVALID).build(),
    ];
    mockDefinitions.getBedenklicheStimmzettel.mockReturnValue(
      mockedBedenklicheStimmzettel
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
    expect(
      wrapper.findComponent(TheMBWUngueltigeStimmenAnzeigenCard).vm
        .ungueltigeStimmzettelNachBeschluss
    ).toBe(3);
  });

  it("should_setDataTo0_when_mountedAndRequestReturnsNoErgebnisse", async () => {
    const keepAliveWrapperComponent = createKeepAliveComponent(
      wahlId,
      wahlbezirkId
    );

    mockDefinitions.getErgebnisse.mockReturnValue(null);
    mockDefinitions.getBedenklicheStimmzettel.mockReturnValue(null);

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
    expect(
      wrapper.findComponent(TheMBWUngueltigeStimmenAnzeigenCard).vm
        .ungueltigeStimmzettelNachBeschluss
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
