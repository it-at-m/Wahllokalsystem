import type { Wahlvorstandsmitglied } from "@/types/wahlvorstand/Wahlvorstandsmitglied.ts";

import { WahlvorstandsmitgliedFunktionEnum } from "@/types/wahlvorstand/WahlvorstandsmitgliedFunktion.ts";

export function useWahlvorstandComparators() {
  const funktionSortOrder: Record<WahlvorstandsmitgliedFunktionEnum, number> = {
    W: 0,
    SWB: 1,
    SB: 2,
    SSB: 3,
    B: 4,
  };

  function compareWahlvorstandsMitglieder(
    mitglied1: Wahlvorstandsmitglied,
    mitglied2: Wahlvorstandsmitglied
  ) {
    return (
      _compareWahlvorstandsmitgliedByFunktion(mitglied1, mitglied2) ||
      _compareWahlvorstandsmitgliedByFamilienname(mitglied1, mitglied2) ||
      _compareWahlvorstandsmitgliedByVorname(mitglied1, mitglied2)
    );
  }

  function _compareWahlvorstandsmitgliedByFunktion(
    mitglied1: Wahlvorstandsmitglied,
    mitglied2: Wahlvorstandsmitglied
  ) {
    return (
      funktionSortOrder[mitglied1.funktion] -
      funktionSortOrder[mitglied2.funktion]
    );
  }

  function _compareWahlvorstandsmitgliedByVorname(
    mitglied1: Wahlvorstandsmitglied,
    mitglied2: Wahlvorstandsmitglied
  ) {
    return mitglied1.vorname.localeCompare(mitglied2.vorname);
  }

  function _compareWahlvorstandsmitgliedByFamilienname(
    mitglied1: Wahlvorstandsmitglied,
    mitglied2: Wahlvorstandsmitglied
  ) {
    return mitglied1.familienname.localeCompare(mitglied2.familienname);
  }

  return {
    compareWahlvorstandsMitglieder,
  };
}
