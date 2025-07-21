import type {
  FarbeDTO,
  WahlDTO,
} from "@/api/wls-clients/generated-basisdaten-api";
import type { Farbe } from "@/types/wahl/Farbe.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { WahlDTOWahlartEnum } from "@/api/wls-clients/generated-basisdaten-api";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const {
  generateRandomDateTimeAsString,
  generateRandomString,
  generateRandomNumberInRange,
} = useCommonTestDataFactory();

const wahlWahlartEnumValues = Object.values(WahlWahlartEnum);
const wahlDTOWahlartEnumValues = Object.values(WahlDTOWahlartEnum);

export function useWahlTestDataFactory() {
  function _createRandomFarbe(): Farbe {
    return {
      r: generateRandomNumberInRange(0, 255),
      g: generateRandomNumberInRange(0, 255),
      b: generateRandomNumberInRange(0, 255),
    };
  }

  function _createRandomFarbeDTO(): FarbeDTO {
    return {
      r: generateRandomNumberInRange(0, 255),
      g: generateRandomNumberInRange(0, 255),
      b: generateRandomNumberInRange(0, 255),
    };
  }

  function _createRandomBeanstandeteWahlbriefe(
    length?: number
  ): ZurueckweisungsgrundEnum[] {
    const counter = length ?? 3;
    const enumValues = Object.values(ZurueckweisungsgrundEnum);
    const result: ZurueckweisungsgrundEnum[] = [];

    for (let i = 0; i < counter; i++) {
      const randomIndex = Math.floor(Math.random() * enumValues.length);
      result.push(enumValues[randomIndex]);
    }

    return result;
  }

  function createWahlDTO(): WahlDTO {
    return {
      wahlID: generateRandomString(10),
      name: generateRandomString(20),
      reihenfolge: generateRandomNumberInRange(0, 100),
      waehlerverzeichnisNummer: generateRandomNumberInRange(0, 100),
      wahltag: generateRandomDateTimeAsString(),
      wahlart:
        wahlDTOWahlartEnumValues[
          generateRandomNumberInRange(0, wahlDTOWahlartEnumValues.length - 1)
        ],
      farbe: _createRandomFarbeDTO(),
      nummer: generateRandomString(5),
    };
  }

  function createWahl(): Wahl {
    return {
      wahlID: generateRandomString(10),
      name: generateRandomString(20),
      reihenfolge: generateRandomNumberInRange(0, 100),
      waehlerverzeichnisNummer: generateRandomNumberInRange(0, 100),
      wahltag: generateRandomDateTimeAsString(),
      wahlart:
        wahlWahlartEnumValues[
          generateRandomNumberInRange(0, wahlWahlartEnumValues.length - 1)
        ],
      farbe: _createRandomFarbe(), // Randomly generated Farbe
      nummer: generateRandomString(5),
      beanstandeteWahlbriefe: _createRandomBeanstandeteWahlbriefe(),
    };
  }

  function prepareWahlDTO(): Builder<WahlDTO> {
    return proxyBuilder<WahlDTO>(createWahlDTO());
  }

  function prepareWahl(): Builder<Wahl> {
    return proxyBuilder<Wahl>(createWahl());
  }

  return {
    createWahlDTO,
    createWahl,
    prepareWahlDTO,
    prepareWahl,
  };
}
