import type { AbstractCommandEvent } from "@/types/experimental/AbstractCommandEvent.ts";
import type { AbstractExpressionHandlerFunction } from "@/types/experimental/AbstractExpressionHandlerFunction.ts";

import { useKandidatHandlers } from "@/composables/experimental/expressionHandler/KandidatHandlers.ts";
import { useWahlvorschlagHandlers } from "@/composables/experimental/expressionHandler/WahlvorschlagHandlers.ts";

export function useStimmzettelQuickInputHandler() {
  const { handleSetExpression } = useWahlvorschlagHandlers();
  const {
    handleSetVotesExpression,
    handleAddVotesExpression,
    handleDiscardKandidatExpression,
  } = useKandidatHandlers();
  const handlers: AbstractExpressionHandlerFunction[] = [
    handleSetExpression,
    handleDiscardKandidatExpression,
    handleSetVotesExpression,
    handleAddVotesExpression,
  ];

  function handleQuickInput(
    quickInputString: string
  ): AbstractCommandEvent | null {
    let commandEvent: AbstractCommandEvent | null = null;
    //set result first return value of handler
    for (let i = 0; i < handlers.length && !commandEvent; ++i) {
      const handler = handlers[i];
      if (handler !== undefined) {
        commandEvent = handler(quickInputString);
      }
    }

    return commandEvent;
  }

  return { handleQuickInput };
}
