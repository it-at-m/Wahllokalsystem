import type { WahlvorschlagWithScorableKandidaten } from "@/types/ergebnisermittlung/WahlvorschlagWithScorableKandidaten.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useWahlvorschlagWithScorableKandidatenMapper } from "@/composables/ergebnisermittlung/wahlvorschlagWithScorableKandidatenMapper.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { generateRandomString, getRandomItem } = useCommonTestDataFactory();
const { prepareWahlvorschlag, prepareKandidat } =
  useWahlvorschlaegeTestDataFactory();
const { createErgebnis, prepareErgebnisse, prepareErgebnis } =
  useErgebnisseTestDataFactory();

describe("wahlvorschlagWithScorableKandidatenMapper.ts", () => {
  let unitUnderTest: ReturnType<
    typeof useWahlvorschlagWithScorableKandidatenMapper
  >;

  beforeEach(() => {
    unitUnderTest = useWahlvorschlagWithScorableKandidatenMapper();
  });

  describe("toWahlvorschlagWithScorableKandidaten", () => {
    it("should_mapWahlvorschlagAndErgebnisValues_when_wahlvorschlagAndErgebnisseAreGiven", () => {
      const kandidat1 = prepareKandidat().identifikator("k1").build();
      const kandidat2 = prepareKandidat().identifikator("k2").build();
      const kandidat3 = prepareKandidat().identifikator("k3").build();
      const wahlvorschlag = prepareWahlvorschlag()
        .kandidaten(new Set<Kandidat>([kandidat1, kandidat2, kandidat3]))
        .build();

      const ergebnis1 = prepareErgebnis()
        .kandidatID(kandidat1.identifikator)
        .build();
      const ergebnis2 = prepareErgebnis()
        .kandidatID(kandidat2.identifikator)
        .build();
      const ergebnisseToMap: Ergebnisse = prepareErgebnisse()
        .ergebnisse([ergebnis1, ergebnis2])
        .build();

      const result = unitUnderTest.toWahlvorschlagWithScorableKandidaten(
        wahlvorschlag,
        ergebnisseToMap
      );

      const expectedResult: WahlvorschlagWithScorableKandidaten = {
        kurzname: wahlvorschlag.kurzname,
        identifikator: wahlvorschlag.identifikator,
        ordnungszahl: wahlvorschlag.ordnungszahl,
        scorableKandidaten: [
          {
            ergebnis: {
              ergebnis: ergebnis1.ergebnis,
              numIndex: ergebnis1.numIndex,
              kandidatID: ergebnis1.kandidatID,
              wahlvorschlagsOrdnungszahl: ergebnis1.wahlvorschlagsOrdnungszahl,
              wahlvorschlagID: ergebnis1.wahlvorschlagID,
            },
            kandidat: kandidat1,
          },
          {
            ergebnis: {
              ergebnis: ergebnis2.ergebnis,
              numIndex: ergebnis2.numIndex,
              kandidatID: ergebnis2.kandidatID,
              wahlvorschlagsOrdnungszahl: ergebnis2.wahlvorschlagsOrdnungszahl,
              wahlvorschlagID: ergebnis1.wahlvorschlagID,
            },
            kandidat: kandidat2,
          },
          {
            ergebnis: {
              ergebnis: null,
              numIndex: null,
              kandidatID: kandidat3.identifikator,
              wahlvorschlagsOrdnungszahl: wahlvorschlag.ordnungszahl,
              wahlvorschlagID: wahlvorschlag.identifikator,
            },
            kandidat: kandidat3,
          },
        ],
      };
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_mapWahlvorschlagValuesAndGenerateErgebnis_when_onlyWahlvorschlagIsGiven", () => {
      const kandidat1 = prepareKandidat().identifikator("k1").build();
      const kandidat2 = prepareKandidat().identifikator("k2").build();
      const kandidat3 = prepareKandidat().identifikator("k3").build();
      const wahlvorschlag = prepareWahlvorschlag()
        .kandidaten(new Set<Kandidat>([kandidat1, kandidat2, kandidat3]))
        .build();

      const result = unitUnderTest.toWahlvorschlagWithScorableKandidaten(
        wahlvorschlag,
        null
      );

      const expectedResult: WahlvorschlagWithScorableKandidaten = {
        kurzname: wahlvorschlag.kurzname,
        identifikator: wahlvorschlag.identifikator,
        ordnungszahl: wahlvorschlag.ordnungszahl,
        scorableKandidaten: [
          {
            ergebnis: {
              ergebnis: null,
              numIndex: null,
              kandidatID: kandidat1.identifikator,
              wahlvorschlagsOrdnungszahl: wahlvorschlag.ordnungszahl,
              wahlvorschlagID: wahlvorschlag.identifikator,
            },
            kandidat: kandidat1,
          },
          {
            ergebnis: {
              ergebnis: null,
              numIndex: null,
              kandidatID: kandidat2.identifikator,
              wahlvorschlagsOrdnungszahl: wahlvorschlag.ordnungszahl,
              wahlvorschlagID: wahlvorschlag.identifikator,
            },
            kandidat: kandidat2,
          },
          {
            ergebnis: {
              ergebnis: null,
              numIndex: null,
              kandidatID: kandidat3.identifikator,
              wahlvorschlagsOrdnungszahl: wahlvorschlag.ordnungszahl,
              wahlvorschlagID: wahlvorschlag.identifikator,
            },
            kandidat: kandidat3,
          },
        ],
      };
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_ignoreErgebnisse_when_wahlvorschlagHasNoKandidaten", () => {
      const wahlvorschlag = prepareWahlvorschlag()
        .kandidaten(new Set<Kandidat>([]))
        .build();

      const ergebnisseToMap: Ergebnisse = prepareErgebnisse()
        .ergebnisse([createErgebnis(), createErgebnis()])
        .build();

      const result = unitUnderTest.toWahlvorschlagWithScorableKandidaten(
        wahlvorschlag,
        ergebnisseToMap
      );

      const expectedResult: WahlvorschlagWithScorableKandidaten = {
        kurzname: wahlvorschlag.kurzname,
        identifikator: wahlvorschlag.identifikator,
        ordnungszahl: wahlvorschlag.ordnungszahl,
        scorableKandidaten: [],
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("toErgebnisse", () => {
    it("should_returnErgebnisseWithErgebnisItems_when_ergebnisseAreGiven", () => {
      const wahlbezirkID = generateRandomString(10);
      const wahlID = generateRandomString(10);
      const stapelArt = getRandomItem(Object.values(StapelArtEnum));

      const ergebnisse = [createErgebnis(), createErgebnis(), createErgebnis()];

      const result = unitUnderTest.toErgebnisse(
        ergebnisse,
        wahlbezirkID,
        wahlID,
        stapelArt
      );

      const expectedResult: Ergebnisse = {
        bezirkUndWahlIDStapelart: {
          stapelArt,
          wahlID,
          wahlbezirkID,
        },
        ergebnisse,
      };
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnErgebnisseWithoutErgebnisItems_when_noErgebnisseAreGiven", () => {
      const wahlbezirkID = generateRandomString(10);
      const wahlID = generateRandomString(10);
      const stapelArt = getRandomItem(Object.values(StapelArtEnum));

      const result = unitUnderTest.toErgebnisse(
        [],
        wahlbezirkID,
        wahlID,
        stapelArt
      );

      const expectedResult: Ergebnisse = {
        bezirkUndWahlIDStapelart: {
          stapelArt,
          wahlID,
          wahlbezirkID,
        },
        ergebnisse: [],
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });
});
