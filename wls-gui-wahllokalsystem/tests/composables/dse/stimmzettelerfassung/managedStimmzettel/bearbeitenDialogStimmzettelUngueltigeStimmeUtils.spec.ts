import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useBearbeitenDialogStimmzettelUngueltigeStimmeUtils } from "@/composables/dse/stimmzettelerfassung/bearbeitenDialogStimmzettel/bearbeitenDialogStimmzettelUngueltigeStimmeUtils.ts";

describe("bearbeitenDialogStimmzettelUngueltigeStimmeUtils.ts", () => {
  const { prepareStimmzettelKandidat } = useStimmzettelTestDataFactory();

  describe("addInvalidVotesToKandidat", () => {
    it("should_addInvalidVotes_when_called", () => {
      const kandidat = prepareStimmzettelKandidat()
        .ungueltigeStimmen(null)
        .build();

      const { addInvalidVotesToKandidat } =
        useBearbeitenDialogStimmzettelUngueltigeStimmeUtils();
      addInvalidVotesToKandidat(kandidat, 3);

      expect(kandidat.ungueltigeStimmen).toBe(3);
    });
  });

  describe("removeInvalidVotesFromKandidat", () => {
    const { removeInvalidVotesFromKandidat } =
      useBearbeitenDialogStimmzettelUngueltigeStimmeUtils();

    it("should_removeInvalidVotes_when_called", () => {
      const kandidat = prepareStimmzettelKandidat()
        .ungueltigeStimmen(4)
        .build();

      removeInvalidVotesFromKandidat(kandidat, 2);

      expect(kandidat.ungueltigeStimmen).toBe(2);
    });

    it("should_setInvalidVotesToNull_when_newValueIsZero", () => {
      const kandidat = prepareStimmzettelKandidat()
        .ungueltigeStimmen(4)
        .build();

      removeInvalidVotesFromKandidat(kandidat, 4);

      expect(kandidat.ungueltigeStimmen).toBe(null);
    });

    it("should_setInvalidVotesToNull_when_newValueIsLowerThanZero", () => {
      const kandidat = prepareStimmzettelKandidat()
        .ungueltigeStimmen(4)
        .build();

      removeInvalidVotesFromKandidat(kandidat, 5);

      expect(kandidat.ungueltigeStimmen).toBe(null);
    });
  });
});
