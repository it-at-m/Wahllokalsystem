import { describe, expect, it } from "vitest";

import { useWahlvorstandComparators } from "@/composables/wahlvorstand/wahlvorstandUtils.ts";
import { WahlvorstandsmitgliedFunktionEnum } from "@/types/wahlvorstand/WahlvorstandsmitgliedFunktion.ts";

describe("Wahlvorstand Comparators", () => {
  const { compareWahlvorstandsMitglieder } = useWahlvorstandComparators();

  it("should_sortByFunktion_first", () => {
    const mitgliedA = {
      identifikator: "1",
      anwesend: true,
      funktion: WahlvorstandsmitgliedFunktionEnum.W,
      vorname: "Xaver",
      familienname: "Zinfandel",
    };
    const mitgliedB = {
      identifikator: "2",
      anwesend: true,
      funktion: WahlvorstandsmitgliedFunktionEnum.Sb,
      vorname: "Anna",
      familienname: "Bauer",
    };

    expect(compareWahlvorstandsMitglieder(mitgliedA, mitgliedB)).toBeLessThan(
      0
    );
    expect(
      compareWahlvorstandsMitglieder(mitgliedB, mitgliedA)
    ).toBeGreaterThan(0);
  });

  it("should_sortByFamilienname_when_funktionenAreTheSame", () => {
    const mitgliedA = {
      identifikator: "1",
      anwesend: true,
      funktion: WahlvorstandsmitgliedFunktionEnum.W,
      vorname: "Xaver",
      familienname: "Zinfandel",
    };
    const mitgliedB = {
      identifikator: "2",
      anwesend: true,
      funktion: WahlvorstandsmitgliedFunktionEnum.W,
      vorname: "Anna",
      familienname: "Bauer",
    };

    expect(
      compareWahlvorstandsMitglieder(mitgliedA, mitgliedB)
    ).toBeGreaterThan(0);
    expect(compareWahlvorstandsMitglieder(mitgliedB, mitgliedA)).toBeLessThan(
      0
    );
  });

  it("should_sortByVorname_whenFunktionAndFamiliennameAreTheSame", () => {
    const mitgliedA = {
      identifikator: "1",
      anwesend: true,
      funktion: WahlvorstandsmitgliedFunktionEnum.W,
      vorname: "Anna",
      familienname: "Müller",
    };
    const mitgliedB = {
      identifikator: "2",
      anwesend: true,
      funktion: WahlvorstandsmitgliedFunktionEnum.W,
      vorname: "Xaver",
      familienname: "Müller",
    };

    expect(compareWahlvorstandsMitglieder(mitgliedA, mitgliedB)).toBeLessThan(
      0
    );
    expect(
      compareWahlvorstandsMitglieder(mitgliedB, mitgliedA)
    ).toBeGreaterThan(0);
  });

  it("should_handleCasesWithSimilarAttributes", () => {
    const mitgliedA = {
      identifikator: "1",
      anwesend: true,
      funktion: WahlvorstandsmitgliedFunktionEnum.W,
      vorname: "Max",
      familienname: "Mustermann",
    };
    const mitgliedB = {
      identifikator: "2",
      anwesend: true,
      funktion: WahlvorstandsmitgliedFunktionEnum.W,
      vorname: "Max",
      familienname: "Mustermann",
    };

    expect(compareWahlvorstandsMitglieder(mitgliedA, mitgliedB)).toBe(0);
  });
});
