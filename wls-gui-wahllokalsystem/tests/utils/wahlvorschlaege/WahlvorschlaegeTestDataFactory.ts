import type {
  KandidatDTO,
  WahlvorschlaegeDTO,
  WahlvorschlagDTO,
} from "@/api/wls-clients/generated-basisdaten-api";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomString, generateRandomNumber, generateRandomBoolean } =
  useCommonTestDataFactory();

export function useWahlvorschlaegeTestDataFactory() {
  function createWahlvorschlaegeDto(): WahlvorschlaegeDTO {
    return {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(10),
      stimmzettelgebietID: generateRandomString(15),
      wahlvorschlaege: [_createWahlvorschlagDto()],
    };
  }

  function createWahlvorschlaege(): Wahlvorschlaege {
    return {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(10),
      stimmzettelgebietID: generateRandomString(15),
      wahlvorschlaege: [createWahlvorschlag()],
    };
  }

  function createWahlvorschlag(): Wahlvorschlag {
    return {
      identifikator: generateRandomString(5),
      ordnungszahl: generateRandomNumber(1),
      kurzname: generateRandomString(3),
      erhaeltStimmen: generateRandomBoolean(),
      kandidaten: [createKandidat()],
    };
  }

  function prepareKandidat() {
    return proxyBuilder<Kandidat>(createKandidat());
  }

  function prepareWahlvorschlaege(): Builder<Wahlvorschlaege> {
    return proxyBuilder<Wahlvorschlaege>(createWahlvorschlaege());
  }

  function prepareWahlvorschlaegeDto(): Builder<WahlvorschlaegeDTO> {
    return proxyBuilder<WahlvorschlaegeDTO>(createWahlvorschlaegeDto());
  }

  function prepareWahlvorschlag(): Builder<Wahlvorschlag> {
    return proxyBuilder<Wahlvorschlag>(createWahlvorschlag());
  }

  function _createWahlvorschlagDto(): WahlvorschlagDTO {
    return {
      identifikator: generateRandomString(5),
      ordnungszahl: generateRandomNumber(1),
      kurzname: generateRandomString(3),
      erhaeltStimmen: generateRandomBoolean(),
      kandidaten: [_createKandidatDto()],
    };
  }

  function _createKandidatDto(): KandidatDTO {
    return {
      identifikator: generateRandomString(5),
      name: generateRandomString(7),
      listenposition: generateRandomNumber(1),
      direktkandidat: generateRandomBoolean(),
      tabellenSpalteInNiederschrift: generateRandomNumber(2),
      einzelbewerber: generateRandomBoolean(),
    };
  }

  function createKandidat(): Kandidat {
    return {
      identifikator: generateRandomString(5),
      name: generateRandomString(7),
      listenposition: generateRandomNumber(1),
      direktkandidat: generateRandomBoolean(),
      tabellenSpalteInNiederschrift: generateRandomNumber(2),
      einzelbewerber: generateRandomBoolean(),
    };
  }

  return {
    createKandidat,
    createWahlvorschlag,
    createWahlvorschlaege,
    createWahlvorschlaegeDto,
    prepareKandidat,
    prepareWahlvorschlag,
    prepareWahlvorschlaege,
    prepareWahlvorschlaegeDto,
  };
}
