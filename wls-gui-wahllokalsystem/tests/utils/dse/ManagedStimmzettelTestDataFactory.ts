import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

export function useManagedStimmzettelTestDataFactory() {
  const { generateRandomNumber, generateRandomBoolean, generateRandomString } =
    useCommonTestDataFactory();

  function createManagedStimmzettelKandidat(): Kandidat {
    return createManagedStimmzettelWahlvorschlag().kandidaten[0];
  }

  function createManagedStimmzettelWahlvorschlag(): Wahlvorschlag {
    const result: Wahlvorschlag = {
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

  function createManagedStimmzettelStimmzettel(): Stimmzettel {
    return {
      stimmzettelkennung: generateRandomNumber(6),
      wahlvorschlaege: [
        createManagedStimmzettelWahlvorschlag(),
        createManagedStimmzettelWahlvorschlag(),
        createManagedStimmzettelWahlvorschlag(),
      ],
      invalideVotes: generateRandomNumber(2),
      gueltigkeit: null,
      beschlussvorschlag: [],
      beschlussfassung: null,
    };
  }

  function prepareManagedStimmzettelKandidat(): Builder<Kandidat> {
    return proxyBuilder<Kandidat>(createManagedStimmzettelKandidat());
  }

  function prepareManagedStimmzettelWahlvorschlag(): Builder<Wahlvorschlag> {
    return proxyBuilder<Wahlvorschlag>(createManagedStimmzettelWahlvorschlag());
  }

  function prepareManagedStimmzettelStimmzettel(): Builder<Stimmzettel> {
    return proxyBuilder<Stimmzettel>(createManagedStimmzettelStimmzettel());
  }

  function _createManagedStimmzettelKandidatForWahlvorschlag(
    wahlvorschlag: Wahlvorschlag
  ): Kandidat {
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
    prepareManagedStimmzettelWahlvorschlag,
    prepareManagedStimmzettelStimmzettel,
  };
}
