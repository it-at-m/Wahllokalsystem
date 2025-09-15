import type { ErgebnisAndWahlvorschlag } from "@/types/ergebnisermittlung/ErgebnisAndWahlvorschlag.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { computed } from "vue";

import { useOBWStapelAUtils } from "@/composables/ergebnisermittlung/obwStapelAUtils.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisseByWahlIdAndStapelartOrUndefined: vi.fn(),
  getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID: vi.fn(),
}));

vi.mock("@/stores/ergebnismeldungStore.ts", () => ({
  useErgebnismeldungStore: vi.fn().mockImplementation(() => ({
    getErgebnisseByWahlIdAndStapelartOrUndefined:
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined,
  })),
}));
vi.mock("@/stores/wahlvorschlaegeStore.ts", () => ({
  useWahlvorschlaegeStore: () => ({
    getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID:
      mockDefinitions.getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID,
  }),
}));

const { generateRandomString } = useCommonTestDataFactory();
const { prepareErgebnisse, prepareErgebnis } = useErgebnisseTestDataFactory();
const { createWahlvorschlag, prepareWahlvorschlag } =
  useWahlvorschlaegeTestDataFactory();

describe("obwStapelAUtils", () => {
  const wahlID = generateRandomString(10);
  const wahlbezirkID = generateRandomString(10);

  let unitUnderTest: ReturnType<typeof useOBWStapelAUtils>;

  beforeEach(() => {
    unitUnderTest = useOBWStapelAUtils(
      computed(() => wahlID),
      computed(() => wahlbezirkID)
    );
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("ergebnisseWithWahlvorschlag", () => {
    it("should_returnArrayWithErgebnisAndWahlvorschlag_when_ergebnisseAndWahlvorschlageForIDsAreGiven", () => {
      const mockedErgebnis1 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .build();
      const mockedErgebnis2 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag2")
        .build();
      const mockedErgebnis3 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag3")
        .build();
      const mockedErgebnis4 = prepareErgebnis()
        .wahlvorschlagID("wahlvorschlag1")
        .build();

      const mockedErgebnisseArray = prepareErgebnisse()
        .ergebnisse([
          mockedErgebnis1,
          mockedErgebnis2,
          mockedErgebnis3,
          mockedErgebnis4,
        ])
        .build();
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        mockedErgebnisseArray
      );

      const mockedWahlvorschlag1 = prepareWahlvorschlag()
        .identifikator("wahlvorschlag1")
        .build();
      const mockedWahlvorschlag2 = prepareWahlvorschlag()
        .identifikator("wahlvorschlag2")
        .build();
      mockDefinitions.getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID.mockImplementation(
        (wahlID, wahlbezirkID, wahlvorschlagID: string) => {
          if (wahlvorschlagID === mockedWahlvorschlag1.identifikator) {
            return mockedWahlvorschlag1;
          } else if (wahlvorschlagID === mockedWahlvorschlag2.identifikator) {
            return mockedWahlvorschlag2;
          } else {
            return null;
          }
        }
      );

      const result = unitUnderTest.ergebnisseAndWahlvorschlaege.value;

      expect(
        mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mock.calls
      ).toStrictEqual([[wahlID, StapelArtEnum.ObwA]]);
      expect(
        mockDefinitions
          .getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID
          .mock.calls
      ).toStrictEqual([
        [wahlID, wahlbezirkID, mockedWahlvorschlag1.identifikator],
        [wahlID, wahlbezirkID, mockedWahlvorschlag2.identifikator],
        [wahlID, wahlbezirkID, "wahlvorschlag3"],
        [wahlID, wahlbezirkID, mockedWahlvorschlag1.identifikator],
      ]);
      const expectedResult: ErgebnisAndWahlvorschlag[] = [
        {
          ergebnis: mockedErgebnis1,
          wahlvorschlag: mockedWahlvorschlag1,
        },
        {
          ergebnis: mockedErgebnis2,
          wahlvorschlag: mockedWahlvorschlag2,
        },
        {
          ergebnis: mockedErgebnis4,
          wahlvorschlag: mockedWahlvorschlag1,
        },
      ];
      expect(result).toStrictEqual(expectedResult);
    });

    it.each([
      {
        mockedResult: [],
        description: "EmptyArray",
      },
      { mockedResult: undefined, description: "Undefined" },
    ])(
      "should_returnEmptyArray_when_noErgebnisseForIDsAreGiven_ergebnisseAre'$description'",
      (testcaseArguments) => {
        mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
          testcaseArguments.mockedResult
        );

        const result = unitUnderTest.ergebnisseAndWahlvorschlaege.value;

        expect(result).toStrictEqual([]);
      }
    );
  });

  describe("sumOfValidVotes", () => {
    it("should_returnSumOfErgebnis_when_ergebnisseAndWahlvorschlagHasEntries", () => {
      const ergebnisseWithWahlvorschlagSpy = vi.spyOn(
        unitUnderTest.ergebnisseAndWahlvorschlaege,
        "value",
        "get"
      );
      ergebnisseWithWahlvorschlagSpy.mockReturnValue([
        {
          ergebnis: prepareErgebnis().ergebnis(2).build(),
          wahlvorschlag: createWahlvorschlag(),
        },
        {
          ergebnis: prepareErgebnis().ergebnis(13).build(),
          wahlvorschlag: createWahlvorschlag(),
        },
        {
          ergebnis: prepareErgebnis().ergebnis(42).build(),
          wahlvorschlag: createWahlvorschlag(),
        },
        {
          ergebnis: prepareErgebnis().ergebnis(47).build(),
          wahlvorschlag: createWahlvorschlag(),
        },
        {
          ergebnis: prepareErgebnis().ergebnis(11).build(),
          wahlvorschlag: createWahlvorschlag(),
        },
      ]);

      const result = unitUnderTest.sumOfValidVotes.value;

      expect(result).toStrictEqual(2 + 13 + 42 + 47 + 11);

      ergebnisseWithWahlvorschlagSpy.mockRestore();
    });

    it("should_countNullAsZero_when_wahlvorschlaeHasEntries", () => {
      const ergebnisseWithWahlvorschlagSpy = vi.spyOn(
        unitUnderTest.ergebnisseAndWahlvorschlaege,
        "value",
        "get"
      );
      ergebnisseWithWahlvorschlagSpy.mockReturnValue([
        {
          ergebnis: prepareErgebnis().ergebnis(2).build(),
          wahlvorschlag: createWahlvorschlag(),
        },
        {
          ergebnis: prepareErgebnis().ergebnis(null).build(),
          wahlvorschlag: createWahlvorschlag(),
        },
        {
          ergebnis: prepareErgebnis().ergebnis(42).build(),
          wahlvorschlag: createWahlvorschlag(),
        },
      ]);

      const result = unitUnderTest.sumOfValidVotes.value;

      expect(result).toStrictEqual(2 + 42);

      ergebnisseWithWahlvorschlagSpy.mockRestore();
    });

    it("should_return0_when_ergebnisseAndWahlvorschlagHasNoEntries", () => {
      const ergebnisseWithWahlvorschlagSpy = vi.spyOn(
        unitUnderTest.ergebnisseAndWahlvorschlaege,
        "value",
        "get"
      );
      ergebnisseWithWahlvorschlagSpy.mockReturnValue([]);

      const result = unitUnderTest.sumOfValidVotes.value;

      expect(result).toStrictEqual(0);

      ergebnisseWithWahlvorschlagSpy.mockRestore();
    });
  });
});
