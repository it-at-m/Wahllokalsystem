import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useKandidatTools } from "@/composables/dse/stimmzettelerfassung/kandidatTools.ts";

const { prepareStimmzettelKandidat } = useStimmzettelTestDataFactory();

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
        .ungueltigeStimmen(null)
        .build();

      expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(true);
    });

    it("should_returnTrue_when_isDurchgestrichenAndAllStimmenLargerThan0", () => {
      const kandidat = prepareStimmzettelKandidat()
        .durchgestrichen(true)
        .einzelstimmen(1)
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
          .ungueltigeStimmen(listenstimmen)
          .build();

        expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(true);
      }
    );

    it("should_returnFalse_when_isNotDurchgestrichenAndAllStimmenAreNull", () => {
      const kandidat = prepareStimmzettelKandidat()
        .durchgestrichen(false)
        .einzelstimmen(null)
        .ungueltigeStimmen(null)
        .build();

      expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(false);
    });

    it("should_returnFalse_when_isNotDurchgestrichenAndAllStimmenAre0", () => {
      const kandidat = prepareStimmzettelKandidat()
        .durchgestrichen(false)
        .einzelstimmen(0)
        .ungueltigeStimmen(0)
        .build();

      expect(unitUnderTest.hasAnyKennzeichen(kandidat)).toStrictEqual(false);
    });
  });

  describe("hasAnyKennzeichenOrReststimme", () => {
    it("should_returnTrue_when_durchgestrichenIsTrue", () => {
      const kandidat = prepareStimmzettelKandidat()
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
      const kandidat = prepareStimmzettelKandidat()
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
        const kandidat = prepareStimmzettelKandidat()
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
        const kandidat = prepareStimmzettelKandidat()
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
        const kandidat = prepareStimmzettelKandidat()
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
      const kandidat = prepareStimmzettelKandidat()
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
      const kandidat = prepareStimmzettelKandidat()
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
});
