import type {
  BeschlussfassungDTO,
  KandidatDTO,
  KandidatIdDTO,
  StimmzettelOfTeamDTO,
  WahlvorschlagDTO,
  WahlvorstandBeschlussgrundDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Beschlussfassung as PersistedBeschlussfassung } from "@/types/dse/persistedStimmzettel/Beschlussfassung.ts";
import type { Beschlussgrund as PersistedBeschlussgrund } from "@/types/dse/persistedStimmzettel/Beschlussgrund.ts";
import type { Kandidat as PersistedKandidat } from "@/types/dse/persistedStimmzettel/Kandidat.ts";
import type { Stimmzettel as PersistedStimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Wahlvorschlag as PersistedWahlvorschlag } from "@/types/dse/persistedStimmzettel/Wahlvorschlag.ts";
import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { StimmzettelOfTeamDTOGueltigkeitEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

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

  function createStimmzettelBeschlussfassungDTO(): BeschlussfassungDTO {
    return {
      text: generateRandomString(20),
      pro: generateRandomNumber(2),
      contra: generateRandomNumber(2),
    };
  }

  function createStimmzettelBeschlussfassung(): PersistedBeschlussfassung {
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

  function createStimmzettelBeschlussgrund(): PersistedBeschlussgrund {
    return {
      text: generateRandomString(20),
    };
  }

  function createPersistedStimmzettelKandidat(): PersistedKandidat {
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

  function createPersistedStimmzettelWahlvorschlag(): PersistedWahlvorschlag {
    return {
      kandidaten: [
        createPersistedStimmzettelKandidat(),
        createPersistedStimmzettelKandidat(),
        createPersistedStimmzettelKandidat(),
      ],
      selected: generateRandomBoolean(),
      wahlvorschlagID: generateRandomString(10),
    };
  }

  function createStimmzettelWahlvorschlag(): Wahlvorschlag {
    const result = _createStimmzettelWahlvorschlagWithoutKandidaten();
    result.kandidaten = [
      createStimmzettelKandidat(),
      createStimmzettelKandidat(),
      createStimmzettelKandidat(),
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

  function createPersistedStimmzettel(): PersistedStimmzettel {
    return {
      stimmzettelkennung: generateRandomNumber(6),
      wahlvorschlaege: [
        createPersistedStimmzettelWahlvorschlag(),
        createPersistedStimmzettelWahlvorschlag(),
        createPersistedStimmzettelWahlvorschlag(),
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

  function preparePersistedStimmzettel(): Builder<PersistedStimmzettel> {
    return proxyBuilder<PersistedStimmzettel>(createPersistedStimmzettel());
  }

  function preparePersistedStimmzettelBeschlussfassung(): Builder<PersistedBeschlussfassung> {
    return proxyBuilder<PersistedBeschlussfassung>(
      createStimmzettelBeschlussfassung()
    );
  }

  function prepareStimmzettelBeschlussfassungDTO(): Builder<BeschlussfassungDTO> {
    return proxyBuilder<BeschlussfassungDTO>(
      createStimmzettelBeschlussfassungDTO()
    );
  }

  function preparePersistedStimmzettelBeschlussgrund(): Builder<PersistedBeschlussgrund> {
    return proxyBuilder<PersistedBeschlussgrund>(
      createStimmzettelBeschlussgrund()
    );
  }

  function prepareStimmzettelBeschlussgrundDTO(): Builder<WahlvorstandBeschlussgrundDTO> {
    return proxyBuilder<WahlvorstandBeschlussgrundDTO>(
      createStimmzettelWahlvorstandBeschlussgrundDTO()
    );
  }

  function preparePersistedStimmzettelKandidat(): Builder<PersistedKandidat> {
    return proxyBuilder<PersistedKandidat>(
      createPersistedStimmzettelKandidat()
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

  function preparePersistedStimmzettelWahlvorschlag(): Builder<PersistedWahlvorschlag> {
    return proxyBuilder<PersistedWahlvorschlag>(
      createPersistedStimmzettelWahlvorschlag()
    );
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
    createPersistedStimmzettelKandidat,
    createPersistedStimmzettel,
    createStimmzettelOfTeamDTO,
    createStimmzettelKandidatDTO,
    createStimmzettelWahlvorschlag,
    preparePersistedStimmzettel,
    preparePersistedStimmzettelBeschlussfassung,
    preparePersistedStimmzettelBeschlussgrund,
    preparePersistedStimmzettelKandidat,
    preparePersistedStimmzettelWahlvorschlag,
    prepareStimmzettelOfTeamDTO,
    prepareStimmzettelBeschlussfassungDTO,
    prepareStimmzettelBeschlussgrundDTO,
    prepareStimmzettelKandidatDTO,
    prepareStimmzettelKandidatIdDTO,
    prepareStimmzettelWahlvorschlagDTO,
  };
}
