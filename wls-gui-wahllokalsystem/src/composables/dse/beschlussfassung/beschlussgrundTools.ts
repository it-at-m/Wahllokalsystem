import type { Beschlussgrund } from "@/types/dse/beschlussfassung/Beschlussgrund.ts";

export function useBeschlussgrundTools() {
  function createBeschlussgrundWithText(text: string): Beschlussgrund {
    return {
      text,
    };
  }

  return {
    createBeschlussgrundWithText,
  };
}
