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
  const handler: AbstractExpressionHandlerFunction[] = [
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
    for (let i = 0; i < handler.length && !commandEvent; ++i) {
      if (handler[i] !== undefined) {
        commandEvent = handler[i](quickInputString);
      }
    }

    return commandEvent;
  }

  return { handleQuickInput };
}
