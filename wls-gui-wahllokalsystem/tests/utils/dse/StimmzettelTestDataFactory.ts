import type {
  BeschlussfassungDTO,
  BeschlussgrundDTO,
  KandidatDTO,
  KandidatIdDTO,
  StimmzettelOfTeamDTO,
  WahlvorschlagDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Beschlussfassung } from "@/types/dse/Beschlussfassung.ts";
import type { Beschlussgrund } from "@/types/dse/Beschlussgrund.ts";
import type { Kandidat } from "@/types/dse/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/Wahlvorschlag.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { StimmzettelOfTeamDTOGueltigkeitEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/StimmzettelGueltigkeitEnum.ts";

const {
  generateRandomNumber,
  generateRandomBoolean,
  generateRandomString,
  getRandomItem,
} = useCommonTestDataFactory();

export function useStimmzettelTestDataFactory() {
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

  function createStimmzettelBeschlussfassung(): Beschlussfassung {
    return {
      text: generateRandomString(20),
      pro: generateRandomNumber(2),
      contra: generateRandomNumber(2),
    };
  }

  function createStimmzettelBeschlussgrundDTO(): BeschlussgrundDTO {
    return {
      text: generateRandomString(20),
    };
  }

  function createStimmzettelBeschlussgrund(): Beschlussgrund {
    return {
      text: generateRandomString(20),
    };
  }

  function createStimmzettelKandidat(): Kandidat {
    return {
      kandidatId: generateRandomString(10),
      nennung: generateRandomNumber(1),
      isDiscarded: generateRandomBoolean(),
      votesByVoter: generateRandomNumber(2),
      invalidVotes: generateRandomNumber(2),
      votesByWahlvorschlag: generateRandomNumber(2),
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

  function createStimmzettelWahlvorschlag(): Wahlvorschlag {
    return {
      kandidaten: [
        createStimmzettelKandidat(),
        createStimmzettelKandidat(),
        createStimmzettelKandidat(),
      ],
      selected: generateRandomBoolean(),
      wahlvorschlagID: generateRandomString(10),
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
      beschlussvorschlag: [
        createStimmzettelBeschlussgrundDTO(),
        createStimmzettelBeschlussgrundDTO(),
        createStimmzettelBeschlussgrundDTO(),
      ],
      wahlvorschlaege: [
        createStimmzettelWahlvorschlagDTO(),
        createStimmzettelWahlvorschlagDTO(),
        createStimmzettelWahlvorschlagDTO(),
      ],
    };
  }

  function createStimmzettel(): Stimmzettel {
    return {
      stimmzettelkennung: generateRandomNumber(6),
      wahlvorschlaege: [
        createStimmzettelWahlvorschlag(),
        createStimmzettelWahlvorschlag(),
        createStimmzettelWahlvorschlag(),
      ],
      beschlussvorschlag: [
        createStimmzettelBeschlussgrund(),
        createStimmzettelBeschlussgrund(),
        createStimmzettelBeschlussgrund(),
      ],
      beschlussfassung: createStimmzettelBeschlussfassung(),
      invalideVotes: generateRandomNumber(2),
      gueltigkeit: getRandomItem(Object.values(StimmzettelGueltigkeitEnum)),
    };
  }

  function prepareStimmzettelOfTeamDTO(): Builder<StimmzettelOfTeamDTO> {
    return proxyBuilder<StimmzettelOfTeamDTO>(createStimmzettelOfTeamDTO());
  }

  function prepareStimmzettel(): Builder<Stimmzettel> {
    return proxyBuilder<Stimmzettel>(createStimmzettel());
  }

  function prepareStimmzettelKandidat(): Builder<Kandidat> {
    return proxyBuilder<Kandidat>(createStimmzettelKandidat());
  }

  function prepareStimmzettelKandidatDTO(): Builder<KandidatDTO> {
    return proxyBuilder<KandidatDTO>(createStimmzettelKandidatDTO());
  }

  return {
    createStimmzettelOfTeamDTO,
    createStimmzettel,
    createStimmzettelKandidatDTO,
    createStimmzettelKandidat,
    prepareStimmzettelOfTeamDTO,
    prepareStimmzettel,
    prepareStimmzettelKandidat,
    prepareStimmzettelKandidatDTO,
  };
}
