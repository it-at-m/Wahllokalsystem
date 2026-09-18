import { useDseStimmzettelTestDataFactory } from "@tests/utils/dse/DseStimmzettelTestDataFactory.ts";
import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useKandidatTools } from "@/composables/dse/stimmzettelerfassung/kandidatTools.ts";

const {
  createDseWahlvorschlag,
  prepareDseKandidat,
  prepareDseKandidatOfDseWahlvorschlag,
} = useDseStimmzettelTestDataFactory();

const { preparePersistedStimmzettelKandidat } =
  usePersistedStimmzettelTestDataFactory();

describe("kandidatTools.ts", () => {
  let unitUnderTest: ReturnType<typeof useKandidatTools>;

  beforeEach(() => {
    unitUnderTest = useKandidatTools();
  });

  describe("hasAnyKennzeichen", () => {
    it("should_returnTrue_when_durchgestrichenIsTrue", () => {
      const kandidat = prepareDseKandidat()
        .durchgestrichen(true)
        .einzelstimmen(null)
        .ungueltigeStimmen(null)
        .build();

      expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(true);
    });

    it("should_returnTrue_when_isDurchgestrichenAndAllStimmenLargerThan0", () => {
      const kandidat = prepareDseKandidat()
        .durchgestrichen(true)
        .einzelstimmen(1)
        .ungueltigeStimmen(1)
        .build();

      expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(true);
    });

    it.each([1, 10])(
      "should_returnTrue_when_einzelstimmenIsLargerThan0By'%d'",
      (einzelstimmen) => {
        const kandidat = prepareDseKandidat()
          .durchgestrichen(false)
          .einzelstimmen(einzelstimmen)
          .ungueltigeStimmen(null)
          .build();

        expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(true);
      }
    );

    it.each([1, 10])(
      "should_returnTrue_when_ungueltigeStimmenIsLargerThan0By'%d'",
      (listenstimmen) => {
        const kandidat = prepareDseKandidat()
          .durchgestrichen(false)
          .einzelstimmen(null)
          .ungueltigeStimmen(listenstimmen)
          .build();

        expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(true);
      }
    );

    it("should_returnFalse_when_isNotDurchgestrichenAndAllStimmenAreNull", () => {
      const kandidat = prepareDseKandidat()
        .durchgestrichen(false)
        .einzelstimmen(null)
        .ungueltigeStimmen(null)
        .build();

      expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(false);
    });

    it("should_returnFalse_when_isNotDurchgestrichenAndAllStimmenAre0", () => {
      const kandidat = prepareDseKandidat()
        .durchgestrichen(false)
        .einzelstimmen(0)
        .ungueltigeStimmen(0)
        .build();

      expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(false);
    });
  });

  describe("hasAnyKennzeichenOrReststimme", () => {
    it("should_returnTrue_when_durchgestrichenIsTrue", () => {
      const kandidat = prepareDseKandidat()
        .durchgestrichen(true)
        .einzelstimmen(null)
        .reststimmen(null)
        .ungueltigeStimmen(null)
        .build();

      expect(
        unitUnderTest.hasAnyKennzeichenOrReststimme(kandidat)
      ).toStrictEqual(true);
    });

    it("should_returnTrue_when_isDurchgestrichenAndAllStimmenLargerThan0", () => {
      const kandidat = prepareDseKandidat()
        .durchgestrichen(true)
        .einzelstimmen(1)
        .reststimmen(1)
        .ungueltigeStimmen(1)
        .build();

      expect(
        unitUnderTest.hasAnyKennzeichenOrReststimme(kandidat)
      ).toStrictEqual(true);
    });

    it.each([1, 10])(
      "should_returnTrue_when_einzelstimmenIsLargerThan0By'%d'",
      (einzelstimmen) => {
        const kandidat = prepareDseKandidat()
          .durchgestrichen(false)
          .einzelstimmen(einzelstimmen)
          .reststimmen(null)
          .ungueltigeStimmen(null)
          .build();

        expect(
          unitUnderTest.hasAnyKennzeichenOrReststimme(kandidat)
        ).toStrictEqual(true);
      }
    );

    it.each([1, 10])(
      "should_returnTrue_when_reststimmenIsLargerThan0By'%d'",
      (reststimmen) => {
        const kandidat = prepareDseKandidat()
          .durchgestrichen(false)
          .einzelstimmen(null)
          .reststimmen(reststimmen)
          .ungueltigeStimmen(null)
          .build();

        expect(
          unitUnderTest.hasAnyKennzeichenOrReststimme(kandidat)
        ).toStrictEqual(true);
      }
    );

    it.each([1, 10])(
      "should_returnTrue_when_ungueltigeStimmenIsLargerThan0By'%d'",
      (listenstimmen) => {
        const kandidat = prepareDseKandidat()
          .durchgestrichen(false)
          .einzelstimmen(null)
          .reststimmen(null)
          .ungueltigeStimmen(listenstimmen)
          .build();

        expect(
          unitUnderTest.hasAnyKennzeichenOrReststimme(kandidat)
        ).toStrictEqual(true);
      }
    );

    it("should_returnFalse_when_isNotDurchgestrichenAndAllStimmenAreNull", () => {
      const kandidat = prepareDseKandidat()
        .durchgestrichen(false)
        .einzelstimmen(null)
        .reststimmen(null)
        .ungueltigeStimmen(null)
        .build();

      expect(
        unitUnderTest.hasAnyKennzeichenOrReststimme(kandidat)
      ).toStrictEqual(false);
    });

    it("should_returnFalse_when_isNotDurchgestrichenAndAllStimmenAre0", () => {
      const kandidat = prepareDseKandidat()
        .durchgestrichen(false)
        .einzelstimmen(0)
        .reststimmen(0)
        .ungueltigeStimmen(0)
        .build();

      expect(
        unitUnderTest.hasAnyKennzeichenOrReststimme(kandidat)
      ).toStrictEqual(false);
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
        const kandidat = prepareDseKandidat()
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
        const kandidat = prepareDseKandidat()
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
      const wahlvorschlag = createDseWahlvorschlag();
      const firstKandidat = prepareDseKandidatOfDseWahlvorschlag(wahlvorschlag)
        .kandidatId("same-kandidat-id")
        .einzelstimmen(2)
        .build();
      const secondKandidat = prepareDseKandidatOfDseWahlvorschlag(wahlvorschlag)
        .kandidatId("same-kandidat-id")
        .einzelstimmen(null)
        .build();
      const thirdKandidat = prepareDseKandidatOfDseWahlvorschlag(wahlvorschlag)
        .kandidatId("different-kandidat-id")
        .einzelstimmen(3)
        .build();
      const fourthKandidat = prepareDseKandidatOfDseWahlvorschlag(wahlvorschlag)
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
      const wahlvorschlag = createDseWahlvorschlag();
      const firstKandidat = prepareDseKandidatOfDseWahlvorschlag(wahlvorschlag)
        .kandidatId("same-kandidat-id")
        .einzelstimmen(2)
        .ungueltigeStimmen(null)
        .build();
      const secondKandidat = prepareDseKandidatOfDseWahlvorschlag(wahlvorschlag)
        .kandidatId("same-kandidat-id")
        .einzelstimmen(null)
        .ungueltigeStimmen(1)
        .build();
      const thirdKandidat = prepareDseKandidatOfDseWahlvorschlag(wahlvorschlag)
        .kandidatId("different-kandidat-id")
        .build();
      const fourthKandidat = prepareDseKandidatOfDseWahlvorschlag(wahlvorschlag)
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

  describe("sortAndDeepCloneKandidaten", () => {
    const kdA1 = preparePersistedStimmzettelKandidat()
      .kandidatId("a")
      .nennung(1)
      .build();
    const kdA2 = preparePersistedStimmzettelKandidat()
      .kandidatId("a")
      .nennung(2)
      .build();
    const kdB = preparePersistedStimmzettelKandidat()
      .kandidatId("b")
      .nennung(1)
      .build();

    it.each([
      { text: "NotSorted", kandidaten: [kdB, kdA2, kdA1] },
      { text: "Sorted", kandidaten: [kdA1, kdA2, kdB] },
    ])(
      `should_returnSortedAndClonedKandidaten_when_givenListOfKandidatenThatIs'$text'`,
      ({ kandidaten }) => {
        const expectedResult = [kdA1, kdA2, kdB];
        const arrayBeforeSort = kandidaten.slice();

        const result = unitUnderTest.sortAndDeepCloneKandidaten(kandidaten);

        expect(result).not.toBe(kandidaten);
        expect(result).toStrictEqual(expectedResult);
        expect(kandidaten).toStrictEqual(arrayBeforeSort);
      }
    );

    it("should_returnEmptyList_when_givenEmptyList", () => {
      const result = unitUnderTest.sortAndDeepCloneKandidaten([]);

      expect(result).toStrictEqual([]);
    });
  });
});
