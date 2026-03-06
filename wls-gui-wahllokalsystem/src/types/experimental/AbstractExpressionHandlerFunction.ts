import type { KandidatEvent } from "@/types/experimental/KandidatEvent.ts";
import type { WahlvorschlagEvent } from "@/types/experimental/WahlvorschlagEvent.ts";

export type AbstractExpressionHandlerFunction = (
  expression: string
) => WahlvorschlagEvent | KandidatEvent | null;
