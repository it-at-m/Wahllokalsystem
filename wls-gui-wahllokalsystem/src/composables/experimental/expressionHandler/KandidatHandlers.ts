import type { AbstractExpressionHandlerFunction } from "@/types/experimental/AbstractExpressionHandlerFunction.ts";
import type { KandidatEvent } from "@/types/experimental/KandidatEvent.ts";

import { useLogging } from "@/composables/common/logging.ts";
import { KandidatEventTypeEnum } from "@/types/experimental/KandidatEventTypeEnum.ts";

export function useKandidatHandlers() {
  const REGEX_SET_VOTES = /^(\d+)$/;
  const REGEX_ADD_VOTES = /^(\d+)\+(\d*)$/;
  const REGEX_REMOVE_VOTES = /^(\d+)\-(\d*)$/;
  const REGEX_DISCARD_KANDIDAT = /^(\d+)x$/;
  const REGEX_DISCARD_KANDIDAT_WITH_NUMBLOCK_ONLY = /^-(\d+)$/;
  const REGEX_REMOVE_DISCARD_KANDIDAT_WITH_NUMBLOCK_ONLY = /^\+(\d+)$/;

  const logger = useLogging("useKandidatHandlers");

  const handleSetVotesExpression: AbstractExpressionHandlerFunction = (
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
      return null;
    }
  };

  const handleAddVotesExpression: AbstractExpressionHandlerFunction = (
    expression: string
  ) => {
    logger.log(`processing expression > ${expression}`);
    const match = REGEX_ADD_VOTES.exec(expression);

    if (match && match[1] !== undefined) {
      const kandidatNummer = Number.parseInt(match[1]);
      const countVotes = match[2] ? Number.parseInt(match[2]) : 1;
      return {
        type: KandidatEventTypeEnum.ADD_VOTE,
        count: countVotes,
        kandidatNummer,
      } as KandidatEvent;
    } else {
      logger.log(`no match`);
      return null;
    }
  };

  const handleRemoveVotesExpression: AbstractExpressionHandlerFunction = (
    expression: string
  ) => {
    logger.log(`processing expression > ${expression}`);
    const match = REGEX_REMOVE_VOTES.exec(expression);

    if (match && match[1] !== undefined) {
      const kandidatNummer = Number.parseInt(match[1]);
      const countVotes = match[2] ? Number.parseInt(match[2]) : 1;
      return {
        type: KandidatEventTypeEnum.REMOVE_VOTE,
        count: countVotes,
        kandidatNummer,
      } as KandidatEvent;
    } else {
      logger.log(`no match`);
      return null;
    }
  };

  const handleDiscardKandidatExpression: AbstractExpressionHandlerFunction = (
    expression: string
  ) => {
    logger.log(`processing expression > ${expression}`);
    return (
      _handleDiscardKandidatExpression(expression) ||
      _handleDiscardKandidatExpressionWithNumblockOnly(expression)
    );
  };

  const handleRevokeDiscardKandidatExpression: AbstractExpressionHandlerFunction =
    (expression: string) => {
      logger.log(`processing expression > ${expression}`);
      return _handleRevokeDiscardKandidatExpressionWithNumblockOnly(expression);
    };

  const _handleDiscardKandidatExpression: AbstractExpressionHandlerFunction = (
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
      return null;
    }
  };

  const _handleDiscardKandidatExpressionWithNumblockOnly: AbstractExpressionHandlerFunction =
    (expression: string) => {
      logger.log(`processing expression > ${expression}`);
      const match = REGEX_DISCARD_KANDIDAT_WITH_NUMBLOCK_ONLY.exec(expression);

      if (match && match[1] !== undefined) {
        const kandidatNummer = Number.parseInt(match[1]);
        return {
          type: KandidatEventTypeEnum.DISCARD,
          kandidatNummer,
        } as KandidatEvent;
      } else {
        logger.log(`no match`);
        return null;
      }
    };

  const _handleRevokeDiscardKandidatExpressionWithNumblockOnly: AbstractExpressionHandlerFunction =
    (expression: string) => {
      logger.log(`processing expression > ${expression}`);
      const match =
        REGEX_REMOVE_DISCARD_KANDIDAT_WITH_NUMBLOCK_ONLY.exec(expression);

      if (match && match[1] !== undefined) {
        const kandidatNummer = Number.parseInt(match[1]);
        return {
          type: KandidatEventTypeEnum.REMOVE_DISCARD,
          kandidatNummer,
        } as KandidatEvent;
      } else {
        logger.log(`no match`);
        return null;
      }
    };

  return {
    handleAddVotesExpression,
    handleDiscardKandidatExpression,
    handleRevokeDiscardKandidatExpression,
    handleSetVotesExpression,
    handleRemoveVotesExpression,
  };
}
