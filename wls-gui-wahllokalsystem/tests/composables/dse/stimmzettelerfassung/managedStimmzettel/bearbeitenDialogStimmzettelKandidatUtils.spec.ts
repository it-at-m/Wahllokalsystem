import { useDseStimmzettelTestDataFactory } from "@tests/utils/dse/DseStimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";
import { ref } from "vue";

import { useBearbeitenDialogStimmzettelKandidatUtils } from "@/composables/dse/stimmzettelerfassung/bearbeitenDialogStimmzettel/bearbeitenDialogStimmzettelKandidatUtils.ts";

describe("bearbeitenDialogStimmzettelKandidatUtils.ts", () => {
  const { prepareDseKandidat, prepareDseStimmzettel, prepareDseWahlvorschlag } =
    useDseStimmzettelTestDataFactory();

  describe("getKandidatToAddVotesByOrdnungszahl", () => {
    it("should_findKandidatByOrdnungszahl_when_called", () => {
      const kWithVotes = prepareDseKandidat()
        .ordnungszahl(101)
        .nennung(3)
        .einzelstimmen(2)
        .durchgestrichen(false)
        .build();
      const kNotStruck = prepareDseKandidat()
        .ordnungszahl(101)
        .nennung(2)
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();
      const kStruck = prepareDseKandidat()
        .ordnungszahl(101)
        .nennung(1)
        .einzelstimmen(null)
        .durchgestrichen(true)
        .build();

      const stimmzettel = prepareDseStimmzettel()
        .wahlvorschlaege([
          prepareDseWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kStruck, kNotStruck, kWithVotes])
            .build(),
        ])
        .build();

      const tools = useBearbeitenDialogStimmzettelKandidatUtils(
        ref(stimmzettel)
      );
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

    it("should_reuseFirstNennung_when_multipleNennungenHaveZeroEinzelstimmen", () => {
      const kandidatNennung3 = prepareDseKandidat()
        .ordnungszahl(101)
        .nennung(3)
        .einzelstimmen(0)
        .durchgestrichen(false)
        .build();
      const kandidatNennung1 = prepareDseKandidat()
        .ordnungszahl(101)
        .nennung(1)
        .einzelstimmen(0)
        .durchgestrichen(false)
        .build();
      const kandidatNennung2 = prepareDseKandidat()
        .ordnungszahl(101)
        .nennung(2)
        .einzelstimmen(0)
        .durchgestrichen(false)
        .build();
      const stimmzettel = prepareDseStimmzettel()
        .wahlvorschlaege([
          prepareDseWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidatNennung3, kandidatNennung1, kandidatNennung2])
            .build(),
        ])
        .build();
      const tools = useBearbeitenDialogStimmzettelKandidatUtils(
        ref(stimmzettel)
      );

      expect(tools.getKandidatToAddVotesByOrdnungszahl(101)).toStrictEqual(
        kandidatNennung1
      );

      kandidatNennung1.einzelstimmen = 1;
      expect(tools.getKandidatToAddVotesByOrdnungszahl(101)).toStrictEqual(
        kandidatNennung1
      );

      kandidatNennung1.einzelstimmen = null;
      expect(tools.getKandidatToAddVotesByOrdnungszahl(101)).toStrictEqual(
        kandidatNennung1
      );
    });
  });

  describe("getKandidatToAddVotesForRangeByOrdnungszahl", () => {
    it("should_returnAllKandidatenInRange_when_called", () => {
      const k1 = prepareDseKandidat().ordnungszahl(101).nennung(2).build();
      const k2 = prepareDseKandidat().ordnungszahl(102).build();
      const stimmzettel = prepareDseStimmzettel()
        .wahlvorschlaege([
          prepareDseWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([k1, k2])
            .build(),
        ])
        .build();
      const tools = useBearbeitenDialogStimmzettelKandidatUtils(
        ref(stimmzettel)
      );

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
      const kWithVotes = prepareDseKandidat()
        .ordnungszahl(101)
        .einzelstimmen(1)
        .durchgestrichen(false)
        .build();
      const kNoVotes = prepareDseKandidat()
        .ordnungszahl(101)
        .einzelstimmen(null)
        .ungueltigeStimmen(null)
        .durchgestrichen(false)
        .build();
      const kFallback = prepareDseKandidat()
        .ordnungszahl(101)
        .einzelstimmen(null)
        .durchgestrichen(true)
        .build();

      const stimmzettel = prepareDseStimmzettel()
        .wahlvorschlaege([
          prepareDseWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kFallback, kWithVotes, kNoVotes])
            .build(),
        ])
        .build();

      const tools = useBearbeitenDialogStimmzettelKandidatUtils(
        ref(stimmzettel)
      );
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
      const kStruck = prepareDseKandidat()
        .ordnungszahl(101)
        .durchgestrichen(true)
        .build();
      const kOther = prepareDseKandidat()
        .ordnungszahl(101)
        .durchgestrichen(false)
        .build();

      const stimmzettel = prepareDseStimmzettel()
        .wahlvorschlaege([
          prepareDseWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kOther, kStruck])
            .build(),
        ])
        .build();

      const tools = useBearbeitenDialogStimmzettelKandidatUtils(
        ref(stimmzettel)
      );
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
