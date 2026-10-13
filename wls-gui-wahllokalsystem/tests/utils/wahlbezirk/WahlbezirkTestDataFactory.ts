import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlhandlung/UrnenwahlSchliessungsuhrzeit.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomDateTimeAsString } = useCommonTestDataFactory();

export function useWahlbezirkTestDataFactory() {
  function createUrnenwahlSchliessungsuhrzeit(): UrnenwahlSchliessungsuhrzeit {
    return {
      schliessungsuhrzeit: generateRandomDateTimeAsString(),
    };
  }

  return {
    createUrnenwahlSchliessungsuhrzeit,
  };
}
