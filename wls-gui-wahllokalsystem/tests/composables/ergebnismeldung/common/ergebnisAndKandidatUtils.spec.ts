import type { ErgebnisAndKandidat } from "@/types/ergebnisermittlung/ErgebnisAndKandidat.ts";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useErgebnisAndKandidatUtils } from "@/composables/ergebnismeldung/common/ergebnisAndKandidatUtils.ts";

const { prepareErgebnis } = useErgebnisseTestDataFactory();
const { createKandidat } = useWahlvorschlaegeTestDataFactory();

describe("ergebnisAndKandidatUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useErgebnisAndKandidatUtils>;

  beforeEach(() => {
    unitUnderTest = useErgebnisAndKandidatUtils();
  });

  describe("summeKandidatenStimmen", () => {
    it("should_returnSum_when_multipleErgebnisseAreGiven", () => {
      const ergebnisAndKandidat: ErgebnisAndKandidat[] = [
        {
          ergebnis: prepareErgebnis().ergebnis(1).build(),
          kandidat: createKandidat(),
        },
        {
          ergebnis: prepareErgebnis().ergebnis(7).build(),
          kandidat: createKandidat(),
        },
        {
          ergebnis: prepareErgebnis().ergebnis(12).build(),
          kandidat: createKandidat(),
        },
        {
          ergebnis: prepareErgebnis().ergebnis(42).build(),
          kandidat: createKandidat(),
        },
      ];
      const result = unitUnderTest.summeKandidatenStimmen(ergebnisAndKandidat);
      expect(result).toStrictEqual(62);
    });

    it("should_returnErgebnis_when_onlyOneErgebnisIsGiven", () => {
      const ergebnisAndKandidat: ErgebnisAndKandidat[] = [
        {
          ergebnis: prepareErgebnis().ergebnis(42).build(),
          kandidat: createKandidat(),
        },
      ];
      const result = unitUnderTest.summeKandidatenStimmen(ergebnisAndKandidat);
      expect(result).toStrictEqual(42);
    });

    it("should_returnZero_when_emptyArrayIsGiven", () => {
      const result = unitUnderTest.summeKandidatenStimmen([]);
      expect(result).toStrictEqual(0);
    });

    it("should_returnSumAndCountMissingErgebnisAsZero_when_multipleErgebnisseAreGiven", () => {
      const ergebnisAndKandidat: ErgebnisAndKandidat[] = [
        {
          ergebnis: prepareErgebnis().ergebnis(null).build(),
          kandidat: createKandidat(),
        },
        {
          ergebnis: prepareErgebnis().ergebnis(7).build(),
          kandidat: createKandidat(),
        },
        {
          ergebnis: prepareErgebnis().ergebnis(12).build(),
          kandidat: createKandidat(),
        },
        {
          ergebnis: prepareErgebnis().ergebnis(null).build(),
          kandidat: createKandidat(),
        },
      ];
      const result = unitUnderTest.summeKandidatenStimmen(ergebnisAndKandidat);
      expect(result).toStrictEqual(19);
    });
  });
});
