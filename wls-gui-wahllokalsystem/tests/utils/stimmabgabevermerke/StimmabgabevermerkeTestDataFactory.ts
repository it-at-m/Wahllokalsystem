import type {
  StimmabgabevermerkeDTO,
  StimmzettelDTO,
  VermerkDTO,
  WahldatenDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";
import type { Stimmzettel } from "@/types/stimmabgabevermerke/Stimmzettel.ts";
import type { Vermerke } from "@/types/stimmabgabevermerke/Vermerke.ts";
import type { Wahldaten } from "@/types/stimmabgabevermerke/Wahldaten.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { StimmzettelDTOStimmzettelartEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";

const { generateRandomString, generateRandomNumber, getRandomItem } =
  useCommonTestDataFactory();

export function useStimmabgabevermerkeTestDataFactory() {
  function createWahldaten(): Wahldaten {
    return {
      wahlbezirkID: generateRandomString(2),
      eingenommeneWahlscheine: new Map([
        [
          getRandomItem(Object.values(StimmzettelStimmzettelartEnum)),
          generateRandomNumber(4),
        ],
      ]),
      vermerke: [
        prepareVermerk().blattnummer(2).build(),
        prepareVermerk().blattnummer(3).build(),
      ],

      waehlerverzeichnisNummer: generateRandomNumber(1),
      wahlID: generateRandomString(10),
    };
  }

  function createWahldatenDTO(): WahldatenDTO {
    return {
      wahlbezirkID: generateRandomString(2),
      eingenommeneWahlscheine: [
        {
          stimmzettelart: getRandomItem(
            Object.values(StimmzettelDTOStimmzettelartEnum)
          ),
          anzahl: generateRandomNumber(4),
        },
      ],
      vermerke: [
        prepareVermerkDTO().blattnummer(2).build(),
        prepareVermerkDTO().blattnummer(3).build(),
      ],

      waehlerverzeichnisNummer: generateRandomNumber(1),
      wahlID: generateRandomString(10),
    };
  }

  function createStimmabgabevermerke(): Stimmabgabevermerke {
    const wahldatenOne = prepareWahldaten()
      .eingenommeneWahlscheine(
        new Map([
          [
            EingenommenerWahlscheinStimmzettelartEnum.Klein,
            generateRandomNumber(2),
          ],
        ])
      )
      .build();
    const wahldatenTwo = prepareWahldaten()
      .eingenommeneWahlscheine(
        new Map([
          [
            getRandomItem(Object.values(StimmzettelStimmzettelartEnum)),
            generateRandomNumber(2),
          ],
        ])
      )
      .build();
    const wahldatenThree = prepareWahldaten()
      .eingenommeneWahlscheine(
        new Map([
          [
            getRandomItem(Object.values(StimmzettelStimmzettelartEnum)),
            generateRandomNumber(2),
          ],
        ])
      )
      .build();
    const wahldaten = [wahldatenOne, wahldatenTwo, wahldatenThree];

    return {
      anzahlBlaetter: generateRandomNumber(1),
      waehlerverzeichnisNummer: generateRandomNumber(1),
      wahlbezirkID: generateRandomString(2),
      wahldaten: wahldaten,
    };
  }

  function createStimmabgabevermerkeDTO(): StimmabgabevermerkeDTO {
    const wahldatenOne = prepareWahldatenDTO()
      .eingenommeneWahlscheine([
        {
          anzahl: generateRandomNumber(1),
          stimmzettelart: StimmzettelDTOStimmzettelartEnum.Klein,
        },
      ])
      .build();
    const wahldatenTwo = prepareWahldatenDTO()
      .eingenommeneWahlscheine([
        {
          anzahl: generateRandomNumber(1),
          stimmzettelart: StimmzettelDTOStimmzettelartEnum.Klein,
        },
      ])
      .build();
    const wahldatenThree = prepareWahldatenDTO()
      .eingenommeneWahlscheine([
        {
          anzahl: generateRandomNumber(1),
          stimmzettelart: StimmzettelDTOStimmzettelartEnum.Klein,
        },
      ])
      .build();
    const wahldaten = [wahldatenOne, wahldatenTwo, wahldatenThree];

    return {
      anzahlBlaetter: generateRandomNumber(1),
      waehlerverzeichnisNummer: generateRandomNumber(1),
      wahlbezirkID: generateRandomString(2),
      wahldaten: wahldaten,
    };
  }

  function createVermerk(): Vermerke {
    return {
      blattnummer: generateRandomNumber(1),
      stimmzettel: [createStimmzettel()],
    };
  }

  function createVermerkDTO(): VermerkDTO {
    return {
      blattnummer: generateRandomNumber(1),
      stimmzettel: [createStimmzettelDTO()],
    };
  }

  function createStimmzettel(): Stimmzettel {
    return {
      anzahl: generateRandomNumber(1) + 1,
      stimmzettelart: StimmzettelStimmzettelartEnum.Klein,
    };
  }

  function createStimmzettelDTO(): StimmzettelDTO {
    return {
      anzahl: generateRandomNumber(1),
      stimmzettelart: StimmzettelDTOStimmzettelartEnum.Klein,
    };
  }

  function prepareWahldaten(): Builder<Wahldaten> {
    return proxyBuilder<Wahldaten>(createWahldaten());
  }

  function prepareWahldatenDTO(): Builder<WahldatenDTO> {
    return proxyBuilder<WahldatenDTO>(createWahldatenDTO());
  }

  function prepareStimmabgabevermerke(): Builder<Stimmabgabevermerke> {
    return proxyBuilder<Stimmabgabevermerke>(createStimmabgabevermerke());
  }

  function prepareStimmabgabevermerkeDTO(): Builder<StimmabgabevermerkeDTO> {
    return proxyBuilder<StimmabgabevermerkeDTO>(createStimmabgabevermerkeDTO());
  }

  function prepareVermerk(): Builder<Vermerke> {
    return proxyBuilder<Vermerke>(createVermerk());
  }

  function prepareStimmzettel(): Builder<Stimmzettel> {
    return proxyBuilder<Stimmzettel>(createStimmzettel());
  }

  function prepareVermerkDTO(): Builder<VermerkDTO> {
    return proxyBuilder<VermerkDTO>(createVermerkDTO());
  }

  function prepareStimmzettelDTO(): Builder<StimmzettelDTO> {
    return proxyBuilder<StimmzettelDTO>(createStimmzettelDTO());
  }

  return {
    createWahldaten,
    prepareWahldaten,
    prepareStimmabgabevermerke,
    createStimmabgabevermerke,
    createVermerk,
    prepareVermerk,
    prepareStimmzettel,
    createStimmzettel,
    createStimmabgabevermerkeDTO,
    prepareWahldatenDTO,
    prepareVermerkDTO,
    prepareStimmzettelDTO,
    prepareStimmabgabevermerkeDTO,
  };
}
