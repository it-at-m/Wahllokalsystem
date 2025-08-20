import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";
import type { Wahldaten } from "@/types/stimmabgabevermerke/Wahldaten.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { StimmzettelDTOStimmzettelartEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";

const { generateRandomString, generateRandomNumber } =
  useCommonTestDataFactory();

export function useStimmabgabevermerkeTestDataFactory() {
  function createWahldaten(): Wahldaten {
    return {
      eingenommeneWahlscheine: new Map([
        [_getRandomEnumValue(), generateRandomNumber(4)],
      ]),
      vermerke: [
        {
          blattnummer: 2,
          stimmzettel: [
            {
              anzahl: generateRandomNumber(2),
              stimmzettelart: StimmzettelStimmzettelartEnum.Klein,
            },
          ],
        },
        {
          blattnummer: 3,
          stimmzettel: [
            {
              anzahl: generateRandomNumber(2),
              stimmzettelart: StimmzettelStimmzettelartEnum.Klein,
            },
          ],
        },
      ],

      waehlerverzeichnisNummer: generateRandomNumber(1),
      wahlID: generateRandomString(10),
    };
  }

  function createStimmabgabevermerke(): Stimmabgabevermerke {
    const wahldatenOne = prepareWahldaten()
      .eingenommeneWahlscheine(
        new Map([[_getRandomEnumValue(), generateRandomNumber(2)]])
      )
      .build();
    const wahldatenTwo = prepareWahldaten()
      .eingenommeneWahlscheine(
        new Map([[_getRandomEnumValue(), generateRandomNumber(2)]])
      )
      .build();
    const wahldatenThree = prepareWahldaten()
      .eingenommeneWahlscheine(
        new Map([[_getRandomEnumValue(), generateRandomNumber(2)]])
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
      wahldaten: wahldaten,
    };
  }

  function prepareWahldaten(): Builder<Wahldaten> {
    return proxyBuilder<Wahldaten>(createWahldaten());
  }

  function prepareStimmabgabevermerke(): Builder<Stimmabgabevermerke> {
    return proxyBuilder<Stimmabgabevermerke>(createStimmabgabevermerke());
  }

  function _getRandomEnumValue() {
    const enumValues = Object.values(StimmzettelDTOStimmzettelartEnum);
    const randomIndex = Math.floor(Math.random() * enumValues.length);
    return enumValues[randomIndex];
  }

  return {
    createWahldaten,
    prepareWahldaten,
    prepareStimmabgabevermerke,
    createStimmabgabevermerke,
  };
}
