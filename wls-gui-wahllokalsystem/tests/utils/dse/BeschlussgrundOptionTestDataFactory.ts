import type { BeschlussgrundOption } from "@/types/dse/beschlussfassung/BeschlussgrundOption.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomBoolean, generateRandomString } =
  useCommonTestDataFactory();

export function useBeschlussgrundOptionTestDataFactory() {
  function createBeschlussgrundOption(): BeschlussgrundOption {
    return {
      grund: generateRandomString(5),
      selected: generateRandomBoolean(),
    };
  }

  function prepareBeschlussgrundOption(): Builder<BeschlussgrundOption> {
    return proxyBuilder<BeschlussgrundOption>(createBeschlussgrundOption());
  }

  return {
    createBeschlussgrundOption,
    prepareBeschlussgrundOption,
  };
}
