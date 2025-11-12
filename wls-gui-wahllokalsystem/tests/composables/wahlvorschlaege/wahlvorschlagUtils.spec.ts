import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { prepareKandidat, prepareWahlvorschlag } =
  useWahlvorschlaegeTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();
const { prepareWahlvorschlaege } = useWahlvorschlaegeTestDataFactory();

describe("wahlvorschlagUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlvorschlagUtils>;

  beforeEach(() => {
    unitUnderTest = useWahlvorschlagUtils();
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
      expect(Array.from(sortedWahlvorschlaege.wahlvorschlaege)).toEqual([
        wahlvorschlag1,
        wahlvorschlag2,
        wahlvorschlag3,
        wahlvorschlag4,
      ]);
    });
  });
});
