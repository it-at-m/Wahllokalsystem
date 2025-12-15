import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import {
  enableAutoUnmount,
  flushPromises,
  mount,
  VueWrapper,
} from "@vue/test-utils";
import { afterEach, describe, expect, it, vi } from "vitest";
import { defineComponent, h, KeepAlive } from "vue";

import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import pinia from "@/plugins/pinia.ts";
import vuetify from "@/plugins/vuetify.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";
import { createUserLocalDevelopment } from "@/types/User.ts";

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    b1: number;
    b2: number;
    b: number;
  }
}

const mockDefinitions = vi.hoisted(() => ({
  getStimmabgabevermerke: vi.fn(),
  getStimmzettelumschlaege: vi.fn(),
  getWahlOrUndefinedById: vi.fn(),
}));

vi.mock(
  "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts",
  () => ({
    useStimmabgabevermerkeService: () => ({
      getStimmabgabevermerke: mockDefinitions.getStimmabgabevermerke,
    }),
  })
);

vi.mock(
  "@/composables/ergebnisermittlung/ergebnisermittlungService.ts",
  () => ({
    useErgebnisermittlungService: () => ({
      getStimmzettelumschlaege: mockDefinitions.getStimmzettelumschlaege,
    }),
  })
);

vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: () => ({
    waehlerverzeichnisActions: {
      getWaehlerverzeichnisNummerOrUndefinedById: vi.fn(() => 1),
    },
    wahlenActions: {
      getWahlOrUndefinedById: mockDefinitions.getWahlOrUndefinedById,
    },
  }),
}));

describe("TheMBWWaehlerAnzeigenCard.vue", () => {
  const { prepareWahl } = useWahlTestDataFactory();
  const {
    prepareWahldaten,
    prepareStimmabgabevermerke,
    prepareVermerk,
    prepareStimmzettel,
  } = useStimmabgabevermerkeTestDataFactory();

  let wrapper: VueWrapper<
    InstanceType<ReturnType<typeof createKeepAliveComponent>>
  >;

  const wahlId = "wahlId";
  const wahlbezirkId = "wahlbezirkId";

  enableAutoUnmount(afterEach);

  it("should_calculateB1AndB2_when_wahlbezirksartIsUWB", async () => {
    const userStore = useUserStore(pinia);
    userStore.setUser(createUserLocalDevelopment());

    mockDefinitions.getStimmabgabevermerke.mockReturnValue(
      prepareStimmabgabevermerke()
        .wahlbezirkID(wahlbezirkId)
        .waehlerverzeichnisNummer(1)
        .wahldaten([
          prepareWahldaten()
            .eingenommeneWahlscheine(
              new Map([
                [StimmzettelStimmzettelartEnum.Klein, 1],
                [StimmzettelStimmzettelartEnum.Beide, 1],
              ])
            )
            .vermerke([
              prepareVermerk()
                .blattnummer(1)
                .stimmzettel([
                  prepareStimmzettel()
                    .anzahl(1)
                    .stimmzettelart(StimmzettelStimmzettelartEnum.Klein)
                    .build(),
                  prepareStimmzettel()
                    .anzahl(1)
                    .stimmzettelart(StimmzettelStimmzettelartEnum.Beide)
                    .build(),
                ])
                .build(),
              prepareVermerk()
                .blattnummer(2)
                .stimmzettel([
                  prepareStimmzettel()
                    .anzahl(1)
                    .stimmzettelart(StimmzettelStimmzettelartEnum.Klein)
                    .build(),
                ])
                .build(),
            ])
            .build(),
        ])
        .build()
    );

    const keepAliveWrapperComponent = createKeepAliveComponent(
      wahlId,
      wahlbezirkId
    );

    wrapper = mount(keepAliveWrapperComponent, {
      global: { plugins: [pinia, vuetify] },
      props: {
        wahlId,
        wahlbezirkId,
      },
    });

    await flushPromises();

    expect(mockDefinitions.getStimmabgabevermerke).toHaveBeenCalledWith(
      wahlbezirkId,
      1
    );

    const theMBWWaehlerAnzeigenCardComponentWrapper = wrapper.findComponent(
      TheMBWWaehlerAnzeigenCard
    );
    expect(theMBWWaehlerAnzeigenCardComponentWrapper.vm.b1).toBe(3);
    expect(theMBWWaehlerAnzeigenCardComponentWrapper.vm.b2).toBe(2);
    expect(theMBWWaehlerAnzeigenCardComponentWrapper.vm.b).toBe(0);
  });

  it("should_calculateB_when_wahlbezirksartIsBWB", async () => {
    const userStore = useUserStore(pinia);
    const user = createUserLocalDevelopment();
    user.wahlbezirksArt = "BWB";
    userStore.setUser(user);

    const wahl = prepareWahl().wahlID(wahlId).build();

    mockDefinitions.getWahlOrUndefinedById.mockReturnValue(wahl);

    mockDefinitions.getStimmzettelumschlaege.mockReturnValue({
      anzahlWaehler: 4,
    });

    const keepAliveWrapperComponent = createKeepAliveComponent(
      wahlId,
      wahlbezirkId
    );

    wrapper = mount(keepAliveWrapperComponent, {
      global: { plugins: [pinia, vuetify] },
      props: {
        wahlId,
        wahlbezirkId,
      },
    });

    await flushPromises();

    expect(mockDefinitions.getStimmzettelumschlaege).toHaveBeenCalledWith(
      wahl,
      wahlbezirkId,
      "",
      false
    );

    const theMBWWaehlerAnzeigenCardComponentWrapper = wrapper.findComponent(
      TheMBWWaehlerAnzeigenCard
    );
    expect(theMBWWaehlerAnzeigenCardComponentWrapper.vm.b1).toBe(0);
    expect(theMBWWaehlerAnzeigenCardComponentWrapper.vm.b2).toBe(0);
    expect(theMBWWaehlerAnzeigenCardComponentWrapper.vm.b).toBe(4);
  });
});

function createKeepAliveComponent(wahlId: string, wahlbezirkId: string) {
  return defineComponent({
    render() {
      return h(KeepAlive, null, {
        default: () =>
          h(TheMBWWaehlerAnzeigenCard, {
            wahlId,
            wahlbezirkId,
          }),
      });
    },
  });
}
