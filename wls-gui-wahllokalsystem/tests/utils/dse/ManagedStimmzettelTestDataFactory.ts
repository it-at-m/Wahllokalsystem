import type { DseKandidat } from "@/types/dse/stimmzettelerfassung/DseKandidat.ts";
import type { DseStimmzettel } from "@/types/dse/stimmzettelerfassung/DseStimmzettel.ts";
import type { DseWahlvorschlag } from "@/types/dse/stimmzettelerfassung/DseWahlvorschlag.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

export function useManagedStimmzettelTestDataFactory() {
  const {
    generateRandomNumber,
    generateRandomBoolean,
    generateRandomString,
    getRandomItem,
  } = useCommonTestDataFactory();

  function createManagedStimmzettelKandidat(): DseKandidat {
    const wahlvorschlag = createManagedStimmzettelWahlvorschlag();
    wahlvorschlag.kandidaten = [wahlvorschlag.kandidaten[0]];
    return wahlvorschlag.kandidaten[0];
  }

  function createManagedStimmzettelWahlvorschlag(): DseWahlvorschlag {
    const result: DseWahlvorschlag = {
      wahlvorschlagID: generateRandomString(10),
      ordnungszahl: generateRandomNumber(2),
      selected: generateRandomBoolean(),
      kandidaten: [],
      kurzname: generateRandomString(10),
      erhaeltStimmen: generateRandomBoolean(),
      gueltigeStimmen: generateRandomNumber(2),
      ungueltigeStimmen: generateRandomNumber(2),
    };
    result.kandidaten = [
      _createManagedStimmzettelKandidatForWahlvorschlag(result),
      _createManagedStimmzettelKandidatForWahlvorschlag(result),
      _createManagedStimmzettelKandidatForWahlvorschlag(result),
    ];
    return result;
  }

  function createManagedStimmzettelStimmzettel(): DseStimmzettel {
    return {
      wahlvorschlaege: [
        createManagedStimmzettelWahlvorschlag(),
        createManagedStimmzettelWahlvorschlag(),
        createManagedStimmzettelWahlvorschlag(),
      ],
      invalideVotes: generateRandomNumber(2),
      gueltigkeit: getRandomItem(Object.values(StimmzettelGueltigkeitEnum)),
      wahlvorstandBeschlussvorschlag: [],
      systemBeschlussvorschlag: [],
      beschlussfassung: null,
    };
  }

  function prepareManagedStimmzettelKandidat(): Builder<DseKandidat> {
    return proxyBuilder<DseKandidat>(createManagedStimmzettelKandidat());
  }

  function prepareManagedStimmzettelKandidatForWahlvorschlag(
    owningWahlvorschlag: DseWahlvorschlag
  ): Builder<DseKandidat> {
    return proxyBuilder<DseKandidat>(
      _createManagedStimmzettelKandidatForWahlvorschlag(owningWahlvorschlag)
    );
  }

  function prepareManagedStimmzettelWahlvorschlag(): Builder<DseWahlvorschlag> {
    return proxyBuilder<DseWahlvorschlag>(
      createManagedStimmzettelWahlvorschlag()
    );
  }

  function prepareManagedStimmzettelStimmzettel(): Builder<DseStimmzettel> {
    return proxyBuilder<DseStimmzettel>(createManagedStimmzettelStimmzettel());
  }

  function _createManagedStimmzettelKandidatForWahlvorschlag(
    wahlvorschlag: DseWahlvorschlag
  ): DseKandidat {
    const listenposition = generateRandomNumber(2);
    return {
      kandidatId: generateRandomString(10),
      listenposition: listenposition,
      ordnungszahl: wahlvorschlag.ordnungszahl * 100 + listenposition,
      nennung: generateRandomNumber(2),
      durchgestrichen: generateRandomBoolean(),
      einzelstimmen: generateRandomNumber(2),
      ungueltigeStimmen: generateRandomNumber(2),
      reststimmen: generateRandomNumber(2),
      name: generateRandomString(10),
      owningWahlvorschlag: wahlvorschlag,
    };
  }

  return {
    createManagedStimmzettelKandidat,
    createManagedStimmzettelWahlvorschlag,
    createManagedStimmzettelStimmzettel,
    prepareManagedStimmzettelKandidat,
    prepareManagedStimmzettelKandidatForWahlvorschlag,
    prepareManagedStimmzettelWahlvorschlag,
    prepareManagedStimmzettelStimmzettel,
  };
}
