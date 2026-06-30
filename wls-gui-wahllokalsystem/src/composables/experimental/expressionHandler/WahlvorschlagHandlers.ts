import type { AbstractExpressionHandlerFunction } from "@/types/experimental/AbstractExpressionHandlerFunction.ts";
import type { WahlvorschlagEvent } from "@/types/experimental/WahlvorschlagEvent.ts";

import { useLogging } from "@/composables/common/logging.ts";
import { WahlvorschlagEventTypeEnum } from "@/types/experimental/WahlvorschlagEventTypeEnum.ts";

export function useWahlvorschlagHandlers() {
  const REGEX_SELECT_LISTE = /^[W|w](\d+)$/;
  const REGEX_DESELECT_LISTE = /^-[W|w](\d+)$/;
  const logger = useLogging("useSetWahlvorschlagHandler");

  const handleSetExpression: AbstractExpressionHandlerFunction = (
    expression: string
  ) => {
    logger.log(`processing expression: ${expression}`);
    const match = REGEX_SELECT_LISTE.exec(expression);

    if (match && match[1] !== undefined) {
      const wahlvorschlagOrdnungszahl = Number.parseInt(match[1]);
      logger.log(`wahlvorschlagOrdnungszahl > ${wahlvorschlagOrdnungszahl}`);
      return {
        type: WahlvorschlagEventTypeEnum.SELECT,
        wahlvorschlagOrdnungszahl,
      } as WahlvorschlagEvent;
    } else {
      logger.log("no match");
      return null;
    }
  };

  const handleUnsetExpression: AbstractExpressionHandlerFunction = (
    expression: string
  ) => {
    logger.log(`processing expression: ${expression}`);
    const match = REGEX_DESELECT_LISTE.exec(expression);

    if (match && match[1] !== undefined) {
      const wahlvorschlagOrdnungszahl = Number.parseInt(match[1]);
      logger.log(`wahlvorschlagOrdnungszahl > ${wahlvorschlagOrdnungszahl}`);
      return {
        type: WahlvorschlagEventTypeEnum.DESELECT,
        wahlvorschlagOrdnungszahl,
      } as WahlvorschlagEvent;
    } else {
      logger.log("no match");
      return null;
    }
  };

  return {
    handleSetExpression,
    handleUnsetExpression,
  };
}
