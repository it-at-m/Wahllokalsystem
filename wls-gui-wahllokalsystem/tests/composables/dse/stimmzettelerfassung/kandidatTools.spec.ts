import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useKandidatTools } from "@/composables/dse/stimmzettelerfassung/kandidatTools.ts";

const {
  createStimmzettelWahlvorschlag,
  prepareStimmzettelKandidat,
  prepareStimmzettelKandidatOfWahlvorschlag,
} = useStimmzettelTestDataFactory();

describe("kandidatTools.ts", () => {
  let unitUnderTest: ReturnType<typeof useKandidatTools>;

  beforeEach(() => {
    unitUnderTest = useKandidatTools();
  });

  describe("hasAnyKennzeichen", () => {
    it("should_returnTrue_when_durchgestrichenIsTrue", () => {
      const kandidat = prepareStimmzettelKandidat()
        .durchgestrichen(true)
        .einzelstimmen(null)
        .reststimmen(null)
        .ungueltigeStimmen(null)
        .build();

      expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(true);
    });

    it("should_returnTrue_when_isDurchgestrichenAndAllStimmenLargerThan0", () => {
      const kandidat = prepareStimmzettelKandidat()
        .durchgestrichen(true)
        .einzelstimmen(1)
        .reststimmen(1)
        .ungueltigeStimmen(1)
        .build();

      expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(true);
    });

    it.each([1, 10])(
      "should_returnTrue_when_einzelstimmenIsLargerThan0By'%d'",
      (einzelstimmen) => {
        const kandidat = prepareStimmzettelKandidat()
          .durchgestrichen(false)
          .einzelstimmen(einzelstimmen)
          .reststimmen(null)
          .ungueltigeStimmen(null)
          .build();

        expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(true);
      }
    );

    it.each([1, 10])(
      "should_returnTrue_when_reststimmenIsLargerThan0By'%d'",
      (reststimmen) => {
        const kandidat = prepareStimmzettelKandidat()
          .durchgestrichen(false)
          .einzelstimmen(null)
          .reststimmen(reststimmen)
          .ungueltigeStimmen(null)
          .build();

        expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(true);
      }
    );

    it.each([1, 10])(
      "should_returnTrue_when_ungueltigeStimmenIsLargerThan0By'%d'",
      (listenstimmen) => {
        const kandidat = prepareStimmzettelKandidat()
          .durchgestrichen(false)
          .einzelstimmen(null)
          .reststimmen(null)
          .ungueltigeStimmen(listenstimmen)
          .build();

        expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(true);
      }
    );

    it("should_returnFalse_when_isNotDurchgestrichenAndAllStimmenAreNull", () => {
      const kandidat = prepareStimmzettelKandidat()
        .durchgestrichen(false)
        .einzelstimmen(null)
        .reststimmen(null)
        .ungueltigeStimmen(null)
        .build();

      expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(false);
    });

    it("should_returnFalse_when_isNotDurchgestrichenAndAllStimmenAre0", () => {
      const kandidat = prepareStimmzettelKandidat()
        .durchgestrichen(false)
        .einzelstimmen(0)
        .reststimmen(0)
        .ungueltigeStimmen(0)
        .build();

      expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(false);
    });
  });

  describe("getEinzelstimmenOrZero", () => {
    it.each([
      [null, 0],
      [0, 0],
      [4, 4],
    ])(
      "should_return'%d'_when_einzelstimmenIs'%d'",
      (einzelstimmen, expectedResult) => {
        const kandidat = prepareStimmzettelKandidat()
          .einzelstimmen(einzelstimmen)
          .build();

        expect(unitUnderTest.getEinzelstimmenOrZero(kandidat)).toStrictEqual(
          expectedResult
        );
      }
    );
  });

  describe("getUngueltigeStimmenOrZero", () => {
    it.each([
      [null, 0],
      [0, 0],
      [4, 4],
    ])(
      "should_return'%d'_when_ungueltigeStimmenIs'%d'",
      (ungueltigeStimmen, expectedResult) => {
        const kandidat = prepareStimmzettelKandidat()
          .ungueltigeStimmen(ungueltigeStimmen)
          .build();

        expect(
          unitUnderTest.getUngueltigeStimmenOrZero(kandidat)
        ).toStrictEqual(expectedResult);
      }
    );
  });

  describe("getTotalEinzelstimmenOfKandidatenWithSameId", () => {
    it("should_returnTotalOfKandidatenEinzelstimmenWithSameId_when_wahlvorschlagContainsDifferentKandidaten", () => {
      const wahlvorschlag = createStimmzettelWahlvorschlag();
      const firstKandidat = prepareStimmzettelKandidatOfWahlvorschlag(
        wahlvorschlag
      )
        .kandidatId("same-kandidat-id")
        .einzelstimmen(2)
        .build();
      const secondKandidat = prepareStimmzettelKandidatOfWahlvorschlag(
        wahlvorschlag
      )
        .kandidatId("same-kandidat-id")
        .einzelstimmen(null)
        .build();
      const thirdKandidat = prepareStimmzettelKandidatOfWahlvorschlag(
        wahlvorschlag
      )
        .kandidatId("different-kandidat-id")
        .einzelstimmen(3)
        .build();
      const fourthKandidat = prepareStimmzettelKandidatOfWahlvorschlag(
        wahlvorschlag
      )
        .kandidatId("same-kandidat-id")
        .einzelstimmen(4)
        .build();
      wahlvorschlag.kandidaten = [
        firstKandidat,
        secondKandidat,
        thirdKandidat,
        fourthKandidat,
      ];

      expect(
        unitUnderTest.getTotalEinzelstimmenOfKandidatenWithSameId(firstKandidat)
      ).toStrictEqual(6);
    });
  });

  describe("getTotalEinzelAndUngueltigeStimmenOfKandidatenWithSameId", () => {
    it("should_returnTotalOfKandidatenWithSameId_when_wahlvorschlagContainsDifferentKandidaten", () => {
      const wahlvorschlag = createStimmzettelWahlvorschlag();
      const firstKandidat = prepareStimmzettelKandidatOfWahlvorschlag(
        wahlvorschlag
      )
        .kandidatId("same-kandidat-id")
        .einzelstimmen(2)
        .ungueltigeStimmen(null)
        .build();
      const secondKandidat = prepareStimmzettelKandidatOfWahlvorschlag(
        wahlvorschlag
      )
        .kandidatId("same-kandidat-id")
        .einzelstimmen(null)
        .ungueltigeStimmen(1)
        .build();
      const thirdKandidat = prepareStimmzettelKandidatOfWahlvorschlag(
        wahlvorschlag
      )
        .kandidatId("different-kandidat-id")
        .build();
      const fourthKandidat = prepareStimmzettelKandidatOfWahlvorschlag(
        wahlvorschlag
      )
        .kandidatId("same-kandidat-id")
        .einzelstimmen(4)
        .ungueltigeStimmen(3)
        .build();
      wahlvorschlag.kandidaten = [
        firstKandidat,
        secondKandidat,
        thirdKandidat,
        fourthKandidat,
      ];

      expect(
        unitUnderTest.getTotalEinzelAndUngueltigeStimmenOfKandidatenWithSameId(
          firstKandidat
        )
      ).toStrictEqual(10);
    });
  });
});
