import type { AbstractExpressionHandler } from "@/types/experimental/AbstractExpressionHandler.ts";
import type { KandidatEvent } from "@/types/experimental/KandidatEvent.ts";

import { useLogging } from "@/composables/common/logging.ts";
import { KandidatEventTypeEnum } from "@/types/experimental/KandidatEventTypeEnum.ts";

export function useKandidatHandlers() {
  const REGEX_SET_VOTES = /^(\d+)$/;
  const REGEX_ADD_VOTES = /^(\d+)\+(\d*)$/;
  const REGEX_DISCARD_KANDIDAT = /^(\d+)x$/;

  const logger = useLogging("useKandidatHandlers");

  const handleSetVotesExpression: AbstractExpressionHandler = (
    expression: string
  ) => {
    logger.log(`processing expression > ${expression}`);
    const match = REGEX_SET_VOTES.exec(expression);

    if (match && match[1] !== undefined) {
      const kandidatNummer = Number.parseInt(match[1]);
      return {
        type: KandidatEventTypeEnum.SET_VOTE,
        count: 1,
        kandidatNummer,
      } as KandidatEvent;
    } else {
      logger.log(`no match`);
    }
  };

  const handleAddVotesExpression: AbstractExpressionHandler = (
    expression: string
  ) => {
    logger.log(`processing expression > ${expression}`);
    const match = REGEX_ADD_VOTES.exec(expression);

    if (match && match[1] !== undefined) {
      const kandidatNummer = Number.parseInt(match[1]);
      const countVotes = match[2] ? Number.parseInt(match[2]) : 0;
      return {
        type: KandidatEventTypeEnum.ADD_VOTE,
        count: countVotes,
        kandidatNummer,
      } as KandidatEvent;
    } else {
      logger.log(`no match`);
    }
  };

  const handleDiscardKandidatExpression: AbstractExpressionHandler = (
    expression: string
  ) => {
    logger.log(`processing expression > ${expression}`);
    const match = REGEX_DISCARD_KANDIDAT.exec(expression);

    if (match && match[1] !== undefined) {
      const kandidatNummer = Number.parseInt(match[1]);
      return {
        type: KandidatEventTypeEnum.DISCARD,
        kandidatNummer,
      } as KandidatEvent;
    } else {
      logger.log(`no match`);
    }
  };

  return {
    handleAddVotesExpression,
    handleDiscardKandidatExpression,
    handleSetVotesExpression,
  };
}
