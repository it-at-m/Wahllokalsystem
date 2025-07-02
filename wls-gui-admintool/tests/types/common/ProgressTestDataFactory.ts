import type { Progress } from "@/types/common/Progress.ts";
import type { Builder } from "@tests/utils/common/Builder.ts";

import { proxyBuilder } from "@tests/utils/common/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomNumber, generateRandomBoolean } =
  useCommonTestDataFactory();

export function useProgressTestDataFactory() {
  function createProgressComplete(): Progress {
    return {
      finished: generateRandomNumber(2),
      next: `next ${generateRandomNumber(10)}`,
      total: generateRandomNumber(2),
      active: generateRandomBoolean(),
    };
  }

  function prepareProgress(): Builder<Progress> {
    return proxyBuilder<Progress>(createProgressComplete());
  }

  return {
    createProgressComplete,
    prepareProgress,
  };
}
