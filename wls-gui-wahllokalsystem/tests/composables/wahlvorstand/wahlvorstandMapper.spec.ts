import type {
  WahlvorstandsmitgliedDTO,
  WahlvorstandWriteDTO,
} from "@/api/wls-clients/generated-wahlvorstand-api";
import type { Wahlvorstand } from "@/types/wahlvorstand/Wahlvorstand";
import type { Wahlvorstandsmitglied } from "@/types/wahlvorstand/Wahlvorstandsmitglied";

import { useWahlvorstandTestDataFactory } from "@tests/utils/wahlvorstand/WahlvorstandTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useWahlvorstandMapper } from "@/composables/wahlvorstand/wahlvorstandMapper.ts";

const {
  prepareWahlvorstandsmitgliedDTO,
  prepareWahlvorstandDTO,
  prepareWahlvorstandsmitglied,
  prepareWahlvorstand,
} = useWahlvorstandTestDataFactory();

describe("wahlvorstandMapper.ts", () => {
  const { toModel, toDto } = useWahlvorstandMapper();

  describe("toModel", () => {
    it("should_returnWahlvorstand_when_dtoIsGiven", () => {
      const dtoToMap = prepareWahlvorstandDTO()
        .anwesenheitBeginn("2025-04-28T08:05:00")
        .wahlbezirkID("1234")
        .wahlvorstandsmitglieder(getWahlvorstandmitgliederDTO())
        .build();

      const result = toModel(dtoToMap);

      const expectedResult: Wahlvorstand = {
        wahlvorstandsmitglieder: getWahlvorstandmitglieder().map(
          (mitglied) => ({
            ...mitglied,
            anwesend: false,
          })
        ),
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("toDto", () => {
    it("should_returnWahlvorstandWriteDto_when_modelIsGiven", () => {
      const wahlvorstandToMap = prepareWahlvorstand()
        .wahlvorstandsmitglieder(getWahlvorstandmitglieder())
        .build();

      const datetime = new Date("2025-04-28T08:05:00");
      const result = toDto(wahlvorstandToMap, datetime);

      const expectedResult: WahlvorstandWriteDTO = {
        wahlvorstandsmitglieder: getWahlvorstandmitgliederDTO(),
        anwesenheitBeginn: "2025-04-28T08:05:00.000",
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });
});

function getWahlvorstandmitgliederDTO(): WahlvorstandsmitgliedDTO[] {
  return [
    prepareWahlvorstandsmitgliedDTO()
      .identifikator("1")
      .funktion("W")
      .funktionsname("Vorstand")
      .anwesend(true)
      .familienname("Müller")
      .vorname("Gerd")
      .build(),
    prepareWahlvorstandsmitgliedDTO()
      .identifikator("2")
      .funktion("B")
      .funktionsname("Beisitzer")
      .anwesend(false)
      .familienname("Meier")
      .vorname("Sepp")
      .build(),
  ];
}

function getWahlvorstandmitglieder(): Wahlvorstandsmitglied[] {
  return [
    prepareWahlvorstandsmitglied()
      .identifikator("1")
      .funktion("W")
      .funktionsname("Vorstand")
      .anwesend(true)
      .familienname("Müller")
      .vorname("Gerd")
      .build(),
    prepareWahlvorstandsmitglied()
      .identifikator("2")
      .funktion("B")
      .funktionsname("Beisitzer")
      .anwesend(false)
      .familienname("Meier")
      .vorname("Sepp")
      .build(),
  ];
}
