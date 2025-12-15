import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
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

import { useOBWStapelBUtils } from "@/composables/ergebnismeldung/OBW/obwStapelBUtils.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisseByWahlIdAndStapelartOrUndefined: vi.fn(),
}));
vi.mock("@/stores/ergebnismeldungStore.ts", () => ({
  useErgebnismeldungStore: vi.fn().mockImplementation(() => ({
    getErgebnisseByWahlIdAndStapelartOrUndefined:
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined,
  })),
}));

const { generateRandomString } = useCommonTestDataFactory();
const { prepareErgebnisse, prepareErgebnis } = useErgebnisseTestDataFactory();

describe("obwStapelBUtils", () => {
  const wahlID = generateRandomString(10);

  let unitUnderTest: ReturnType<typeof useOBWStapelBUtils>;

  beforeEach(() => {
    unitUnderTest = useOBWStapelBUtils(computed(() => wahlID));
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("ergebnisseStapelBLeer", () => {
    it("should_returnNumber_when_ergebnisseGiven", () => {
      const ergebnisToFind = 5;
      const stapelArt = StapelArtEnum.ObwBLeer;
      const mockedErgebnisse = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: "wahlbezirkid",
          stapelArt: stapelArt,
        })
        .ergebnisse([prepareErgebnis().ergebnis(ergebnisToFind).build()])
        .build();

      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        mockedErgebnisse
      );

      const result = unitUnderTest.ergebnisseStapelBLeer.value;

      expect(result).toStrictEqual(ergebnisToFind);
      expect(
        mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined
      ).toHaveBeenCalledWith(wahlID, stapelArt);
    });

    it("should_returnUndefined_when_noErgebnisseGiven", () => {
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        undefined
      );

      const result = unitUnderTest.ergebnisseStapelBLeer.value;

      expect(result).toStrictEqual(undefined);
      expect(
        mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined
      ).toHaveBeenCalledWith(wahlID, StapelArtEnum.ObwBLeer);
    });
  });

  describe("ergebnisseStapelBUngekennzeichnet", () => {
    it("should_returnNumber_when_ergebnisseGiven", () => {
      const ergebnisToFind = 9;
      const stapelArt = StapelArtEnum.ObwBUngekennzeichnet;
      const mockedErgebnisse = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: "wahlbezirkid",
          stapelArt: stapelArt,
        })
        .ergebnisse([prepareErgebnis().ergebnis(ergebnisToFind).build()])
        .build();

      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        mockedErgebnisse
      );

      const result = unitUnderTest.ergebnisseStapelBUngekennzeichnet.value;

      expect(result).toStrictEqual(ergebnisToFind);
      expect(
        mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined
      ).toHaveBeenCalledWith(wahlID, stapelArt);
    });

    it("should_returnUndefined_when_noErgebnisseGiven", () => {
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockReturnValue(
        undefined
      );

      const result = unitUnderTest.ergebnisseStapelBUngekennzeichnet.value;

      expect(result).toStrictEqual(undefined);
      expect(
        mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined
      ).toHaveBeenCalledWith(wahlID, StapelArtEnum.ObwBUngekennzeichnet);
    });
  });

  describe("sumStapelB", () => {
    it.each([
      { stapelBLeer: 0, stapelBUngekennzeichnet: 0 },
      { stapelBLeer: 0, stapelBUngekennzeichnet: undefined },
      { stapelBLeer: null, stapelBUngekennzeichnet: 0 },
    ])(
      "should_returnSumZero_when_ergebnisForStapelIsNullUndefinedOrZero",
      (values) => {
        const ergebnisseStapelBLeerSpy = vi.spyOn(
          unitUnderTest.ergebnisseStapelBLeer,
          "value",
          "get"
        );
        ergebnisseStapelBLeerSpy.mockReturnValue(values.stapelBLeer);

        const ergebnisseStapelBUngekennzeichnetSpy = vi.spyOn(
          unitUnderTest.ergebnisseStapelBUngekennzeichnet,
          "value",
          "get"
        );
        ergebnisseStapelBUngekennzeichnetSpy.mockReturnValue(
          values.stapelBUngekennzeichnet
        );

        const result = unitUnderTest.sumStapelB.value;

        expect(result).toStrictEqual(0);
      }
    );

    it.each([
      { stapelBLeer: 0, stapelBUngekennzeichnet: 3, sum: 3 },
      { stapelBLeer: 4, stapelBUngekennzeichnet: 7, sum: 11 },
      { stapelBLeer: null, stapelBUngekennzeichnet: 17, sum: 17 },
      { stapelBLeer: undefined, stapelBUngekennzeichnet: 17, sum: 17 },
      { stapelBLeer: 8, stapelBUngekennzeichnet: null, sum: 8 },
      { stapelBLeer: 8, stapelBUngekennzeichnet: undefined, sum: 8 },
    ])(
      `should_returnSum'$sum'_when_ergebnisForStapelIs'$stapelBLeer'And'$stapelBUngekennzeichnet'`,
      async (values) => {
        const ergebnisseStapelBLeerSpy = vi.spyOn(
          unitUnderTest.ergebnisseStapelBLeer,
          "value",
          "get"
        );
        ergebnisseStapelBLeerSpy.mockReturnValue(values.stapelBLeer);

        const ergebnisseStapelBUngekennzeichnetSpy = vi.spyOn(
          unitUnderTest.ergebnisseStapelBUngekennzeichnet,
          "value",
          "get"
        );
        ergebnisseStapelBUngekennzeichnetSpy.mockReturnValue(
          values.stapelBUngekennzeichnet
        );

        const result = unitUnderTest.sumStapelB.value;

        expect(result).toStrictEqual(values.sum);

        ergebnisseStapelBLeerSpy.mockRestore();
        ergebnisseStapelBUngekennzeichnetSpy.mockRestore();
      }
    );
  });
});
