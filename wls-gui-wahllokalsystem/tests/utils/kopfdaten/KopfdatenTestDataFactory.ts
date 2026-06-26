import type { KopfdatenDTO } from "@/api/wls-clients/generated-basisdaten-api";
import type { Kopfdaten } from "@/types/kopfdaten/kopfdaten.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { KopfdatenDTOStimmzettelgebietsartEnum } from "@/api/wls-clients/generated-basisdaten-api";
import { KopfdatenStimmzettelgebietsartEnum } from "@/types/kopfdaten/KopfdatenStimmzettelgebietsartEnum.ts";

const { generateRandomString, generateRandomNumber } = useCommonTestDataFactory();

export function useKopfdatenTestDataFactory() {
  function createKopfdatenDto(): KopfdatenDTO {
    return {
      gemeinde: generateRandomString(10),
      stimmzettelgebietsart: KopfdatenDTOStimmzettelgebietsartEnum.Sb,
      stimmzettelgebietsname: generateRandomString(10),
      stimmzettelgebietsnummer: generateRandomString(20),
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(20),
      wahlbezirknummer: generateRandomString(10),
      wahlname: generateRandomString(10),
      maximalErlaubteStimmenProWaehler: generateRandomNumber(2),
    };
  }

  function createKopfdaten(): Kopfdaten {
    return {
      gemeinde: generateRandomString(10),
      stimmzettelgebietsart: KopfdatenStimmzettelgebietsartEnum.Sb,
      stimmzettelgebietsname: generateRandomString(10),
      stimmzettelgebietsnummer: generateRandomString(20),
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(20),
      wahlbezirknummer: generateRandomString(10),
      wahlname: generateRandomString(10),
      maximalErlaubteStimmenProWaehler: generateRandomNumber(2),
    };
  }

  function prepareKopfdaten(): Builder<Kopfdaten> {
    return proxyBuilder<Kopfdaten>(createKopfdaten());
  }

  function prepareKopfdatenDto(): Builder<KopfdatenDTO> {
    return proxyBuilder<KopfdatenDTO>(createKopfdatenDto());
  }

  return {
    createKopfdaten,
    createKopfdatenDto,
    prepareKopfdaten,
    prepareKopfdatenDto,
  };
}
