import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { createKandidat, prepareKandidat, prepareWahlvorschlag } =
  useWahlvorschlaegeTestDataFactory();
const { generateRandomNumber, generateRandomString } =
  useCommonTestDataFactory();
const { prepareWahlvorschlaege } = useWahlvorschlaegeTestDataFactory();

describe("wahlvorschlagUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlvorschlagUtils>;

  beforeEach(() => {
    unitUnderTest = useWahlvorschlagUtils();
  });

  describe("compareKandidatenByListenPosition", () => {
    it("should_returnEqual_when_bothObjectsAreSame", () => {
      const kandidat = createKandidat();

      const result = unitUnderTest.compareKandidatenByListenPosition(
        kandidat,
        kandidat
      );

      expect(result).toStrictEqual(0);
    });

    it("should_returnEqual_when_bothObjectsHaveEqualListenposition", () => {
      const listenposition = generateRandomNumber(3);
      const kandidat1 = prepareKandidat()
        .listenposition(listenposition)
        .build();
      const kandidat2 = prepareKandidat()
        .listenposition(listenposition)
        .build();

      const result = unitUnderTest.compareKandidatenByListenPosition(
        kandidat1,
        kandidat2
      );

      expect(result).toStrictEqual(0);
    });

    it("should_returnSmaller_when_kandidat1HasSmallerListenpositionThanKandidat2", () => {
      const listenposition = generateRandomNumber(3);
      const kandidat1 = prepareKandidat()
        .listenposition(listenposition)
        .build();
      const kandidat2 = prepareKandidat()
        .listenposition(listenposition + 1)
        .build();

      const result = unitUnderTest.compareKandidatenByListenPosition(
        kandidat1,
        kandidat2
      );

      expect(result).lessThan(0);
    });
    it("should_returnLarger_when_kandidat1HasLargerListenpositionThanKandidat2", () => {
      const listenposition = generateRandomNumber(3);
      const kandidat1 = prepareKandidat()
        .listenposition(listenposition)
        .build();
      const kandidat2 = prepareKandidat()
        .listenposition(listenposition - 1)
        .build();

      const result = unitUnderTest.compareKandidatenByListenPosition(
        kandidat1,
        kandidat2
      );

      expect(result).toBeGreaterThan(0);
    });
  });

  describe("getKandidatLaufendeNummer", () => {
    it.each([
      { kandidatListenPosition: 0, expectedKandidatListenPosition: "00" },
      { kandidatListenPosition: 9, expectedKandidatListenPosition: "09" },
      { kandidatListenPosition: 10, expectedKandidatListenPosition: "10" },
      { kandidatListenPosition: 99, expectedKandidatListenPosition: "99" },
      {
        kandidatListenPosition: 10000,
        expectedKandidatListenPosition: "10000",
      },
    ])(
      "should_returnNumberWithTwoCharPadAtStartEndingWith'$expectedKandidatListenPosition'_when_noPadLengthIsDefinedAndKandidatenListenpositionIs'$kandidatListenPosition'",
      (testcaseArgument) => {
        const wahlvorschlagNummer = generateRandomNumber(2);
        const result = unitUnderTest.getKandidatLaufendeNummer(
          wahlvorschlagNummer,
          testcaseArgument.kandidatListenPosition
        );

        const expectedResult = `${wahlvorschlagNummer}${testcaseArgument.expectedKandidatListenPosition}`;
        expect(result).toStrictEqual(expectedResult);
      }
    );

    it.each([
      { kandidatListenPosition: 0, expectedKandidatListenPosition: "00000" },
      { kandidatListenPosition: 9, expectedKandidatListenPosition: "00009" },
      { kandidatListenPosition: 10, expectedKandidatListenPosition: "00010" },
      { kandidatListenPosition: 99, expectedKandidatListenPosition: "00099" },
      {
        kandidatListenPosition: 10000,
        expectedKandidatListenPosition: "10000",
      },
    ])(
      "should_returnNumberWithTwoCharPadAtStartEndingWith'$expectedKandidatListenPosition'_when_padLengthIs5AndKandidatenListenpositionIs'$kandidatListenPosition'",
      (testcaseArgument) => {
        const wahlvorschlagNummer = generateRandomNumber(2);
        const result = unitUnderTest.getKandidatLaufendeNummer(
          wahlvorschlagNummer,
          testcaseArgument.kandidatListenPosition,
          5
        );

        const expectedResult = `${wahlvorschlagNummer}${testcaseArgument.expectedKandidatListenPosition}`;
        expect(result).toStrictEqual(expectedResult);
      }
    );
  });

  describe("getFirstKandidatNameOrEmptyString", () => {
    it.each([
      { value: undefined, description: "AreUndefined" },
      { value: [], description: "AreEmpty" },
    ])(
      "should_returnEmptyString_when_wahlvorschlagHasNoKandidaten'$description'",
      (args) => {
        const wahlvorschlag = prepareWahlvorschlag()
          .kandidaten(args.value)
          .build();

        const result =
          unitUnderTest.getFirstKandidatNameOrEmptyString(wahlvorschlag);

        expect(result).toStrictEqual("");
      }
    );

    it("should_returnNameOfKandidat_when_onlyOneKandidatIsGiven", () => {
      const kandidatName = generateRandomString(10);
      const wahlvorschlag = prepareWahlvorschlag()
        .kandidaten([prepareKandidat().name(kandidatName).build()])
        .build();

      const result =
        unitUnderTest.getFirstKandidatNameOrEmptyString(wahlvorschlag);

      expect(result).toStrictEqual(kandidatName);
    });

    it("should_returnNameOfKandidatWithLowestListenPosition_when_moreThanOneKandidatAreGivenWithDifferentListenPosition", () => {
      const kandidatenNameToGet = generateRandomString(10);
      const wahlvorschlag = prepareWahlvorschlag()
        .kandidaten([
          prepareKandidat().listenposition(10).build(),
          prepareKandidat().listenposition(7).build(),
          prepareKandidat().listenposition(5).name(kandidatenNameToGet).build(),
          prepareKandidat().listenposition(6).build(),
          prepareKandidat().listenposition(12).build(),
        ])
        .build();

      const result =
        unitUnderTest.getFirstKandidatNameOrEmptyString(wahlvorschlag);

      expect(result).toStrictEqual(kandidatenNameToGet);
    });

    it("should_returnNameOfKandidatWithFirstAppearanceOfLowestListenPosition_when_moreThanOneKandidatAreGivenWithSameListenPosition", () => {
      const kandidatenNameToGet = generateRandomString(10);
      const wahlvorschlag = prepareWahlvorschlag()
        .kandidaten([
          prepareKandidat().listenposition(10).build(),
          prepareKandidat().listenposition(7).build(),
          prepareKandidat().listenposition(5).name(kandidatenNameToGet).build(),
          prepareKandidat().listenposition(6).build(),
          prepareKandidat().listenposition(5).build(),
        ])
        .build();

      const result =
        unitUnderTest.getFirstKandidatNameOrEmptyString(wahlvorschlag);

      expect(result).toStrictEqual(kandidatenNameToGet);
    });
  });

  describe("sortWahlvorschlaegeByOrdnungszahl", () => {
    it("should_returnWahlvorschlaegeWithSortedEntries_when_givenWahlvorschlaege", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const wahlvorschlag1 = prepareWahlvorschlag().ordnungszahl(1).build();
      const wahlvorschlag2 = prepareWahlvorschlag().ordnungszahl(2).build();
      const wahlvorschlag3 = prepareWahlvorschlag().ordnungszahl(3).build();
      const wahlvorschlag4 = prepareWahlvorschlag().ordnungszahl(4).build();

      const mockedWahlvorschlaege = prepareWahlvorschlaege()
        .wahlID(wahlID)
        .wahlbezirkID(wahlbezirkID)
        .wahlvorschlaege([
          wahlvorschlag4,
          wahlvorschlag2,
          wahlvorschlag1,
          wahlvorschlag3,
        ])
        .build();

      const sortedWahlvorschlaege =
        unitUnderTest.sortWahlvorschlaegeByOrdnungszahl(mockedWahlvorschlaege);

      let expectedOrdnungszahl = 1;
      sortedWahlvorschlaege.wahlvorschlaege.forEach((wahlvorschlag) => {
        expect(wahlvorschlag.ordnungszahl).toBe(expectedOrdnungszahl);
        expectedOrdnungszahl++;
      });
      expect(sortedWahlvorschlaege.wahlvorschlaege).toEqual([
        wahlvorschlag1,
        wahlvorschlag2,
        wahlvorschlag3,
        wahlvorschlag4,
      ]);
    });
  });
});
