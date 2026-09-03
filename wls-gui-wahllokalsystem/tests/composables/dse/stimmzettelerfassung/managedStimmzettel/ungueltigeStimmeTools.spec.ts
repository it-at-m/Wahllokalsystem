import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useUngueltigeStimmeTools } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/ungueltigeStimmeTools.ts";

describe("ungueltigeStimmeTools.ts", () => {
  const { prepareManagedStimmzettelKandidat } =
    useManagedStimmzettelTestDataFactory();

  it("should_addInvalidVotes_accumulating_fromNull", () => {
    const kandidat = prepareManagedStimmzettelKandidat()
      .ungueltigeStimmen(null)
      .build();

    const { addInvalidVotesToKandidat } = useUngueltigeStimmeTools();
    addInvalidVotesToKandidat(kandidat, 2);
    addInvalidVotesToKandidat(kandidat, 3);

    expect(kandidat.ungueltigeStimmen).toBe(5);
  });

  it("should_removeInvalidVotes_subtracting_fromCurrentValue", () => {
    const kandidat = prepareManagedStimmzettelKandidat()
      .ungueltigeStimmen(4)
      .build();

    const { removeInvalidVotesFromKandidat } = useUngueltigeStimmeTools();
    removeInvalidVotesFromKandidat(kandidat, 1);
    removeInvalidVotesFromKandidat(kandidat, 2);

    expect(kandidat.ungueltigeStimmen).toBe(1);
  });
});
