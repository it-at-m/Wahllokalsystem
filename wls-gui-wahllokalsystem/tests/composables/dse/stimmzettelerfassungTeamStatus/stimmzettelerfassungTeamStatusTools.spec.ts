import { describe, expect, it } from "vitest";

import { isTeamStatusBeingWritten } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusTools.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";

describe("stimmzettelerfassungTeamStatusTools.ts", () => {
  describe("isTeamStatusBeingWritten", () => {
    it("should_returnTrue_when_statusBeingWrittenMatchesStatus", () => {
      const result = isTeamStatusBeingWritten(
        StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG,
        StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG
      );

      expect(result).toStrictEqual(true);
    });

    it("should_returnFalse_when_statusBeingWrittenDoesNotMatchStatus", () => {
      const result = isTeamStatusBeingWritten(
        StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN,
        StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG
      );

      expect(result).toStrictEqual(false);
    });

    it("should_returnTrue_when_anotherStatusIsBeingWritten", () => {
      const result = isTeamStatusBeingWritten(
        StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN,
        StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG,
        false
      );

      expect(result).toStrictEqual(true);
    });

    it("should_returnFalse_when_noStatusIsBeingWrittenAndAnotherStatusIsChecked", () => {
      const result = isTeamStatusBeingWritten(
        null,
        StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG,
        false
      );

      expect(result).toStrictEqual(false);
    });
  });
});
