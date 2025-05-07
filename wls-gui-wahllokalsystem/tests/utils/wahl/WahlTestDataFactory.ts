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
import { WahlWahlartEnum } from "@/types/wahl/wahlWahlartEnum.ts";

const {
  generateRandomDateTimeAsString,
  generateRandomString,
  generateRandomNumber,
} = useCommonTestDataFactory();

const wahlWahlartEnumValues = Object.values(WahlWahlartEnum);
const wahlDTOWahlartEnumValues = Object.values(WahlDTOWahlartEnum);

export function useWahlTestDataFactory() {
  function createRandomFarbe(): Farbe {
    return {
      r: generateRandomNumber(255),
      g: generateRandomNumber(255),
      b: generateRandomNumber(255),
    };
  }

  function createRandomFarbeDTO(): FarbeDTO {
    return {
      r: generateRandomNumber(255),
      g: generateRandomNumber(255),
      b: generateRandomNumber(255),
    };
  }

  function createWahlDTO(): WahlDTO {
    return {
      wahlID: generateRandomString(10),
      name: generateRandomString(20),
      reihenfolge: generateRandomNumber(100),
      waehlerverzeichnisnummer: generateRandomNumber(100),
      wahltag: generateRandomDateTimeAsString(),
      wahlart:
        wahlDTOWahlartEnumValues[
          generateRandomNumber(wahlDTOWahlartEnumValues.length)
        ],
      farbe: createRandomFarbeDTO(),
      nummer: generateRandomString(5),
    };
  }

  function createWahl(): Wahl {
    return {
      wahlID: generateRandomString(10),
      name: generateRandomString(20),
      reihenfolge: generateRandomNumber(100),
      waehlerverzeichnisnummer: generateRandomNumber(100),
      wahltag: generateRandomDateTimeAsString(),
      wahlart:
        wahlWahlartEnumValues[
          generateRandomNumber(wahlWahlartEnumValues.length)
        ],
      farbe: createRandomFarbe(), // Randomly generated Farbe
      nummer: generateRandomString(5),
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
