import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";
import type { Builder } from "@tests/common/Builder.ts";

import { proxyBuilder } from "@tests/common/Builder.ts";
import { useCommonTestDataFactory } from "@tests/common/CommonTestDataFactory.ts";

const { generateDateRandomAsString, generateNumberRandom } =
  useCommonTestDataFactory();

export function useWahltagTestDataFactory() {
  function createWahltagDtoComplete(): Builder<WahltagDTO> {
    return proxyBuilder<WahltagDTO>({
      wahltag: generateDateRandomAsString(),
      wahltagID: `wahltagID${generateNumberRandom(3)}`,
      nummer: `${generateNumberRandom(2)}`,
      beschreibung: `beschreibung${generateNumberRandom(3)}`,
    });
  }

  return {
    createWahltagDtoComplete,
  };
}
