import type {
  FarbeDTO,
  WahlDTO,
} from "@/api/wls-clients/generated-basisdaten-api";
import type { Farbe } from "@/types/wahl/Farbe.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { useBeanstandeteWahlbriefeTestDataFactory } from "@tests/utils/briefwahl/BeanstandeteWahlbriefeTestDataFactory.ts";
import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelumschlaegeTestDataFactory } from "@tests/utils/ergebnisermittlung/StimmzettelumschlaegeTestDataFactory.ts";

import { WahlDTOWahlartEnum } from "@/api/wls-clients/generated-basisdaten-api";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const {
  generateRandomDateTimeAsString,
  generateRandomString,
  generateRandomNumberInRange,
  getRandomItem,
} = useCommonTestDataFactory();
const { createRandomBeanstandeteWahlbriefeValues } =
  useBeanstandeteWahlbriefeTestDataFactory();

const { createStimmzettelumschlaege } =
  useStimmzettelumschlaegeTestDataFactory();

export function useWahlTestDataFactory() {
  function createWahlDTO(): WahlDTO {
    return {
      wahlID: generateRandomString(10),
      name: generateRandomString(20),
      reihenfolge: generateRandomNumberInRange(0, 100),
      waehlerverzeichnisNummer: generateRandomNumberInRange(0, 100),
      wahltag: generateRandomDateTimeAsString(),
      wahlart: getRandomItem(Object.values(WahlDTOWahlartEnum)),
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
      wahlart: getRandomItem(Object.values(WahlWahlartEnum)),
      farbe: _createRandomFarbe(), // Randomly generated Farbe
      nummer: generateRandomString(5),
      beanstandeteWahlbriefe: createRandomBeanstandeteWahlbriefeValues(),
      stimmzettelumschlaege: createStimmzettelumschlaege(),
    };
  }

  function prepareWahlDTO(): Builder<WahlDTO> {
    return proxyBuilder<WahlDTO>(createWahlDTO());
  }

  function prepareWahl(): Builder<Wahl> {
    return proxyBuilder<Wahl>(createWahl());
  }

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

  return {
    createWahlDTO,
    createWahl,
    prepareWahlDTO,
    prepareWahl,
  };
}
