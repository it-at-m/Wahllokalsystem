import type { MessageDTO } from "@/api/wls-clients/generated-broadcast-api";
import type { BroadcastMessage } from "@/types/broadcast/broadcastMessage.ts";

export function useBroadcastMapper() {
  function dtoToModel(dto: MessageDTO): BroadcastMessage {
    return {
      empfangsZeit: new Date(dto.empfangsZeit),
      nachricht: dto.nachricht,
      id: dto.oid,
    };
  }

  return {
    dtoToModel,
  };
}
