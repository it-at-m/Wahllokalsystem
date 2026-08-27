import { WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL } from "@/constants.ts";

export function useHandlerTools() {
  function isValidKandidatOrdnungszahl(value: number): boolean {
    return (
      Number.isSafeInteger(value) &&
      value % WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL != 0
    );
  }

  function isValidWahlvorschlagOrdnungszahl(value: number): boolean {
    return (
      Number.isSafeInteger(value) &&
      (value < 100 ||
        value % WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL == 0)
    );
  }

  function isValidCount(value: number): boolean {
    return Number.isSafeInteger(value) && value > 0;
  }

  function isValidRange(lowerBound: number, upperBound: number) {
    return (
      Math.floor(
        lowerBound / WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL
      ) ===
      Math.floor(upperBound / WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL)
    );
  }

  function normalizeBounds(
    a: number,
    b: number
  ): { lower: number; upper: number } {
    return { lower: Math.min(a, b), upper: Math.max(a, b) };
  }

  function parseOptionalPlusCountToNumber(text: string | undefined): number {
    if (!text || text.length === 0) return 1;
    // accept only plain non-negative integer digits
    if (!/^\d+$/.test(text)) return Number.NaN;
    return Number.parseInt(text);
  }

  return {
    isValidKandidatOrdnungszahl,
    isValidWahlvorschlagOrdnungszahl,
    isValidCount,
    isValidRange,
    normalizeBounds,
    parseOptionalPlusCountToNumber,
  };
}
