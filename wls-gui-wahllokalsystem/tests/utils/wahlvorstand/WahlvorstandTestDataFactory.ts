import type {
  WahlvorstandDTO,
  WahlvorstandsmitgliedDTO,
} from "@/api/wls-clients/generated-wahlvorstand-api";
import type { Wahlvorstand } from "@/types/wahlvorstand/Wahlvorstand.ts";
import type { Wahlvorstandsmitglied } from "@/types/wahlvorstand/Wahlvorstandsmitglied.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { WahlvorstandsmitgliedDTOFunktionEnum } from "@/api/wls-clients/generated-wahlvorstand-api";
import { WahlvorstandsmitgliedFunktionEnum } from "@/types/wahlvorstand/WahlvorstandsmitgliedFunktion.ts";

const {
  generateRandomBoolean,
  generateRandomString,
  generateRandomDateTimeAsString,
  getRandomItem,
} = useCommonTestDataFactory();

const wahlvorstandsmitgliedDTOFunktionEnumValues = Object.values(
  WahlvorstandsmitgliedDTOFunktionEnum
);
const wahlvorstandsmitgliedFunktionEnumValues = Object.values(
  WahlvorstandsmitgliedFunktionEnum
);

export function useWahlvorstandTestDataFactory() {
  function createWahlvorstand(countMitglieder = 3): Wahlvorstand {
    const wahlvorstandsmitglieder: Wahlvorstandsmitglied[] = [];
    for (let i = 0; i < countMitglieder; i++) {
      wahlvorstandsmitglieder.push(createWahlvorstandsmitglied());
    }
    return {
      wahlvorstandsmitglieder: wahlvorstandsmitglieder,
    };
  }

  function createWahlvorstandDTO(countMitglieder = 3): WahlvorstandDTO {
    const wahlvorstandsmitglieder: WahlvorstandsmitgliedDTO[] = [];
    for (let i = 0; i < countMitglieder; i++) {
      wahlvorstandsmitglieder.push(createWahlvorstandsmitgliedDTO());
    }
    return {
      wahlbezirkID: generateRandomString(10),
      wahlvorstandsmitglieder: wahlvorstandsmitglieder,
      anwesenheitBeginn: generateRandomDateTimeAsString(),
    };
  }

  function createWahlvorstandsmitglied(): Wahlvorstandsmitglied {
    return {
      familienname: generateRandomString(10),
      anwesend: true,
      vorname: generateRandomString(10),
      funktion: getRandomItem(wahlvorstandsmitgliedFunktionEnumValues),
      funktionsname: generateRandomString(10),
      identifikator: generateRandomString(10),
    };
  }

  function createWahlvorstandsmitgliedDTO(): WahlvorstandsmitgliedDTO {
    return {
      funktion: getRandomItem(wahlvorstandsmitgliedDTOFunktionEnumValues),
      anwesend: generateRandomBoolean(),
      familienname: generateRandomString(10),
      identifikator: generateRandomString(10),
      funktionsname: generateRandomString(10),
      vorname: generateRandomString(10),
    };
  }

  function prepareWahlvorstandsmitglied(): Builder<Wahlvorstandsmitglied> {
    return proxyBuilder<Wahlvorstandsmitglied>(createWahlvorstandsmitglied());
  }

  function prepareWahlvorstandsmitgliedDTO(): Builder<WahlvorstandsmitgliedDTO> {
    return proxyBuilder<WahlvorstandsmitgliedDTO>(
      createWahlvorstandsmitgliedDTO()
    );
  }

  function prepareWahlvorstand(): Builder<Wahlvorstand> {
    return proxyBuilder<Wahlvorstand>(createWahlvorstand());
  }

  function prepareWahlvorstandDTO(): Builder<WahlvorstandDTO> {
    return proxyBuilder<WahlvorstandDTO>(createWahlvorstandDTO());
  }

  return {
    createWahlvorstand,
    createWahlvorstandDTO,
    createWahlvorstandsmitglied,
    createWahlvorstandsmitgliedDTO,
    prepareWahlvorstandsmitglied,
    prepareWahlvorstandsmitgliedDTO,
    prepareWahlvorstand,
    prepareWahlvorstandDTO,
  };
}
