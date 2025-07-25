import type { UngueltigerWahlschein } from "@/types/wahlbezirk/UngueltigerWahlschein.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomString } = useCommonTestDataFactory();

export function useWahlbezirkTestDataFactory() {
  function createUngueltigerWahlschein(): UngueltigerWahlschein {
    return {
      wahlscheinnummer: generateRandomString(4),
      vorname: generateRandomString(10),
      familienname: generateRandomString(10),
    };
  }

  function prepareUngueltigerWahlschein(): Builder<UngueltigerWahlschein> {
    return proxyBuilder<UngueltigerWahlschein>(createUngueltigerWahlschein());
  }

  return { createUngueltigerWahlschein, prepareUngueltigerWahlschein };
}
