import type { EroeffnungsUhrzeitWriteDTO } from "@/api/wls-clients/generated-wahlvorbereitung-api";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomDateTimeAsString } = useCommonTestDataFactory();

export function useWahlvorbereitungTestDataFactory() {
  function createEroeffnungsUhrzeitWriteDTO(): EroeffnungsUhrzeitWriteDTO {
    return {
      eroeffnungsuhrzeit: generateRandomDateTimeAsString(),
    };
  }

  return {
    createEroeffnungsUhrzeitWriteDTO,
  };
}
