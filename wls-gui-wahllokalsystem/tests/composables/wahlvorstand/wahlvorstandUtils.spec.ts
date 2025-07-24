import { useWahlvorstandTestDataFactory } from "@tests/utils/wahlvorstand/WahlvorstandTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useWahlvorstandComparators } from "@/composables/wahlvorstand/wahlvorstandUtils.ts";
import { WahlvorstandsmitgliedFunktionEnum } from "@/types/wahlvorstand/WahlvorstandsmitgliedFunktion.ts";

const { prepareWahlvorstandsmitglied } = useWahlvorstandTestDataFactory();

describe("useWahlvorstandComparators", () => {
  const { compareWahlvorstandsMitglieder } = useWahlvorstandComparators();

  it("should_sortByFunktion_when_funktionIsDifferent", () => {
    const mitgliedA = prepareWahlvorstandsmitglied()
      .identifikator("1")
      .anwesend(true)
      .funktion(WahlvorstandsmitgliedFunktionEnum.W)
      .vorname("Xaver")
      .familienname("Zinfandel")
      .build();
    const mitgliedB = prepareWahlvorstandsmitglied()
      .identifikator("2")
      .anwesend(true)
      .funktion(WahlvorstandsmitgliedFunktionEnum.Sb)
      .vorname("Anna")
      .familienname("Bauer")
      .build();

    expect(compareWahlvorstandsMitglieder(mitgliedA, mitgliedB)).toBeLessThan(
      0
    );
    expect(
      compareWahlvorstandsMitglieder(mitgliedB, mitgliedA)
    ).toBeGreaterThan(0);
  });

  it("should_sortByFamilienname_when_funktionIsTheSame", () => {
    const mitgliedA = prepareWahlvorstandsmitglied()
      .identifikator("1")
      .anwesend(true)
      .funktion(WahlvorstandsmitgliedFunktionEnum.W)
      .vorname("Xaver")
      .familienname("Zinfandel")
      .build();
    const mitgliedB = prepareWahlvorstandsmitglied()
      .identifikator("2")
      .anwesend(true)
      .funktion(WahlvorstandsmitgliedFunktionEnum.W)
      .vorname("Anna")
      .familienname("Bauer")
      .build();

    expect(
      compareWahlvorstandsMitglieder(mitgliedA, mitgliedB)
    ).toBeGreaterThan(0);
    expect(compareWahlvorstandsMitglieder(mitgliedB, mitgliedA)).toBeLessThan(
      0
    );
  });

  it("should_sortByVorname_when_funktionAndFamiliennameAreTheSame", () => {
    const mitgliedA = prepareWahlvorstandsmitglied()
      .identifikator("1")
      .anwesend(true)
      .funktion(WahlvorstandsmitgliedFunktionEnum.W)
      .vorname("Anna")
      .familienname("Müller")
      .build();
    const mitgliedB = prepareWahlvorstandsmitglied()
      .identifikator("2")
      .anwesend(true)
      .funktion(WahlvorstandsmitgliedFunktionEnum.W)
      .vorname("Xaver")
      .familienname("Müller")
      .build();

    expect(compareWahlvorstandsMitglieder(mitgliedA, mitgliedB)).toBeLessThan(
      0
    );
    expect(
      compareWahlvorstandsMitglieder(mitgliedB, mitgliedA)
    ).toBeGreaterThan(0);
  });

  it("should_handleCase_when_funktionAndFamiliennameAndVornameAreTheSame", () => {
    const mitgliedA = prepareWahlvorstandsmitglied()
      .identifikator("1")
      .anwesend(true)
      .funktion(WahlvorstandsmitgliedFunktionEnum.W)
      .vorname("Max")
      .familienname("Mustermann")
      .build();
    const mitgliedB = prepareWahlvorstandsmitglied()
      .identifikator("2")
      .anwesend(true)
      .funktion(WahlvorstandsmitgliedFunktionEnum.W)
      .vorname("Max")
      .familienname("Mustermann")
      .build();

    expect(compareWahlvorstandsMitglieder(mitgliedA, mitgliedB)).toBe(0);
  });
});
