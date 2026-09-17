import type { Stimmzettel as PersistedStimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { DseKandidat } from "@/types/dse/stimmzettelerfassung/DseKandidat.ts";
import type { DseStimmzettel } from "@/types/dse/stimmzettelerfassung/DseStimmzettel.ts";
import type { DseWahlvorschlag } from "@/types/dse/stimmzettelerfassung/DseWahlvorschlag.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { Wahlvorschlag as UiWahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { describe, expect, it, vi } from "vitest";

import { useStimmzettelTools } from "@/composables/dse/stimmzettelerfassung/stimmzettelTools.ts";
import { useSystemBeschlussgrundReasonEnumTools } from "@/composables/dse/stimmzettelerfassung/systemBeschlussgrundReasonEnumTools.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getStimmzettel: vi.fn(),
  sortSystemBeschlussgruende: vi.fn(),
  sortWahlvorstandBeschlussgruende: vi.fn(),
  sortAndDeepCloneWahlvorschlaege: vi.fn(),
}));

vi.mock(
  import("@/composables/dse/stimmzettelerfassung/stimmzettelService.ts"),
  () => ({
    useStimmzettelService: () => ({
      getStimmzettel: mockDefinitions.getStimmzettel,
      saveStimmzettel: vi.fn(),
      getAnzahlStimmzettel: vi.fn(),
    }),
  })
);

vi.mock(
  import("@/composables/dse/beschlussfassung/beschlussgrundTools.ts"),
  async (importOriginal) => {
    const original = await importOriginal();
    return {
      useBeschlussgrundTools: () => ({
        ...original.useBeschlussgrundTools(),
        sortSystemBeschlussgruende: mockDefinitions.sortSystemBeschlussgruende,
        sortWahlvorstandBeschlussgruende:
          mockDefinitions.sortWahlvorstandBeschlussgruende,
      }),
    };
  }
);

vi.mock(
  import("@/composables/dse/stimmzettelerfassung/wahlvorschlagTools.ts"),
  () => ({
    useWahlvorschlagTools: () => ({
      sortAndDeepCloneWahlvorschlaege:
        mockDefinitions.sortAndDeepCloneWahlvorschlaege,
    }),
  })
);
const {
  preparePersistedStimmzettel,
  preparePersistedStimmzettelWahlvorschlag,
  createStimmzettel,
  prepareStimmzettel,
} = useStimmzettelTestDataFactory();
const { generateRandomString, getRandomItem } = useCommonTestDataFactory();
const {
  createWahlvorschlaege,
  createWahlvorschlag,
  prepareWahlvorschlag,
  prepareKandidat,
} = useWahlvorschlaegeTestDataFactory();

