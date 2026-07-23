import type {
  StimmzettelKandidatDTO,
  StimmzettelOfTeamDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Kandidat } from "@/types/dse/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomNumber, generateRandomBoolean, generateRandomString } =
  useCommonTestDataFactory();

export function useStimmzettelTestDataFactory() {
  function createStimmzettelKandidatDTO(): StimmzettelKandidatDTO {
    return {
      kandidatId: generateRandomString(10),
      isDiscarded: generateRandomBoolean(),
      votesByVoter: generateRandomNumber(2),
    };
  }

  function createStimmzettelKandidat(): Kandidat {
    return {
      kandidatId: generateRandomString(10),
      isDiscarded: generateRandomBoolean(),
      votesByVoter: generateRandomNumber(2),
    };
  }

  function createStimmzettelOfTeamDTO(): StimmzettelOfTeamDTO {
    return {
      stimmzettelkennung: generateRandomNumber(6),
      kandidaten: [
        createStimmzettelKandidatDTO(),
        createStimmzettelKandidatDTO(),
      ],
      selectedWahlvorschlaegeOrdnungszahlen: [
        generateRandomNumber(2),
        generateRandomNumber(2),
      ],
    };
  }

  function createStimmzettel(): Stimmzettel {
    return {
      stimmzettelkennung: generateRandomNumber(6),
      kandidaten: [createStimmzettelKandidat(), createStimmzettelKandidat()],
      selectedWahlvorschlaegeOrdnungszahlen: [
        generateRandomNumber(2),
        generateRandomNumber(2),
      ],
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

  return {
    createStimmzettelOfTeamDTO,
    createStimmzettel,
    createStimmzettelKandidatDTO,
    createStimmzettelKandidat,
    prepareStimmzettelOfTeamDTO,
    prepareStimmzettel,
    prepareStimmzettelKandidat,
  };
}
