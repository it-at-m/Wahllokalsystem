import { useWahlvorstandTestDataFactory } from "@tests/utils/wahlvorstand/WahlvorstandTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useWahlvorstandComparators } from "@/composables/wahlvorstand/wahlvorstandUtils.ts";
import { WahlvorstandsmitgliedFunktionEnum } from "@/types/wahlvorstand/WahlvorstandsmitgliedFunktion.ts";

const { prepareWahlvorstandsmitglied } = useWahlvorstandTestDataFactory();

describe("wahlvorstandUtils.ts", () => {
  const { compareWahlvorstandsMitglieder } = useWahlvorstandComparators();

  describe("compareWahlvorstandsMitglieder", () => {
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

    it("should_sortArrayByFunktionFamiliennameVorname_when_unsortedArrayIsGiven", () => {
      const mitglied1 = prepareWahlvorstandsmitglied()
        .identifikator("1")
        .anwesend(true)
        .familienname("Müller")
        .vorname("Anna")
        .funktion(WahlvorstandsmitgliedFunktionEnum.Sb)
        .build();
      const mitglied2 = prepareWahlvorstandsmitglied()
        .identifikator("2")
        .anwesend(true)
        .familienname("Bauer")
        .vorname("Karl")
        .funktion(WahlvorstandsmitgliedFunktionEnum.W)
        .build();
      const mitglied3 = prepareWahlvorstandsmitglied()
        .identifikator("3")
        .anwesend(true)
        .familienname("Schmidt")
        .vorname("Ursula")
        .funktion(WahlvorstandsmitgliedFunktionEnum.Sb)
        .build();
      const mitglied4 = prepareWahlvorstandsmitglied()
        .identifikator("4")
        .anwesend(true)
        .familienname("Müller")
        .vorname("Hans")
        .funktion(WahlvorstandsmitgliedFunktionEnum.Swb)
        .build();
      const mitglied5 = prepareWahlvorstandsmitglied()
        .identifikator("5")
        .anwesend(true)
        .familienname("Schmidt")
        .vorname("Berta")
        .funktion(WahlvorstandsmitgliedFunktionEnum.B)
        .build();
      const mitglied6 = prepareWahlvorstandsmitglied()
        .identifikator("6")
        .anwesend(true)
        .familienname("Schmidt")
        .vorname("Anna")
        .funktion(WahlvorstandsmitgliedFunktionEnum.B)
        .build();
      const unsortedWahlvorstandsmitglieder = [
        mitglied1,
        mitglied2,
        mitglied3,
        mitglied4,
        mitglied5,
        mitglied6,
      ];
      const expectedSortedWahlvorstandsmitglieder = [
        mitglied2,
        mitglied4,
        mitglied1,
        mitglied3,
        mitglied6,
        mitglied5,
      ];

      expect(
        unsortedWahlvorstandsmitglieder.sort(compareWahlvorstandsMitglieder)
      ).toStrictEqual(expectedSortedWahlvorstandsmitglieder);
    });
  });
});
