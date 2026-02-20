import type { MessageDTO } from "@/api/wls-clients/generated-broadcast-api";
import type { BroadcastMessage } from "@/types/broadcast/broadcastMessage.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomDateTimeAsString, generateRandomNumber } =
  useCommonTestDataFactory();

export function useBroadcastTestDataFactory() {
  function createMessageDTO(): MessageDTO {
    return {
      empfangsZeit: generateRandomDateTimeAsString(),
      oid: `${generateRandomNumber(10)}`,
      nachricht: `${generateRandomNumber(40)}`,
      wahlbezirkID: `${generateRandomNumber(10)}`,
    };
  }

  function createBroadcastMessage(): BroadcastMessage {
    return {
      empfangsZeit: new Date(),
      id: `${generateRandomNumber(10)}`,
      nachricht: `${generateRandomNumber(40)}`,
    };
  }

  function prepareMessageDTO(): Builder<MessageDTO> {
    return proxyBuilder<MessageDTO>(createMessageDTO());
  }

  return {
    createBroadcastMessage,
    createMessageDTO,
    prepareMessageDTO,
  };
}
