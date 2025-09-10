import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { prepareKandidat, prepareWahlvorschlag } =
  useWahlvorschlaegeTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();

describe("wahlvorschlagUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlvorschlagUtils>;

  beforeEach(() => {
    unitUnderTest = useWahlvorschlagUtils();
  });

  describe("getFirstKandidatNameOrEmptyString", () => {
    it.each([
      { value: undefined, description: "AreUndefined" },
      { value: new Set([]), description: "AreEmpty" },
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
        .kandidaten(new Set([prepareKandidat().name(kandidatName).build()]))
        .build();

      const result =
        unitUnderTest.getFirstKandidatNameOrEmptyString(wahlvorschlag);

      expect(result).toStrictEqual(kandidatName);
    });

    it("should_returnNameOfKandidatWithLowestListenPosition_when_moreThanOneKandidatAreGivenWithDifferentListenPosition", () => {
      const kandidatenNameToGet = generateRandomString(10);
      const wahlvorschlag = prepareWahlvorschlag()
        .kandidaten(
          new Set([
            prepareKandidat().listenposition(10).build(),
            prepareKandidat().listenposition(7).build(),
            prepareKandidat()
              .listenposition(5)
              .name(kandidatenNameToGet)
              .build(),
            prepareKandidat().listenposition(6).build(),
            prepareKandidat().listenposition(12).build(),
          ])
        )
        .build();

      const result =
        unitUnderTest.getFirstKandidatNameOrEmptyString(wahlvorschlag);

      expect(result).toStrictEqual(kandidatenNameToGet);
    });

    it("should_returnNameOfKandidatWithFirstAppearenceOfLoweseListenPosition_when_moreThanOneKandidatAreGivenWithSameListenPosition", () => {
      const kandidatenNameToGet = generateRandomString(10);
      const wahlvorschlag = prepareWahlvorschlag()
        .kandidaten(
          new Set([
            prepareKandidat().listenposition(10).build(),
            prepareKandidat().listenposition(7).build(),
            prepareKandidat()
              .listenposition(5)
              .name(kandidatenNameToGet)
              .build(),
            prepareKandidat().listenposition(6).build(),
            prepareKandidat().listenposition(5).build(),
          ])
        )
        .build();

      const result =
        unitUnderTest.getFirstKandidatNameOrEmptyString(wahlvorschlag);

      expect(result).toStrictEqual(kandidatenNameToGet);
    });
  });
});
