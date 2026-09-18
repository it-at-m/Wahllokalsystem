import type { Beschlussfassung } from "@/types/dse/beschlussfassung/Beschlussfassung.ts";
import type { DseKandidat } from "@/types/dse/stimmzettelerfassung/DseKandidat.ts";
import type { DseStimmzettel } from "@/types/dse/stimmzettelerfassung/DseStimmzettel.ts";
import type { DseWahlvorschlag } from "@/types/dse/stimmzettelerfassung/DseWahlvorschlag.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const {
  generateRandomNumber,
  generateRandomBoolean,
  generateRandomString,
  getRandomItem,
} = useCommonTestDataFactory();

export function useStimmzettelTestDataFactory() {
  function createDseStimmzettel(): DseStimmzettel {
    return {
      gueltigkeit: getRandomItem(Object.values(StimmzettelGueltigkeitEnum)),
      wahlvorschlaege: [
        createDseWahlvorschlag(),
        createDseWahlvorschlag(),
        createDseWahlvorschlag(),
      ],
      beschlussfassung: createStimmzettelBeschlussfassung(),
      systemBeschlussvorschlag: [],
      wahlvorstandBeschlussvorschlag: [],
      invalideVotes: generateRandomNumber(2),
    };
  }

  function createDseKandidat(): DseKandidat {
    const wahlvorschlag = _createDseWahlvorschlagWithoutKandidaten();
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

  function createDseKandidatOfDseWahlvorschlag(
    owningWahlvorschlag: DseWahlvorschlag
  ): DseKandidat {
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

  function createStimmzettelBeschlussfassung(): Beschlussfassung {
    return {
      text: generateRandomString(20),
      pro: generateRandomNumber(2),
      contra: generateRandomNumber(2),
    };
  }

  function createDseWahlvorschlag(): DseWahlvorschlag {
    const result = _createDseWahlvorschlagWithoutKandidaten();
    result.kandidaten = [
      createDseKandidatOfDseWahlvorschlag(result),
      createDseKandidatOfDseWahlvorschlag(result),
      createDseKandidatOfDseWahlvorschlag(result),
    ];
    return result;
  }

  function prepareDseStimmzettel(): Builder<DseStimmzettel> {
    return proxyBuilder<DseStimmzettel>(createDseStimmzettel());
  }

  function prepareDseKandidat(): Builder<DseKandidat> {
    return proxyBuilder<DseKandidat>(createDseKandidat());
  }

  function prepareDseKandidatOfDseWahlvorschlag(
    owningWahlvorschlag: DseWahlvorschlag
  ): Builder<DseKandidat> {
    return proxyBuilder<DseKandidat>(
      createDseKandidatOfDseWahlvorschlag(owningWahlvorschlag)
    );
  }

  function prepareDseWahlvorschlag(): Builder<DseWahlvorschlag> {
    return proxyBuilder<DseWahlvorschlag>(createDseWahlvorschlag());
  }

  function _createDseWahlvorschlagWithoutKandidaten(): DseWahlvorschlag {
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
    createDseStimmzettel,
    createDseKandidat,
    createDseKandidatOfDseWahlvorschlag,
    createDseWahlvorschlag,
    prepareDseStimmzettel,
    prepareDseKandidat,
    prepareDseKandidatOfDseWahlvorschlag,
    prepareDseWahlvorschlag,
  };
}
