import type { ErgebnismeldungDruckInput } from "@/types/ergebnismeldung/common/ErgebnismeldungDruckInput.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useAWerteTestDataFactory } from "@tests/utils/ergebnismeldung/common/aWerteTestDataFactory.ts";
import { useBWerteTestDataFactory } from "@tests/utils/ergebnismeldung/common/bWerteTestDataFactory.ts";
import { useMbwErgebnisseAndWahlvorschlagTestDataFactory } from "@tests/utils/ergebnismeldung/MBW/mbwErgebnisseAndWahlvorschlagTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";

import { useNumberFormatter } from "@/composables/common/numberFormatter.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { createWahl } = useWahlTestDataFactory();
const { createAWerte } = useAWerteTestDataFactory();
const { createBWerte } = useBWerteTestDataFactory();
const {
  generateRandomString,
  generateRandomNumberInRange,
  generateRandomBoolean,
} = useCommonTestDataFactory();
const { createMbwErgebnisseAndWahlvorschlag } =
  useMbwErgebnisseAndWahlvorschlagTestDataFactory();

const { convertToSixDigitArray } = useNumberFormatter();

export function useErgebnismeldungDruckInputTestDataFactory() {
  function createErgebnismeldungDruckInput(): ErgebnismeldungDruckInput {
    return {
      meldungsArt: MeldungsArtEnum.Schnellmeldung,
      wahlbezirksArt: WahlbezirksArtEnum.UWB,
      aktuelleWahl: createWahl(),
      footer: generateRandomString(15),
      alleStimmen: convertToSixDigitArray(
        generateRandomNumberInRange(0, 999999)
      ),
      gueltigeStimmenListe: [
        createMbwErgebnisseAndWahlvorschlag(),
        createMbwErgebnisseAndWahlvorschlag(),
        createMbwErgebnisseAndWahlvorschlag(),
      ],
      gueltigeStimmenGesamt: convertToSixDigitArray(
        generateRandomNumberInRange(0, 999999)
      ),
      ungueltigeStimmen: convertToSixDigitArray(
        generateRandomNumberInRange(0, 999999)
      ),
      bWerte: createBWerte(),
      aWerte: createAWerte(),
      wahlbezirkNummer: generateRandomString(3),
      barcode: generateRandomString(5),
      sendOk: generateRandomBoolean(),
    };
  }

  function prepareErgebnismeldungDruckInput(): Builder<ErgebnismeldungDruckInput> {
    return proxyBuilder<ErgebnismeldungDruckInput>(
      createErgebnismeldungDruckInput()
    );
  }

  return { createErgebnismeldungDruckInput, prepareErgebnismeldungDruckInput };
}
