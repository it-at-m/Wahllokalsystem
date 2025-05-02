import type { BroadcastMessage } from "@/types/broadcast/broadcastMessage.ts";

import { useBroadcastTestDataFactory } from "@tests/utils/broadcast/BroadcastTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useBroadcastMapper } from "@/composables/broadcast/broadcastMapper.ts";

const { prepareMessageDTO } = useBroadcastTestDataFactory();

describe("broadcastMapper.ts", () => {
  const { dtoToModel } = useBroadcastMapper();

  describe("dtoToModel", () => {
    it("should_returnBroadcastMessage_when_dtoIsGiven", () => {
      const empfangszeit = "2025-04-28T13:20:11";
      const dtoToMap = prepareMessageDTO().empfangsZeit(empfangszeit).build();

      const result = dtoToModel(dtoToMap);

      const expectedResult: BroadcastMessage = {
        empfangsZeit: new Date(empfangszeit),
        nachricht: dtoToMap.nachricht,
        id: dtoToMap.oid,
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });
});