describe("stimmzettelTools.ts", () => {
  const {
    isVorgemerktFuerBeschluss,
    getVormerkungsgrund,
    createStimmzettelWithWahlvorschlaege,
    normalizePersistedStimmzettel,
    resetDseStimmzettel,
    isSamePersistedStimmzettel,
  } = useStimmzettelTools();

  const { mapSystemBeschlussgrundReasonEnumToText } =
    useSystemBeschlussgrundReasonEnumTools();

  describe("createStimmzettelWithWahlvorschlaege", () => {
    it("should_createStimmzettelWithInitialValues_when_wahlvorschlaegeAreGiven", () => {
      const uiWahlvorschlaege: Wahlvorschlaege = createWahlvorschlaege();

      const result: DseStimmzettel = createStimmzettelWithWahlvorschlaege(
        uiWahlvorschlaege.wahlvorschlaege
      );

      const expected: DseStimmzettel = {
        wahlvorstandBeschlussvorschlag: [],
        systemBeschlussvorschlag: [],
        beschlussfassung: null,
        gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
        invalideVotes: 0,
        wahlvorschlaege: uiWahlvorschlaege.wahlvorschlaege.map((ui) => {
          const dseWahlvorschlag: DseWahlvorschlag = {
            wahlvorschlagID: ui.identifikator,
            ordnungszahl: ui.ordnungszahl,
            selected: false,
            kandidaten: [],
            kurzname: ui.kurzname,
            erhaeltStimmen: ui.erhaeltStimmen,
            gueltigeStimmen: 0,
            ungueltigeStimmen: 0,
          };

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
                  ordnungszahl:
                    dseWahlvorschlag.ordnungszahl * 100 +
                    uiKandidat.listenposition,
                  einzelstimmen: null,
                  durchgestrichen: false,
                  reststimmen: null,
                  ungueltigeStimmen: null,
                  name: uiKandidat.name,
                  owningWahlvorschlag: dseWahlvorschlag,
                });
              }
              return kandidaten;
            }) ?? [];
          dseWahlvorschlag.kandidaten = dseKandidaten;
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

      const result: DseStimmzettel = createStimmzettelWithWahlvorschlaege([
        uiWahlvorschlag,
      ]);

      const dseKandidaten = result.wahlvorschlaege[0]
        .kandidaten as DseKandidat[];

      const expectedDseKandidaten: DseKandidat[] = [
        {
          kandidatId: "k-id",
          nennung: 1,
          listenposition: 5,
          ordnungszahl: 1005,
          einzelstimmen: null,
          durchgestrichen: false,
          reststimmen: null,
          ungueltigeStimmen: null,
          name: kandidatWithTwoNennungen.name,
          owningWahlvorschlag: result.wahlvorschlaege[0],
        },
        {
          kandidatId: "k-id",
          nennung: 2,
          listenposition: 5,
          ordnungszahl: 1005,
          einzelstimmen: null,
          durchgestrichen: false,
          reststimmen: null,
          ungueltigeStimmen: null,
          name: kandidatWithTwoNennungen.name,
          owningWahlvorschlag: result.wahlvorschlaege[0],
        },
      ];

      expect(dseKandidaten).toStrictEqual(expectedDseKandidaten);
    });

    it("should_createDseWahlvorschlagWithEmptyKandidaten_when_uiWahlvorschlagHasNoKandidaten", () => {
      const uiWahlvorschlagWithoutKandidaten: UiWahlvorschlag =
        prepareWahlvorschlag().kandidaten(undefined).build();

      const result: DseStimmzettel = createStimmzettelWithWahlvorschlaege([
        uiWahlvorschlagWithoutKandidaten,
      ]);

      const expectedDseWahlvorschlag: DseWahlvorschlag = {
        wahlvorschlagID: uiWahlvorschlagWithoutKandidaten.identifikator,
        ordnungszahl: uiWahlvorschlagWithoutKandidaten.ordnungszahl,
        selected: false,
        kandidaten: [],
        kurzname: uiWahlvorschlagWithoutKandidaten.kurzname,
        erhaeltStimmen: uiWahlvorschlagWithoutKandidaten.erhaeltStimmen,
        gueltigeStimmen: 0,
        ungueltigeStimmen: 0,
      };

      expect(result.wahlvorschlaege[0]).toStrictEqual(expectedDseWahlvorschlag);
    });

    it("should_createDseStimmzettelWithAllWahlvorschlaege_when_multipleUiWahlvorschlaegeAreGiven", () => {
      const w1: UiWahlvorschlag = createWahlvorschlag();
      const w2: UiWahlvorschlag = prepareWahlvorschlag()
        .identifikator("w2")
        .ordnungszahl(20)
        .build();

      const result: DseStimmzettel = createStimmzettelWithWahlvorschlaege([
        w1,
        w2,
      ]);

      const expectedWahlvorschlaege: DseWahlvorschlag[] = [w1, w2].map((ui) => {
        const dseWahlvorschlag: DseWahlvorschlag = {
          wahlvorschlagID: ui.identifikator,
          ordnungszahl: ui.ordnungszahl,
          selected: false,
          kandidaten: [],
          kurzname: ui.kurzname,
          erhaeltStimmen: ui.erhaeltStimmen,
          gueltigeStimmen: 0,
          ungueltigeStimmen: 0,
        };

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
                ordnungszahl:
                  dseWahlvorschlag.ordnungszahl * 100 +
                  uiKandidat.listenposition,
                einzelstimmen: null,
                durchgestrichen: false,
                reststimmen: null,
                ungueltigeStimmen: null,
                name: uiKandidat.name,
                owningWahlvorschlag: dseWahlvorschlag,
              });
            }
            return kandidaten;
          }) ?? [];

        dseWahlvorschlag.kandidaten = dseKandidaten;
        return dseWahlvorschlag;
      });

      expect(result.wahlvorschlaege).toStrictEqual(expectedWahlvorschlaege);
    });

    it("should_createStimmzettelWithEmptyWahlvorschlaege_when_inputIsEmpty", () => {
      const result: DseStimmzettel = createStimmzettelWithWahlvorschlaege([]);

      const expected: DseStimmzettel = {
        wahlvorstandBeschlussvorschlag: [],
        systemBeschlussvorschlag: [],
        beschlussfassung: null,
        gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
        invalideVotes: 0,
        wahlvorschlaege: [],
      };

      expect(result).toStrictEqual(expected);
    });
  });

  describe("isVorgemerktFuerBeschluss", () => {
    it("should_returnFalse_when_noBeschlussvorschlagPresent", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .systemBeschlussvorschlag([])
        .wahlvorstandBeschlussvorschlag([])
        .build();

      const vorgemerkt = isVorgemerktFuerBeschluss(stimmzettel);

      expect(vorgemerkt).toBe(false);
    });

    it("should_returnTrue_when_wahlvorstandBeschlussvorschlagPresent", () => {
      const text1 = generateRandomString(8);
      const text2 = generateRandomString(10);

      const stimmzettel = preparePersistedStimmzettel()
        .wahlvorstandBeschlussvorschlag([{ text: text1 }, { text: text2 }])
        .build();

      const vorgemerkt = isVorgemerktFuerBeschluss(stimmzettel);

      expect(vorgemerkt).toBe(true);
    });

    it("should_returnTrue_when_systemBeschlussvorschlagPresent", () => {
      const reason1 = getRandomItem(
        Object.values(SystemBeschlussgrundReasonEnum)
      );
      const reason2 = getRandomItem(
        Object.values(SystemBeschlussgrundReasonEnum)
      );

      const stimmzettel = preparePersistedStimmzettel()
        .systemBeschlussvorschlag([{ reason: reason1 }, { reason: reason2 }])
        .build();

      const vorgemerkt = isVorgemerktFuerBeschluss(stimmzettel);

      expect(vorgemerkt).toBe(true);
    });
  });

  describe("getVormerkungsgrund", () => {
    it("should_returnConcatString_when_wahlvorstandAndSystemBeschlussvorschlaegeAreGiven", () => {
      const systemReason = getRandomItem(
        Object.values(SystemBeschlussgrundReasonEnum)
      );
      const wahlvorstandBeschlussvorschlagText = generateRandomString(10);
      const stimmzettel = preparePersistedStimmzettel()
        .systemBeschlussvorschlag([{ reason: systemReason }])
        .wahlvorstandBeschlussvorschlag([
          { text: wahlvorstandBeschlussvorschlagText },
        ])
        .build();

      const result = getVormerkungsgrund(stimmzettel);

      expect(result).toStrictEqual(
        `${mapSystemBeschlussgrundReasonEnumToText(systemReason)}, ${wahlvorstandBeschlussvorschlagText}`
      );
    });

    it("should_returnConcatString_when_multipleSystemBeschlussvorschlaegeAreGiven", () => {
      const systemReason1 = getRandomItem(
        Object.values(SystemBeschlussgrundReasonEnum)
      );
      const systemReason2 = getRandomItem(
        Object.values(SystemBeschlussgrundReasonEnum)
      );
      const stimmzettel = preparePersistedStimmzettel()
        .systemBeschlussvorschlag([
          { reason: systemReason1 },
          { reason: systemReason2 },
        ])
        .wahlvorstandBeschlussvorschlag([])
        .build();

      const result = getVormerkungsgrund(stimmzettel);

      expect(result).toStrictEqual(
        `${mapSystemBeschlussgrundReasonEnumToText(systemReason1)}, ${mapSystemBeschlussgrundReasonEnumToText(systemReason2)}`
      );
    });

    it("should_returnConcatString_when_multipleWahlvorstandBeschlussvorschlaegeAreGiven", () => {
      const wahlvorstandBeschlussvorschlagText1 = generateRandomString(10);
      const wahlvorstandBeschlussvorschlagText2 = generateRandomString(10);
      const stimmzettel = preparePersistedStimmzettel()
        .systemBeschlussvorschlag([])
        .wahlvorstandBeschlussvorschlag([
          { text: wahlvorstandBeschlussvorschlagText1 },
          { text: wahlvorstandBeschlussvorschlagText2 },
        ])
        .build();

      const result = getVormerkungsgrund(stimmzettel);

      expect(result).toStrictEqual(
        `${wahlvorstandBeschlussvorschlagText1}, ${wahlvorstandBeschlussvorschlagText2}`
      );
    });

    it("should_returnStringWithoutSeparator_when_onlyOneWahlvorstandBeschlussvorschlagIsGiven", () => {
      const systemReason = getRandomItem(
        Object.values(SystemBeschlussgrundReasonEnum)
      );
      const stimmzettel = preparePersistedStimmzettel()
        .systemBeschlussvorschlag([{ reason: systemReason }])
        .wahlvorstandBeschlussvorschlag([])
        .build();

      const result = getVormerkungsgrund(stimmzettel);

      expect(result).toStrictEqual(
        `${mapSystemBeschlussgrundReasonEnumToText(systemReason)}`
      );
    });
    it("should_returnStringWithoutSeparator_when_onlyOneSystemBeschlussvorschlagIsGiven", () => {
      const wahlvorstandBeschlussvorschlagText = generateRandomString(10);
      const stimmzettel = preparePersistedStimmzettel()
        .systemBeschlussvorschlag([])
        .wahlvorstandBeschlussvorschlag([
          { text: wahlvorstandBeschlussvorschlagText },
        ])
        .build();

      const result = getVormerkungsgrund(stimmzettel);

      expect(result).toStrictEqual(`${wahlvorstandBeschlussvorschlagText}`);
    });
    it("should_returnEmptyString_when_noBeschlussvorschlaegeAreGiven", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .systemBeschlussvorschlag([])
        .wahlvorstandBeschlussvorschlag([])
        .build();

      const result = getVormerkungsgrund(stimmzettel);

      expect(result).toStrictEqual("");
    });
  });

  describe("normalizePersistedStimmzettel", () => {
    it("should_returnNormalizedStimmzettel_when_givenStimmzettel", () => {
      const sysBeschlussvorschlag = [
        { reason: SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig },
      ];
      const wvBeschlussvorschlag = [{ text: generateRandomString(6) }];

      const wvA = preparePersistedStimmzettelWahlvorschlag()
        .kandidaten([])
        .wahlvorschlagID("a")
        .build();
      const wvB = preparePersistedStimmzettelWahlvorschlag()
        .kandidaten([])
        .wahlvorschlagID("b")
        .build();

      const wahlvorschlaegeSorted = [wvA, wvB];

      mockDefinitions.sortSystemBeschlussgruende.mockReturnValueOnce(
        sysBeschlussvorschlag
      );
      mockDefinitions.sortWahlvorstandBeschlussgruende.mockReturnValueOnce(
        wvBeschlussvorschlag
      );
      mockDefinitions.sortAndDeepCloneWahlvorschlaege.mockReturnValue(
        wahlvorschlaegeSorted
      );

      const input = preparePersistedStimmzettel()
        .systemBeschlussvorschlag(sysBeschlussvorschlag)
        .wahlvorstandBeschlussvorschlag(wvBeschlussvorschlag)
        .wahlvorschlaege([wvB, wvA])
        .build();

      const result = normalizePersistedStimmzettel(input);

      const expectedNormalizedStimmzettel = {
        stimmzettelkennung: input.stimmzettelkennung,
        teamID: input.teamID,
        wahlvorschlaege: wahlvorschlaegeSorted,
        invalideVotes: input.invalideVotes ?? 0,
        gueltigkeit: input.gueltigkeit,
        wahlvorstandBeschlussvorschlag: wvBeschlussvorschlag,
        systemBeschlussvorschlag: sysBeschlussvorschlag,
        beschlussfassung: input.beschlussfassung
          ? { ...input.beschlussfassung }
          : null,
      } satisfies PersistedStimmzettel;

      expect(result).toStrictEqual(expectedNormalizedStimmzettel);
      expect(
        mockDefinitions.sortWahlvorstandBeschlussgruende
      ).toHaveBeenCalledOnce();
      expect(mockDefinitions.sortSystemBeschlussgruende).toHaveBeenCalledOnce();
      expect(
        mockDefinitions.sortAndDeepCloneWahlvorschlaege
      ).toHaveBeenCalledOnce();
    });
  });

  describe("resetDseStimmzettel", () => {
    it("should_returnDseStimmzettelWithoutAnyValuesSet_when_called", () => {
      const dseStimmzettel = createStimmzettel();

      const expectedResetStimmzettel = prepareStimmzettel()
        .invalideVotes(0)
        .gueltigkeit("VALID")
        .wahlvorstandBeschlussvorschlag([])
        .systemBeschlussvorschlag([])
        .beschlussfassung(null)
        .wahlvorschlaege(dseStimmzettel.wahlvorschlaege)
        .build();
      const expectedWahlvorschlaege =
        expectedResetStimmzettel.wahlvorschlaege[0];
      expectedWahlvorschlaege.selected = false;
      const expectedKandidaten = expectedWahlvorschlaege.kandidaten[0];
      expectedKandidaten.einzelstimmen = null;
      expectedKandidaten.ungueltigeStimmen = null;
      expectedKandidaten.reststimmen = null;
      expectedKandidaten.durchgestrichen = false;

      const result = resetDseStimmzettel(dseStimmzettel);

      expect(result).toStrictEqual(expectedResetStimmzettel);
    });
  });

  describe("isSamePersistedStimmzettel", () => {
    it.each([
      {
        stimmzettelkennungMatches: false,
        teamIdMatches: false,
        expected: false,
      },
      {
        stimmzettelkennungMatches: true,
        teamIdMatches: false,
        expected: false,
      },
      {
        stimmzettelkennungMatches: false,
        teamIdMatches: true,
        expected: false,
      },
      {
        stimmzettelkennungMatches: true,
        teamIdMatches: true,
        expected: true,
      },
    ])(
      "should_return'$expected'_whenStimmzettelkennungMatchesIs'$stimmzettelkennungMatches'AndTeamIdMatchesIs'$teamIdMatches'",
      ({ stimmzettelkennungMatches, teamIdMatches, expected }) => {
        const stimmzettel1 = preparePersistedStimmzettel()
          .stimmzettelkennung(1)
          .teamID("A")
          .build();
        const stimmzettel2 = preparePersistedStimmzettel()
          .stimmzettelkennung(
            stimmzettelkennungMatches ? stimmzettel1.stimmzettelkennung : 2
          )
          .teamID(teamIdMatches ? stimmzettel1.teamID : "B")
          .build();

        expect(isSamePersistedStimmzettel(stimmzettel1, stimmzettel2)).toBe(
          expected
        );
      }
    );
  });
});
