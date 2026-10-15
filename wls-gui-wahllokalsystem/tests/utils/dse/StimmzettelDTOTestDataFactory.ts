import type {
  BeschlussfassungDTO,
  KandidatDTO,
  KandidatIdDTO,
  StimmzettelOfTeamDTO,
  SystemBeschlussgrundDTO,
  WahlvorschlagDTO,
  WahlvorstandBeschlussgrundDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import {
  StimmzettelOfTeamDTOGueltigkeitEnum,
  SystemBeschlussgrundDTOReasonEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";

const {
  generateRandomNumber,
  generateRandomBoolean,
  generateRandomString,
  getRandomItem,
} = useCommonTestDataFactory();

export function useStimmzettelDTOTestDataFactory() {
  function createStimmzettelKandidatDTO(): KandidatDTO {
    return {
      id: createStimmzettelKandidatIdDTO(),
      discarded: generateRandomBoolean(),
      votesByWahlvorschlag: generateRandomNumber(2),
      invalidVotes: generateRandomNumber(2),
      votesByVoter: generateRandomNumber(2),
    };
  }

  function createStimmzettelBeschlussfassungDTO(): BeschlussfassungDTO {
    return {
      text: generateRandomString(20),
      pro: generateRandomNumber(2),
      contra: generateRandomNumber(2),
    };
  }

  function createStimmzettelWahlvorstandBeschlussgrundDTO(): WahlvorstandBeschlussgrundDTO {
    return {
      text: generateRandomString(20),
    };
  }

  function createStimmzettelSystemBeschlussgrundDto(): SystemBeschlussgrundDTO {
    return {
      reason: getRandomItem(Object.values(SystemBeschlussgrundDTOReasonEnum)),
    };
  }

  function createStimmzettelWahlvorschlagDTO(): WahlvorschlagDTO {
    return {
      wahlvorschlagID: generateRandomString(10),
      kandidaten: [
        createStimmzettelKandidatDTO(),
        createStimmzettelKandidatDTO(),
        createStimmzettelKandidatDTO(),
      ],
      selected: generateRandomBoolean(),
    };
  }

  function createStimmzettelKandidatIdDTO(): KandidatIdDTO {
    return {
      kandidatID: generateRandomString(10),
      nennungsNummer: generateRandomNumber(2),
    };
  }

  function createStimmzettelOfTeamDTO(): StimmzettelOfTeamDTO {
    return {
      stimmzettelkennung: generateRandomNumber(6),
      invalideVotes: generateRandomNumber(6),
      gueltigkeit: getRandomItem(
        Object.values(StimmzettelOfTeamDTOGueltigkeitEnum)
      ),
      beschlussfassung: createStimmzettelBeschlussfassungDTO(),
      systemBeschlussvorschlag: [
        createStimmzettelSystemBeschlussgrundDto(),
        createStimmzettelSystemBeschlussgrundDto(),
        createStimmzettelSystemBeschlussgrundDto(),
      ],
      wahlvorstandBeschlussvorschlag: [
        createStimmzettelWahlvorstandBeschlussgrundDTO(),
        createStimmzettelWahlvorstandBeschlussgrundDTO(),
        createStimmzettelWahlvorstandBeschlussgrundDTO(),
      ],
      wahlvorschlaege: [
        createStimmzettelWahlvorschlagDTO(),
        createStimmzettelWahlvorschlagDTO(),
        createStimmzettelWahlvorschlagDTO(),
      ],
    };
  }

  function prepareStimmzettelOfTeamDTO(): Builder<StimmzettelOfTeamDTO> {
    return proxyBuilder<StimmzettelOfTeamDTO>(createStimmzettelOfTeamDTO());
  }

  function prepareStimmzettelBeschlussfassungDTO(): Builder<BeschlussfassungDTO> {
    return proxyBuilder<BeschlussfassungDTO>(
      createStimmzettelBeschlussfassungDTO()
    );
  }

  function prepareStimmzettelBeschlussgrundDTO(): Builder<WahlvorstandBeschlussgrundDTO> {
    return proxyBuilder<WahlvorstandBeschlussgrundDTO>(
      createStimmzettelWahlvorstandBeschlussgrundDTO()
    );
  }

  function prepareStimmzettelKandidatDTO(): Builder<KandidatDTO> {
    return proxyBuilder<KandidatDTO>(createStimmzettelKandidatDTO());
  }

  function prepareStimmzettelKandidatIdDTO(): Builder<KandidatIdDTO> {
    return proxyBuilder<KandidatIdDTO>(createStimmzettelKandidatIdDTO());
  }

  function prepareStimmzettelWahlvorschlagDTO(): Builder<WahlvorschlagDTO> {
    return proxyBuilder<WahlvorschlagDTO>(createStimmzettelWahlvorschlagDTO());
  }

  return {
    createStimmzettelOfTeamDTO,
    createStimmzettelKandidatDTO,
    prepareStimmzettelOfTeamDTO,
    prepareStimmzettelBeschlussfassungDTO,
    prepareStimmzettelBeschlussgrundDTO,
    prepareStimmzettelKandidatDTO,
    prepareStimmzettelKandidatIdDTO,
    prepareStimmzettelWahlvorschlagDTO,
  };
}
