import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";

const { createErgebnis } = useErgebnisseTestDataFactory();
const { createWahlvorschlag } = useWahlvorschlaegeTestDataFactory();

export function useMbwErgebnisseAndWahlvorschlagTestDataFactory() {
  function createMbwErgebnisseAndWahlvorschlag(): MbwErgebnisseAndWahlvorschlag {
    return {
      ergebnisStapelA: createErgebnis(),
      ergebnisStapelB: createErgebnis(),
      wahlvorschlag: createWahlvorschlag(),
    };
  }

  function prepareMbwErgebnisseAndWahlvorschlag() {
    return proxyBuilder<MbwErgebnisseAndWahlvorschlag>(
      createMbwErgebnisseAndWahlvorschlag()
    );
  }

  return {
    createMbwErgebnisseAndWahlvorschlag,
    prepareMbwErgebnisseAndWahlvorschlag,
  };
}
