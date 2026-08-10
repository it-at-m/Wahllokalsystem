import type { Kandidat } from "@/types/dse/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/Wahlvorschlag.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

export function useManagedStimmzettelTestDataFactory() {
  const { generateRandomNumber, generateRandomBoolean, generateRandomString } =
    useCommonTestDataFactory();

  function createManagedStimmzettelKandidat(): Kandidat {
    return {
      kandidatId: generateRandomString(10),
      listenposition: generateRandomNumber(2),
      nennung: generateRandomNumber(2),
      isDiscarded: generateRandomBoolean(),
      votesByVoter: generateRandomNumber(2),
      invalidVotes: generateRandomNumber(2),
      votesByWahlvorschlag: generateRandomNumber(2),
    };
  }

  function createManagedStimmzettelWahlvorschlag(): Wahlvorschlag {
    return {
      wahlvorschlagID: generateRandomString(10),
      ordnungszahl: generateRandomNumber(2),
      selected: generateRandomBoolean(),
      kandidaten: [
        createManagedStimmzettelKandidat(),
        createManagedStimmzettelKandidat(),
        createManagedStimmzettelKandidat(),
      ],
    };
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

  return {
    createManagedStimmzettelKandidat,
    createManagedStimmzettelWahlvorschlag,
    createManagedStimmzettelStimmzettel,
    prepareManagedStimmzettelKandidat,
    prepareManagedStimmzettelWahlvorschlag,
    prepareManagedStimmzettelStimmzettel,
  };
}
