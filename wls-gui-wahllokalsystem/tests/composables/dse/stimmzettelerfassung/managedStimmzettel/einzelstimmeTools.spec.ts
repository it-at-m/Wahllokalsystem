import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useEinzelstimmeTools } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/einzelstimmeTools.ts";

describe("einzelstimmeTools.ts", () => {
  const { prepareManagedStimmzettelKandidat } =
    useManagedStimmzettelTestDataFactory();

  it("should_addVotes_accumulating_fromNull", () => {
    const kandidat = prepareManagedStimmzettelKandidat()
      .einzelstimmen(null)
      .build();

    const { addVotesToKandidat } = useEinzelstimmeTools();
    addVotesToKandidat(kandidat, 2);
    addVotesToKandidat(kandidat, 3);

    expect(kandidat.einzelstimmen).toBe(5);
  });

  it("should_removeVotes_subtracting_fromCurrentValue", () => {
    const kandidat = prepareManagedStimmzettelKandidat()
      .einzelstimmen(4)
      .build();

    const { removeVotesFromKandidat } = useEinzelstimmeTools();
    removeVotesFromKandidat(kandidat, 1);
    removeVotesFromKandidat(kandidat, 2);

    expect(kandidat.einzelstimmen).toBe(1);
  });
});
