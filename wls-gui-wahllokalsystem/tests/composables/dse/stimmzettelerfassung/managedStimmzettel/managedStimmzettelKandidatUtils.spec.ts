import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";
import { ref } from "vue";

import { useManagedStimmzettelKandidatUtils } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/managedStimmzettelKandidatUtils.ts";

describe("managedStimmzettelKandidatUtils.ts", () => {
  const {
    prepareManagedStimmzettelStimmzettel,
    prepareManagedStimmzettelWahlvorschlag,
    prepareManagedStimmzettelKandidat,
  } = useManagedStimmzettelTestDataFactory();

  describe("getKandidatToAddVotesByOrdnungszahl", () => {
    it("should_findKandidatByOrdnungszahl_when_called", () => {
      const kWithVotes = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .einzelstimmen(2)
        .durchgestrichen(false)
        .build();
      const kNotStruck = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();
      const kStruck = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(true)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kStruck, kNotStruck, kWithVotes])
            .build(),
        ])
        .build();

      const tools = useManagedStimmzettelKandidatUtils(ref(stimmzettel));
      expect(tools.getKandidatToAddVotesByOrdnungszahl(101)).toStrictEqual(
        kWithVotes
      );

      kWithVotes.einzelstimmen = null;
      expect(tools.getKandidatToAddVotesByOrdnungszahl(101)).toStrictEqual(
        kNotStruck
      );

      kNotStruck.durchgestrichen = true;
      kWithVotes.durchgestrichen = true;
      expect(tools.getKandidatToAddVotesByOrdnungszahl(101)).toStrictEqual(
        kStruck
      );
    });
  });

  describe("getKandidatToAddVotesForRangeByOrdnungszahl", () => {
    it("should_returnAllKandidatenInRange_when_called", () => {
      const k1 = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .nennung(2)
        .build();
      const k2 = prepareManagedStimmzettelKandidat().ordnungszahl(102).build();
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([k1, k2])
            .build(),
        ])
        .build();
      const tools = useManagedStimmzettelKandidatUtils(ref(stimmzettel));

      expect(tools.getKandidatToAddVotesForRangeByOrdnungszahl(101)).toEqual([
        k1,
      ]);
      expect(tools.getKandidatToAddVotesForRangeByOrdnungszahl(102)).toEqual([
        k2,
      ]);
      expect(
        tools.getKandidatToAddVotesForRangeByOrdnungszahl(103)
      ).toBeUndefined();
    });
  });

  describe("getKandidatForStreichungByOrdnungszahl", () => {
    it("should_findKandidatForStreichung_when_called", () => {
      const kWithVotes = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .einzelstimmen(1)
        .durchgestrichen(false)
        .build();
      const kNoVotes = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .einzelstimmen(null)
        .ungueltigeStimmen(null)
        .durchgestrichen(false)
        .build();
      const kFallback = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(true)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kFallback, kWithVotes, kNoVotes])
            .build(),
        ])
        .build();

      const tools = useManagedStimmzettelKandidatUtils(ref(stimmzettel));
      expect(tools.getKandidatForStreichungByOrdnungszahl(101)).toStrictEqual(
        kNoVotes
      );

      kNoVotes.durchgestrichen = true;
      expect(tools.getKandidatForStreichungByOrdnungszahl(101)).toStrictEqual(
        kWithVotes
      );
    });
  });

  describe("getKandidatToRemoveStreichungByOrdnungszahl", () => {
    it("should_findKandidatToRemoveStreichung_when_called", () => {
      const kStruck = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .durchgestrichen(true)
        .build();
      const kOther = prepareManagedStimmzettelKandidat()
        .ordnungszahl(101)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kOther, kStruck])
            .build(),
        ])
        .build();

      const tools = useManagedStimmzettelKandidatUtils(ref(stimmzettel));
      expect(
        tools.getKandidatToRemoveStreichungByOrdnungszahl(101)
      ).toStrictEqual(kStruck);

      kStruck.durchgestrichen = false;
      expect(
        tools.getKandidatToRemoveStreichungByOrdnungszahl(101)
      ).toStrictEqual(kOther);
    });
  });
});
