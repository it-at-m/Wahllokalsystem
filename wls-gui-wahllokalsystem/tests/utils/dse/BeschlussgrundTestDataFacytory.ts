import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";

const { generateRandomString, getRandomItem } = useCommonTestDataFactory();

export function useBeschlussgrundTestDataFactory() {
  function createSystemBeschlussgrund(): SystemBeschlussgrund {
    return {
      reason: getRandomItem(Object.values(SystemBeschlussgrundReasonEnum)),
    };
  }

  function createWahlvorstandBeschlussgrund(): WahlvorstandBeschlussgrund {
    return {
      text: generateRandomString(20),
    };
  }

  return {
    createSystemBeschlussgrund,
    createWahlvorstandBeschlussgrund,
  };
}
