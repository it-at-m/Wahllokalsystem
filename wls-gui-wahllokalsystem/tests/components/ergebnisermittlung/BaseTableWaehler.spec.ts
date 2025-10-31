import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import {
  enableAutoUnmount,
  flushPromises,
  mount,
  VueWrapper,
} from "@vue/test-utils";
import { afterEach, describe, expect, it, vi } from "vitest";

import BaseTableWaehler from "@/components/ergebnisermittlung/BaseTableWaehler.vue";
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

describe("BaseTableWaehler.vue", () => {
  const { prepareWahl } = useWahlTestDataFactory();
  const {
    prepareWahldaten,
    prepareStimmabgabevermerke,
    prepareVermerk,
    prepareStimmzettel,
  } = useStimmabgabevermerkeTestDataFactory();

  let wrapper: VueWrapper<InstanceType<typeof BaseTableWaehler>>;

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

    wrapper = mount(BaseTableWaehler, {
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

    expect(wrapper.vm.b1).toBe(3);
    expect(wrapper.vm.b2).toBe(2);
    expect(wrapper.vm.b).toBe(0);
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

    wrapper = mount(BaseTableWaehler, {
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

    expect(wrapper.vm.b1).toBe(0);
    expect(wrapper.vm.b2).toBe(0);
    expect(wrapper.vm.b).toBe(4);
  });
});
