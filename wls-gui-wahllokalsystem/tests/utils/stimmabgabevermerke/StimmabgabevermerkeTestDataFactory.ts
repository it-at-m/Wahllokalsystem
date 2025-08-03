import type { Stimmabgabevermerke } from "@/types/stimmabgabermerke/Stimmabgabevermerke.ts";
import type { Wahldaten } from "@/types/stimmabgabermerke/Wahldaten.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";

const { generateRandomString, generateRandomNumber } =
  useCommonTestDataFactory();

export function useStimmabgabevermerkeTestDataFactory() {
  function createWahldaten(): Wahldaten {
    return {
      eingenommeneWahlscheine: new Map([
        [
          EingenommenerWahlscheinStimmzettelartEnum.Klein,
          generateRandomNumber(4),
        ],
      ]),
      waehlerverzeichnisNummer: generateRandomNumber(1),
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(10),
    };
  }

  function createStimmabgabevermerke(): Stimmabgabevermerke {
    const wahldatenOne = prepareWahldaten()
      .eingenommeneWahlscheine(
        new Map([[EingenommenerWahlscheinStimmzettelartEnum.Klein, 50]])
      )
      .build();
    const wahldatenTwo = prepareWahldaten()
      .eingenommeneWahlscheine(
        new Map([[EingenommenerWahlscheinStimmzettelartEnum.Klein, 20]])
      )
      .build();
    const wahldatenThree = prepareWahldaten()
      .eingenommeneWahlscheine(
        new Map([[EingenommenerWahlscheinStimmzettelartEnum.Klein, 30]])
      )
      .build();
    const wahldaten = new Set<Wahldaten>([
      wahldatenOne,
      wahldatenTwo,
      wahldatenThree,
    ]);

    return {
      anzahlBlaetter: generateRandomNumber(1),
      waehlerverzeichnisNummer: generateRandomNumber(1),
      wahlbezirkID: generateRandomString(1),
      wahldaten: wahldaten,
    };
  }

  function prepareWahldaten(): Builder<Wahldaten> {
    return proxyBuilder<Wahldaten>(createWahldaten());
  }

  function prepareStimmabgabevermerke(): Builder<Stimmabgabevermerke> {
    return proxyBuilder<Stimmabgabevermerke>(createStimmabgabevermerke());
  }

  return {
    createWahldaten,
    prepareWahldaten,
    prepareStimmabgabevermerke,
    createStimmabgabevermerke,
  };
}
