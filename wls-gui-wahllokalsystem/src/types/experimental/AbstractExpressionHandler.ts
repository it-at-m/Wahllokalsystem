import type { AbstractCommandEvent } from "@/types/experimental/AbstractCommandEvent.ts";

export interface AbstractExpressionHandler {
  handleSetExpression: function (expression: string): AbstractCommandEvent | null
}