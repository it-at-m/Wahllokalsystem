import type { PersistedBeschlussfassung } from "@/types/dse/beschlussfassung/PersistedBeschlussfassung.ts";
import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";
import type { PersistedKandidat } from "@/types/dse/stimmzettelerfassung/PersistedKandidat.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { PersistedWahlvorschlag } from "@/types/dse/stimmzettelerfassung/PersistedWahlvorschlag.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { PersistedStimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettelGueltigkeitEnum.ts";

const {
  generateRandomNumber,
  generateRandomBoolean,
  generateRandomString,
  getRandomItem,
} = useCommonTestDataFactory();

export function usePersistedStimmzettelTestDataFactory() {
  function createStimmzettelBeschlussfassung(): PersistedBeschlussfassung {
    return {
      text: generateRandomString(20),
      pro: generateRandomNumber(2),
      contra: generateRandomNumber(2),
    };
  }

  function createStimmzettelSystemBeschlussgrund(): SystemBeschlussgrund {
    return {
      reason: getRandomItem(Object.values(SystemBeschlussgrundReasonEnum)),
    };
  }

  function createStimmzettelWahlvorstandBeschlussgrund(): WahlvorstandBeschlussgrund {
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

  function createPersistedStimmzettel(): PersistedStimmzettel {
    return {
      stimmzettelkennung: generateRandomNumber(6),
      teamID: generateRandomString(1),
      wahlvorschlaege: [
        createPersistedStimmzettelWahlvorschlag(),
        createPersistedStimmzettelWahlvorschlag(),
        createPersistedStimmzettelWahlvorschlag(),
      ],
      systemBeschlussvorschlag: [
        createStimmzettelSystemBeschlussgrund(),
        createStimmzettelSystemBeschlussgrund(),
        createStimmzettelSystemBeschlussgrund(),
      ],
      wahlvorstandBeschlussvorschlag: [
        createStimmzettelWahlvorstandBeschlussgrund(),
        createStimmzettelWahlvorstandBeschlussgrund(),
        createStimmzettelWahlvorstandBeschlussgrund(),
      ],
      beschlussfassung: createStimmzettelBeschlussfassung(),
      invalideVotes: generateRandomNumber(2),
      gueltigkeit: getRandomItem(
        Object.values(PersistedStimmzettelGueltigkeitEnum)
      ),
    };
  }

  function preparePersistedStimmzettel(): Builder<PersistedStimmzettel> {
    return proxyBuilder<PersistedStimmzettel>(createPersistedStimmzettel());
  }

  function preparePersistedStimmzettelBeschlussfassung(): Builder<PersistedBeschlussfassung> {
    return proxyBuilder<PersistedBeschlussfassung>(
      createStimmzettelBeschlussfassung()
    );
  }

  function preparePersistedStimmzettelBeschlussgrund(): Builder<WahlvorstandBeschlussgrund> {
    return proxyBuilder<WahlvorstandBeschlussgrund>(
      createStimmzettelWahlvorstandBeschlussgrund()
    );
  }

  function preparePersistedStimmzettelKandidat(): Builder<PersistedKandidat> {
    return proxyBuilder<PersistedKandidat>(
      createPersistedStimmzettelKandidat()
    );
  }

  function preparePersistedStimmzettelWahlvorschlag(): Builder<PersistedWahlvorschlag> {
    return proxyBuilder<PersistedWahlvorschlag>(
      createPersistedStimmzettelWahlvorschlag()
    );
  }

  return {
    createPersistedStimmzettelKandidat,
    createPersistedStimmzettelWahlvorschlag,
    createPersistedStimmzettel,
    preparePersistedStimmzettel,
    preparePersistedStimmzettelBeschlussfassung,
    preparePersistedStimmzettelBeschlussgrund,
    preparePersistedStimmzettelKandidat,
    preparePersistedStimmzettelWahlvorschlag,
  };
}
