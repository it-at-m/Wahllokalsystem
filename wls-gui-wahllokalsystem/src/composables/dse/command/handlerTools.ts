import { WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL } from "@/composables/dse/ManagedStimmzettel.ts";

/**
 * Composable mit gemeinsam genutzten Prüf- und Hilfsfunktionen für Command-Handler.
 * Minimal gehalten, um die modulare Trennung der Handler zu bewahren.
 */
export function useHandlerTools() {
  function isValidKandidatOrdnungszahl(value: number): boolean {
    return (
      Number.isSafeInteger(value) &&
      value % WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL != 0
    );
  }

  function isValidCount(value: number): boolean {
    return Number.isSafeInteger(value) && value > 0;
  }

  function normalizeBounds(
    a: number,
    b: number
  ): { lower: number; upper: number } {
    return { lower: Math.min(a, b), upper: Math.max(a, b) };
  }

  /**
   * Parsed optionales Plus-Segment (z.B. "+3").
   * Ohne Text → Default 1. Bei ungültig → NaN (Validierung erfolgt separat).
   */
  function parseOptionalPlusCount(text: string | undefined): number {
    if (!text || text.length === 0) return 1;
    // accept only plain non-negative integer digits
    if (!/^\d+$/.test(text)) return Number.NaN;
    return Number.parseInt(text);
  }

  return {
    isValidKandidatOrdnungszahl,
    isValidCount,
    normalizeBounds,
    parseOptionalPlusCount,
  };
}
