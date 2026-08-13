import type { Kandidat as DseKandidat } from "@/types/dse/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";
import type { Wahlvorschlag as DseWahlvorschlag } from "@/types/dse/Wahlvorschlag.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { Wahlvorschlag as UiWahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { describe, expect, it, vi } from "vitest";

import { useStimmzettelUtils } from "@/composables/dse/stimmzettelUtils.ts";

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

const { prepareStimmzettel } = useStimmzettelTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();
const {
  createWahlvorschlaege,
  createWahlvorschlag,
  prepareWahlvorschlag,
  prepareKandidat,
} = useWahlvorschlaegeTestDataFactory();

describe("stimmzettelUtils.ts", () => {
  const {
    isVorgemerktFuerBeschluss,
    getVormerkungsgrund,
    createStimmzettelWithWahlvorschlaege,
  } = useStimmzettelUtils();

  describe("createStimmzettelWithWahlvorschlaege", () => {
    it("should_createStimmzettelWithInitialValues_when_wahlvorschlaegeAreGiven", () => {
      const uiWahlvorschlaege: Wahlvorschlaege = createWahlvorschlaege();

      const result: Stimmzettel = createStimmzettelWithWahlvorschlaege(
        uiWahlvorschlaege.wahlvorschlaege
      );

      const expected: Stimmzettel = {
        stimmzettelkennung: 0,
        beschlussvorschlag: [],
        beschlussfassung: null,
        gueltigkeit: null,
        invalideVotes: 0,
        wahlvorschlaege: uiWahlvorschlaege.wahlvorschlaege.map((ui) => {
          const dseKandidaten: DseKandidat[] =
            ui.kandidaten?.flatMap((uiKandidat) => {
              const kandidaten: DseKandidat[] = [];
              for (
                let nennung = 1;
                nennung <= uiKandidat.anzahlNennungen;
                nennung++
              ) {
                kandidaten.push({
                  kandidatId: uiKandidat.identifikator,
                  nennung,
                  listenposition: uiKandidat.listenposition,
                  votesByVoter: null,
                  isDiscarded: false,
                  votesByWahlvorschlag: null,
                  invalidVotes: null,
                });
              }
              return kandidaten;
            }) ?? [];

          const dseWahlvorschlag: DseWahlvorschlag = {
            wahlvorschlagID: ui.identifikator,
            ordnungszahl: ui.ordnungszahl,
            selected: false,
            kandidaten: dseKandidaten,
          };
          return dseWahlvorschlag;
        }),
      };

      expect(result).toStrictEqual(expected);
    });

    it("should_createDseKandidatenForEachNennung_when_kandidatHasMultipleNennungen", () => {
      const kandidatWithTwoNennungen = prepareKandidat()
        .anzahlNennungen(2)
        .listenposition(5)
        .identifikator("k-id")
        .build();

      const uiWahlvorschlag: UiWahlvorschlag = prepareWahlvorschlag()
        .identifikator("w-id")
        .ordnungszahl(10)
        .kandidaten([kandidatWithTwoNennungen])
        .build();

      const result: Stimmzettel = createStimmzettelWithWahlvorschlaege([
        uiWahlvorschlag,
      ]);

      const dseKandidaten = result.wahlvorschlaege[0]
        .kandidaten as DseKandidat[];

      const expectedDseKandidaten: DseKandidat[] = [
        {
          kandidatId: "k-id",
          nennung: 1,
          listenposition: 5,
          votesByVoter: null,
          isDiscarded: false,
          votesByWahlvorschlag: null,
          invalidVotes: null,
        },
        {
          kandidatId: "k-id",
          nennung: 2,
          listenposition: 5,
          votesByVoter: null,
          isDiscarded: false,
          votesByWahlvorschlag: null,
          invalidVotes: null,
        },
      ];

      expect(dseKandidaten).toStrictEqual(expectedDseKandidaten);
    });

    it("should_createDseWahlvorschlagWithEmptyKandidaten_when_uiWahlvorschlagHasNoKandidaten", () => {
      const uiWahlvorschlagWithoutKandidaten: UiWahlvorschlag =
        prepareWahlvorschlag().kandidaten(undefined).build();

      const result: Stimmzettel = createStimmzettelWithWahlvorschlaege([
        uiWahlvorschlagWithoutKandidaten,
      ]);

      const expectedDseWahlvorschlag: DseWahlvorschlag = {
        wahlvorschlagID: uiWahlvorschlagWithoutKandidaten.identifikator,
        ordnungszahl: uiWahlvorschlagWithoutKandidaten.ordnungszahl,
        selected: false,
        kandidaten: [],
      };

      expect(result.wahlvorschlaege[0]).toStrictEqual(expectedDseWahlvorschlag);
    });

    it("should_createDseStimmzettelWithAllWahlvorschlaege_when_multipleUiWahlvorschlaegeAreGiven", () => {
      const w1: UiWahlvorschlag = createWahlvorschlag();
      const w2: UiWahlvorschlag = prepareWahlvorschlag()
        .identifikator("w2")
        .ordnungszahl(20)
        .build();

      const result: Stimmzettel = createStimmzettelWithWahlvorschlaege([
        w1,
        w2,
      ]);

      const expectedWahlvorschlaege: DseWahlvorschlag[] = [w1, w2].map((ui) => {
        const dseKandidaten: DseKandidat[] =
          ui.kandidaten?.flatMap((uiKandidat) => {
            const kandidaten: DseKandidat[] = [];
            for (
              let nennung = 1;
              nennung <= uiKandidat.anzahlNennungen;
              nennung++
            ) {
              kandidaten.push({
                kandidatId: uiKandidat.identifikator,
                nennung,
                listenposition: uiKandidat.listenposition,
                votesByVoter: null,
                isDiscarded: false,
                votesByWahlvorschlag: null,
                invalidVotes: null,
              });
            }
            return kandidaten;
          }) ?? [];

        const dseWahlvorschlag: DseWahlvorschlag = {
          wahlvorschlagID: ui.identifikator,
          ordnungszahl: ui.ordnungszahl,
          selected: false,
          kandidaten: dseKandidaten,
        };
        return dseWahlvorschlag;
      });

      expect(result.wahlvorschlaege).toStrictEqual(expectedWahlvorschlaege);
    });

    it("should_createStimmzettelWithEmptyWahlvorschlaege_when_inputIsEmpty", () => {
      const result: Stimmzettel = createStimmzettelWithWahlvorschlaege([]);

      const expected: Stimmzettel = {
        stimmzettelkennung: 0,
        beschlussvorschlag: [],
        beschlussfassung: null,
        gueltigkeit: null,
        invalideVotes: 0,
        wahlvorschlaege: [],
      };

      expect(result).toStrictEqual(expected);
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
