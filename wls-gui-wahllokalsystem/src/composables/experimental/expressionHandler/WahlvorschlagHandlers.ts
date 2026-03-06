import type { AbstractCommandEvent } from "@/types/experimental/AbstractCommandEvent.ts";
import type { AbstractExpressionHandler } from "@/types/experimental/AbstractExpressionHandler.ts";
import type { WahlvorschlagEvent } from "@/types/experimental/WahlvorschlagEvent.ts";

import { useLogging } from "@/composables/common/logging.ts";
import { WahlvorschlagEventTypeEnum } from "@/types/experimental/WahlvorschlagEventTypeEnum.ts";

export function useWahlvorschlagHandlers() {
  const REGEX = /^[W|w](\d+)$/;
  const logger = useLogging("useSetWahlvorschlagHandler");

  const handleSetExpression: AbstractExpressionHandler = (
    expression: string
  ) => {
    logger.log(`processing expression: ${expression}`);
    const match = REGEX.exec(expression);

    if (match && match[1] !== undefined) {
      const wahlvorschlagOrdnungszahl = Number.parseInt(match[1]);
      logger.log(`wahlvorschlagOrdnungszahl > ${wahlvorschlagOrdnungszahl}`);
      return {
        type: WahlvorschlagEventTypeEnum.SELECT,
        wahlvorschlagOrdnungszahl,
      } as WahlvorschlagEvent;
    } else {
      logger.log("no match");
    }

    return null;
  };

  return {
    handleSetExpression,
  };
}
