import type {
  BeschlussfassungDTO,
  KandidatDTO,
  KandidatIdDTO,
  StimmzettelOfTeamDTO,
  SystemBeschlussgrundDTO,
  WahlvorschlagDTO,
  WahlvorstandBeschlussgrundDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Beschlussfassung } from "@/types/dse/beschlussfassung/Beschlussfassung.ts";
import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";
import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import {
  StimmzettelOfTeamDTOGueltigkeitEnum,
  SystemBeschlussgrundDTOReasonEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const {
  generateRandomNumber,
  generateRandomBoolean,
  generateRandomString,
  getRandomItem,
} = useCommonTestDataFactory();

export function useStimmzettelTestDataFactory() {
  function createStimmzettel(): Stimmzettel {
    return {
      gueltigkeit: getRandomItem(Object.values(StimmzettelGueltigkeitEnum)),
      wahlvorschlaege: [
        createStimmzettelWahlvorschlag(),
        createStimmzettelWahlvorschlag(),
        createStimmzettelWahlvorschlag(),
      ],
      beschlussfassung: createStimmzettelBeschlussfassung(),
      systemBeschlussvorschlag: [],
      wahlvorstandBeschlussvorschlag: [],
      invalideVotes: generateRandomNumber(2),
    };
  }

  function createStimmzettelKandidatDTO(): KandidatDTO {
    return {
      id: createStimmzettelKandidatIdDTO(),
      discarded: generateRandomBoolean(),
      votesByWahlvorschlag: generateRandomNumber(2),
      invalidVotes: generateRandomNumber(2),
      votesByVoter: generateRandomNumber(2),
    };
  }

  function createStimmzettelKandidat(): Kandidat {
    const wahlvorschlag = _createStimmzettelWahlvorschlagWithoutKandidaten();
    const result = {
      reststimmen: generateRandomNumber(2),
      ungueltigeStimmen: generateRandomNumber(2),
      ordnungszahl: generateRandomNumber(2),
      listenposition: generateRandomNumber(2),
      name: generateRandomString(10),
      einzelstimmen: generateRandomNumber(2),
      owningWahlvorschlag: wahlvorschlag,
      durchgestrichen: generateRandomBoolean(),
      kandidatId: generateRandomString(10),
      nennung: generateRandomNumber(1),
    };
    wahlvorschlag.kandidaten = [result];

    return result;
  }

  function createStimmzettelKandidatOfWahlvorschlag(
    owningWahlvorschlag: Wahlvorschlag
  ): Kandidat {
    return {
      reststimmen: generateRandomNumber(2),
      ungueltigeStimmen: generateRandomNumber(2),
      ordnungszahl: generateRandomNumber(2),
      listenposition: generateRandomNumber(2),
      name: generateRandomString(10),
      einzelstimmen: generateRandomNumber(2),
      owningWahlvorschlag: owningWahlvorschlag,
      durchgestrichen: generateRandomBoolean(),
      kandidatId: generateRandomString(10),
      nennung: generateRandomNumber(1),
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

  function createStimmzettelWahlvorstandBeschlussgrundDTO(): WahlvorstandBeschlussgrundDTO {
    return {
      text: generateRandomString(20),
    };
  }

  function createStimmzettelSystemBeschlussgrund(): SystemBeschlussgrund {
    return {
      reason: getRandomItem(Object.values(SystemBeschlussgrundReasonEnum)),
    };
  }

  function createStimmzettelSystemBeschlussgrundDto(): SystemBeschlussgrundDTO {
    return {
      reason: getRandomItem(Object.values(SystemBeschlussgrundDTOReasonEnum)),
    };
  }

  function createStimmzettelWahlvorstandBeschlussgrund(): WahlvorstandBeschlussgrund {
    return {
      text: generateRandomString(20),
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
    const result = _createStimmzettelWahlvorschlagWithoutKandidaten();
    result.kandidaten = [
      createStimmzettelKandidatOfWahlvorschlag(result),
      createStimmzettelKandidatOfWahlvorschlag(result),
      createStimmzettelKandidatOfWahlvorschlag(result),
    ];
    return result;
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

  function prepareStimmzettel(): Builder<Stimmzettel> {
    return proxyBuilder<Stimmzettel>(createStimmzettel());
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

  function prepareStimmzettelKandidat(): Builder<Kandidat> {
    return proxyBuilder<Kandidat>(createStimmzettelKandidat());
  }

  function prepareStimmzettelKandidatOfWahlvorschlag(
    owningWahlvorschlag: Wahlvorschlag
  ): Builder<Kandidat> {
    return proxyBuilder<Kandidat>(
      createStimmzettelKandidatOfWahlvorschlag(owningWahlvorschlag)
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

  function prepareStimmzettelWahlvorschlag(): Builder<Wahlvorschlag> {
    return proxyBuilder<Wahlvorschlag>(createStimmzettelWahlvorschlag());
  }

  function _createStimmzettelWahlvorschlagWithoutKandidaten(): Wahlvorschlag {
    return {
      ordnungszahl: generateRandomNumber(2),
      kandidaten: [],
      selected: generateRandomBoolean(),
      ungueltigeStimmen: generateRandomNumber(2),
      gueltigeStimmen: generateRandomNumber(2),
      erhaeltStimmen: generateRandomBoolean(),
      kurzname: generateRandomString(2),
      wahlvorschlagID: generateRandomString(10),
    };
  }

  return {
    createStimmzettel,
    createStimmzettelOfTeamDTO,
    createStimmzettelKandidat,
    createStimmzettelKandidatOfWahlvorschlag,
    createStimmzettelKandidatDTO,
    createStimmzettelWahlvorschlag,
    prepareStimmzettel,
    prepareStimmzettelOfTeamDTO,
    prepareStimmzettelBeschlussfassungDTO,
    prepareStimmzettelBeschlussgrundDTO,
    prepareStimmzettelKandidat,
    prepareStimmzettelKandidatOfWahlvorschlag,
    prepareStimmzettelKandidatDTO,
    prepareStimmzettelKandidatIdDTO,
    prepareStimmzettelWahlvorschlag,
    prepareStimmzettelWahlvorschlagDTO,
    createStimmzettelSystemBeschlussgrund,
    createStimmzettelWahlvorstandBeschlussgrund,
  };
}
