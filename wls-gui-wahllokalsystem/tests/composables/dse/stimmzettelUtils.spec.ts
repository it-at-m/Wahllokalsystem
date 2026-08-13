import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { afterEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelUtils } from "@/composables/dse/stimmzettelUtils.ts";

const { prepareStimmzettel } = useStimmzettelTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  getStimmzettel: vi.fn(),
}));

vi.mock(import("@/composables/dse/stimmzettelService.ts"), () => ({
  useStimmzettelService: () => ({
    getStimmzettel: mockDefinitions.getStimmzettel,
    saveStimmzettel: vi.fn(),
    getAnzahlStimmzettel: vi.fn(),
  }),
}));

describe("stimmzettelUtils.ts", () => {
  const {
    getNextStimmzettelNumber,
    isVorgemerktFuerBeschluss,
    getVormerkungsgrund,
  } = useStimmzettelUtils();

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("getNextStimmzettelNumber", () => {
    it("should_returnNextNumber_when_oneStimmzettelExists", () => {
      const stimmzettel = prepareStimmzettel().stimmzettelkennung(3).build();

      const result = getNextStimmzettelNumber([stimmzettel]);

      expect(result).toStrictEqual(4);
    });

    it("should_returnNextNumber_when_multipleStimmzettelExists", () => {
      const stimmzettel1 = prepareStimmzettel().stimmzettelkennung(5).build();
      const stimmzettel2 = prepareStimmzettel().stimmzettelkennung(3).build();
      const stimmzettel3 = prepareStimmzettel().stimmzettelkennung(24).build();

      const result = getNextStimmzettelNumber([
        stimmzettel1,
        stimmzettel2,
        stimmzettel3,
      ]);

      expect(result).toStrictEqual(25);
    });

    it("should_returnNextNumber_when_noStimmzettelExists", () => {
      const result = getNextStimmzettelNumber([]);
      expect(result).toStrictEqual(1);
    });
  });

  describe("isVorgemerktFuerBeschluss", () => {
    it("should_returnFalse_when_noBeschlussvorschlagPresent", () => {
      const stimmzettel = prepareStimmzettel().beschlussvorschlag([]).build();

      const vorgemerkt = isVorgemerktFuerBeschluss(stimmzettel);

      expect(vorgemerkt).toBe(false);
    });

    it("should_returnTrue_when_beschlussvorschlagPresent", () => {
      const text1 = generateRandomString(8);
      const text2 = generateRandomString(10);

      const stimmzettel = prepareStimmzettel()
        .beschlussvorschlag([{ text: text1 }, { text: text2 }])
        .build();

      const vorgemerkt = isVorgemerktFuerBeschluss(stimmzettel);

      expect(vorgemerkt).toBe(true);
    });
  });

  describe("getVormerkungsgrund", () => {
    it("should_returnEmptyString_when_noBeschlussvorschlagPresent", () => {
      const stimmzettel = prepareStimmzettel().beschlussvorschlag([]).build();

      const grund = getVormerkungsgrund(stimmzettel);

      expect(grund).toBe("");
    });

    it("should_returnConcatenatedVormerkungsgrund_when_beschlussvorschlagPresent", () => {
      const text1 = generateRandomString(8);
      const text2 = generateRandomString(10);

      const stimmzettel = prepareStimmzettel()
        .beschlussvorschlag([{ text: text1 }, { text: text2 }])
        .build();

      const grund = getVormerkungsgrund(stimmzettel);

      expect(grund).toBe(`${text1}, ${text2}`);
    });
  });
});
