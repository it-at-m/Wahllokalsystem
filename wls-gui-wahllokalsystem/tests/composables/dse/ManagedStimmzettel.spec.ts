import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useManagedStimmzettel } from "@/composables/dse/ManagedStimmzettel.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

describe("ManagedStimmzettel.ts", () => {
  const {
    prepareManagedStimmzettelStimmzettel,
    prepareManagedStimmzettelWahlvorschlag,
    prepareManagedStimmzettelKandidat,
  } = useManagedStimmzettelTestDataFactory();

  describe("kandidatAddVotesOrThrow", () => {
    it("should_useKandidatWithUserVotes_when_multipleCandidatesShareListenposition", () => {
      const kandidatWithoutVotes = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .votesByVoter(null)
        .isDiscarded(false)
        .build();
      const kandidatWithVotes = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .votesByVoter(2)
        .isDiscarded(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidatWithoutVotes, kandidatWithVotes])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(stimmzettel);

      managed.kandidatAddVotesOrThrow(101, 1);

      expect(kandidatWithVotes.votesByVoter).toBe(3);
      expect(kandidatWithoutVotes.votesByVoter).toBeNull();
    });

    it("should_useFirstNotDiscardedCandidate_when_noUserVotesPresent", () => {
      const discardedKandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .votesByVoter(null)
        .isDiscarded(true)
        .build();
      const kandidatToUse = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .votesByVoter(null)
        .isDiscarded(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([discardedKandidat, kandidatToUse])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(stimmzettel);

      managed.kandidatAddVotesOrThrow(101, 1);

      expect(kandidatToUse.votesByVoter).toBe(1);
      expect(discardedKandidat.votesByVoter).toBeNull();
    });

    it("should_useFirstCandidate_when_allCandidatesAreDiscarded", () => {
      const firstDiscarded = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .votesByVoter(null)
        .isDiscarded(true)
        .build();
      const secondDiscarded = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .votesByVoter(null)
        .isDiscarded(true)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([firstDiscarded, secondDiscarded])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(stimmzettel);

      managed.kandidatAddVotesOrThrow(101, 1);

      expect(firstDiscarded.votesByVoter).toBe(1);
      expect(secondDiscarded.votesByVoter).toBeNull();
    });

    it("should_throwManagedStimmzettelError_when_noKandidatForGivenOrdnungszahl", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([])
        .build();

      const managed = useManagedStimmzettel(stimmzettel);

      expect(() => managed.kandidatAddVotesOrThrow(101, 1)).toThrow(
        ManagedStimmzettelError
      );
    });

    it("should_addAbsoluteNumberOfVotes_when_votesToAddIsNegative", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .votesByVoter(null)
        .isDiscarded(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidat])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(stimmzettel);

      managed.kandidatAddVotesOrThrow(101, -3);

      expect(kandidat.votesByVoter).toBe(3);
    });

    it("should_incrementVotes_when_addVotesMultipleTimes", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .votesByVoter(1)
        .isDiscarded(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidat])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(stimmzettel);

      managed.kandidatAddVotesOrThrow(101, 2);
      managed.kandidatAddVotesOrThrow(101, 3);

      expect(kandidat.votesByVoter).toBe(6);
    });

    it("should_useKandidatFromSecondWahlvorschlag_when_ordnungszahlPointsToIt", () => {
      const kandidatInFirst = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .votesByVoter(null)
        .isDiscarded(false)
        .build();
      const kandidatInSecond = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .votesByVoter(null)
        .isDiscarded(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidatInFirst])
            .build(),
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(2)
            .kandidaten([kandidatInSecond])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(stimmzettel);

      managed.kandidatAddVotesOrThrow(201, 1);

      expect(kandidatInSecond.votesByVoter).toBe(1);
      expect(kandidatInFirst.votesByVoter).toBeNull();
    });

    it("should_throwManagedStimmzettelError_when_listenpositionHasNoCandidate", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .listenposition(1)
        .votesByVoter(null)
        .isDiscarded(false)
        .build();

      const stimmzettel = prepareManagedStimmzettelStimmzettel()
        .wahlvorschlaege([
          prepareManagedStimmzettelWahlvorschlag()
            .ordnungszahl(1)
            .kandidaten([kandidat])
            .build(),
        ])
        .build();

      const managed = useManagedStimmzettel(stimmzettel);

      expect(() => managed.kandidatAddVotesOrThrow(199, 1)).toThrow(
        ManagedStimmzettelError
      );
    });
  });

  describe("publicApi", () => {
    it("should_exposePublicApiAndStimmzettel_when_useManagedStimmzettelIsCalled", () => {
      const stimmzettel = prepareManagedStimmzettelStimmzettel().build();

      const managed = useManagedStimmzettel(stimmzettel);

      expect(typeof managed.kandidatAddVotesOrThrow).toBe("function");
      expect(managed.stimmzettel).toBe(stimmzettel);
    });
  });
});
