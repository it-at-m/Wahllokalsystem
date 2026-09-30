import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { usePersistedStimmzettelTools } from "@/composables/dse/stimmzettelerfassung/PersistedStimmzettelTools.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  hasOnlyReststimme: vi.fn(),
}));

vi.mock(
  import("@/composables/dse/stimmzettelerfassung/kandidatTools.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useKandidatTools: () => ({
        ...mod.useKandidatTools(),
        hasOnlyReststimme: mockDefinitions.hasOnlyReststimme,
      }),
    };
  }
);

const {
  createPersistedStimmzettelWahlvorschlag,
  createPersistedStimmzettelKandidat,
  preparePersistedStimmzettel,
  preparePersistedStimmzettelBeschlussfassung,
  preparePersistedStimmzettelKandidat,
  preparePersistedStimmzettelWahlvorschlag,
} = usePersistedStimmzettelTestDataFactory();
const { generateRandomNumber } = useCommonTestDataFactory();

describe("persistedStimmzettelTools.ts", () => {
  let unitUnderTest: ReturnType<typeof usePersistedStimmzettelTools>;

  const nonValidGueltigkeiten = Object.values(
    StimmzettelGueltigkeitEnum
  ).filter((gueltigkeit) => gueltigkeit !== StimmzettelGueltigkeitEnum.Valid);

  beforeEach(() => {
    unitUnderTest = usePersistedStimmzettelTools();
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("matchesMBWStapelA", () => {
    it("should_returnTrue_when_stimmzettelIsValidAndContainsOnlyReststimmen", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
        .invalideVotes(0)
        .wahlvorschlaege([createPersistedStimmzettelWahlvorschlag()])
        .build();
      mockDefinitions.hasOnlyReststimme.mockReturnValue(true);

      expect(unitUnderTest.matchesMBWStapelA(stimmzettel)).toStrictEqual(true);
    });

    it.each(nonValidGueltigkeiten)(
      "should_returnFalse_when_stimmzettelHasOnlyOneWahlvorschlagWithOnlyReststimmenButIsNotValidWith%s",
      (nonValidGueltigkeit) => {
        const stimmzettel = preparePersistedStimmzettel()
          .gueltigkeit(nonValidGueltigkeit)
          .invalideVotes(0)
          .wahlvorschlaege([createPersistedStimmzettelWahlvorschlag()])
          .build();
        mockDefinitions.hasOnlyReststimme.mockReturnValue(true);

        expect(unitUnderTest.matchesMBWStapelA(stimmzettel)).toStrictEqual(
          false
        );
      }
    );

    it.each([
      {
        text: "gueltigkeitIsNotValid",
        gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
        invalideVotes: 0,
        wahlvorschlaegeCount: 1,
        hasOnlyReststimme: true,
      },
      {
        text: "invalideVotesArePresent",
        gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
        invalideVotes: 1,
        wahlvorschlaegeCount: 1,
        hasOnlyReststimme: true,
      },
      {
        text: "multipleWahlvorschlaegeArePresent",
        gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
        invalideVotes: 0,
        wahlvorschlaegeCount: 2,
        hasOnlyReststimme: true,
      },
      {
        text: "kandidatDoesNotHaveOnlyReststimme",
        gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
        invalideVotes: 0,
        wahlvorschlaegeCount: 1,
        hasOnlyReststimme: false,
      },
    ])(
      "should_returnFalse_when_$text",
      ({
        gueltigkeit,
        invalideVotes,
        wahlvorschlaegeCount,
        hasOnlyReststimme,
      }) => {
        const wahlvorschlaege = Array.from(
          { length: wahlvorschlaegeCount },
          () =>
            preparePersistedStimmzettelWahlvorschlag()
              .kandidaten([preparePersistedStimmzettelKandidat().build()])
              .build()
        );
        const stimmzettel = preparePersistedStimmzettel()
          .gueltigkeit(gueltigkeit)
          .invalideVotes(invalideVotes)
          .wahlvorschlaege(wahlvorschlaege)
          .build();
        mockDefinitions.hasOnlyReststimme.mockReturnValue(hasOnlyReststimme);

        expect(unitUnderTest.matchesMBWStapelA(stimmzettel)).toStrictEqual(
          false
        );
      }
    );

    it("should_returnFalse_when_atLeastOnKandidatHasNotOnlyReststimmen", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
        .invalideVotes(0)
        .wahlvorschlaege([
          preparePersistedStimmzettelWahlvorschlag()
            .kandidaten([
              createPersistedStimmzettelKandidat(),
              createPersistedStimmzettelKandidat(),
              createPersistedStimmzettelKandidat(),
            ])
            .build(),
        ])
        .build();
      mockDefinitions.hasOnlyReststimme.mockReturnValueOnce(true);
      mockDefinitions.hasOnlyReststimme.mockReturnValueOnce(true);
      mockDefinitions.hasOnlyReststimme.mockReturnValueOnce(false);

      expect(unitUnderTest.matchesMBWStapelA(stimmzettel)).toStrictEqual(false);
    });
  });

  describe("matchesMBWStapelB", () => {
    it("should_returnTrue_when_stimmzettelIsValidAndContainsNotOnlyReststimmen", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
        .invalideVotes(generateRandomNumber(2))
        .wahlvorschlaege([
          preparePersistedStimmzettelWahlvorschlag()
            .kandidaten([
              createPersistedStimmzettelKandidat(),
              createPersistedStimmzettelKandidat(),
              createPersistedStimmzettelKandidat(),
            ])
            .build(),
        ])
        .build();

      mockDefinitions.hasOnlyReststimme.mockReturnValueOnce(true);
      mockDefinitions.hasOnlyReststimme.mockReturnValueOnce(true);
      mockDefinitions.hasOnlyReststimme.mockReturnValueOnce(false);

      expect(unitUnderTest.matchesMBWStapelB(stimmzettel)).toStrictEqual(true);
    });

    it.each(nonValidGueltigkeiten)(
      "should_returnFalse_when_stimmzettelWahlvorschlagWithNotOnlyReststimmenButIsNotValid%s",
      (nonValidGueltigkeit) => {
        const stimmzettel = preparePersistedStimmzettel()
          .gueltigkeit(nonValidGueltigkeit)
          .invalideVotes(1)
          .wahlvorschlaege([createPersistedStimmzettelWahlvorschlag()])
          .build();

        mockDefinitions.hasOnlyReststimme.mockReturnValueOnce(false);

        expect(unitUnderTest.matchesMBWStapelB(stimmzettel)).toStrictEqual(
          false
        );
      }
    );

    it.each([
      {
        text: "multipleWahlvorschlaegeArePresent",
        gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
        wahlvorschlaegeCount: 2,
        hasOnlyReststimme: false,
      },
      {
        text: "kandidatHasOnlyReststimme",
        gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
        wahlvorschlaegeCount: 1,
        hasOnlyReststimme: true,
      },
    ])(
      "should_returnFalse_when_$text",
      ({ gueltigkeit, wahlvorschlaegeCount, hasOnlyReststimme }) => {
        const stimmzettel = preparePersistedStimmzettel()
          .gueltigkeit(gueltigkeit)
          .wahlvorschlaege(
            Array.from({ length: wahlvorschlaegeCount }, () =>
              preparePersistedStimmzettelWahlvorschlag()
                .kandidaten([preparePersistedStimmzettelKandidat().build()])
                .build()
            )
          )
          .build();
        mockDefinitions.hasOnlyReststimme.mockReturnValue(hasOnlyReststimme);

        expect(unitUnderTest.matchesMBWStapelB(stimmzettel)).toStrictEqual(
          false
        );
      }
    );

    it.each(nonValidGueltigkeiten)(
      "should_returnFalse_whenGueltigkeitIs%s",
      (nonValidGueltigkeit) => {
        const kandidaten = [
          preparePersistedStimmzettelKandidat().build(),
          preparePersistedStimmzettelKandidat().build(),
        ];
        const stimmzettel = preparePersistedStimmzettel()
          .gueltigkeit(nonValidGueltigkeit)
          .invalideVotes(1)
          .wahlvorschlaege([
            preparePersistedStimmzettelWahlvorschlag()
              .kandidaten(kandidaten)
              .build(),
          ])
          .build();

        mockDefinitions.hasOnlyReststimme.mockReturnValue(false);

        expect(unitUnderTest.matchesMBWStapelB(stimmzettel)).toStrictEqual(
          false
        );
      }
    );
  });

  describe("matchesMBWStapelBC", () => {
    it("should_returnTrue_when_stimmzettelIsValidButDoesNotMatchStapelAWithInvalideVotes", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
        .invalideVotes(1)
        .wahlvorschlaege([
          preparePersistedStimmzettelWahlvorschlag()
            .kandidaten([preparePersistedStimmzettelKandidat().build()])
            .build(),
        ])
        .build();
      mockDefinitions.hasOnlyReststimme.mockReturnValue(true);

      expect(unitUnderTest.matchesMBWStapelBC(stimmzettel)).toStrictEqual(true);
    });

    it("should_returnTrue_when_stimmzettelIsValidButDoesNotMatchStapelAWithMoreThanOneWahlvorschlagWithOnlyReststimmen", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
        .invalideVotes(0)
        .wahlvorschlaege([
          createPersistedStimmzettelWahlvorschlag(),
          createPersistedStimmzettelWahlvorschlag(),
        ])
        .build();
      mockDefinitions.hasOnlyReststimme.mockReturnValue(true);

      expect(unitUnderTest.matchesMBWStapelBC(stimmzettel)).toStrictEqual(true);
    });

    it("should_returnTrue_when_stimmzettelIsValidButDoesNotMatchStapelAWithMoreOneWahlvorschlagButNotOnlyReststimmen", () => {
      const stimmzettel = preparePersistedStimmzettel()
        .gueltigkeit(StimmzettelGueltigkeitEnum.Valid)
        .invalideVotes(0)
        .wahlvorschlaege([
          createPersistedStimmzettelWahlvorschlag(),
          createPersistedStimmzettelWahlvorschlag(),
        ])
        .build();
      mockDefinitions.hasOnlyReststimme.mockReturnValue(false);

      expect(unitUnderTest.matchesMBWStapelBC(stimmzettel)).toStrictEqual(true);
    });

    it.each(nonValidGueltigkeiten)(
      "should_returnFalse_when_stimmzettelDoesNotMatchStapelAButIsNotValidWith%s",
      (nonValidGueltigkeit) => {
        const stimmzettel = preparePersistedStimmzettel()
          .gueltigkeit(nonValidGueltigkeit)
          .invalideVotes(1)
          .wahlvorschlaege([
            preparePersistedStimmzettelWahlvorschlag()
              .kandidaten([preparePersistedStimmzettelKandidat().build()])
              .build(),
          ])
          .build();
        mockDefinitions.hasOnlyReststimme.mockReturnValue(true);

        expect(unitUnderTest.matchesMBWStapelBC(stimmzettel)).toStrictEqual(
          false
        );
      }
    );

    it.each([
      {
        text: "stimmzettelMatchesStapelA",
        gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
        invalideVotes: 0,
      },
      {
        text: "stimmzettelIsNotValid",
        gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
        invalideVotes: 1,
      },
    ])("should_returnFalse_when_$text", ({ gueltigkeit, invalideVotes }) => {
      const stimmzettel = preparePersistedStimmzettel()
        .gueltigkeit(gueltigkeit)
        .invalideVotes(invalideVotes)
        .wahlvorschlaege([
          preparePersistedStimmzettelWahlvorschlag()
            .kandidaten([preparePersistedStimmzettelKandidat().build()])
            .build(),
        ])
        .build();
      mockDefinitions.hasOnlyReststimme.mockReturnValue(true);

      expect(unitUnderTest.matchesMBWStapelBC(stimmzettel)).toStrictEqual(
        false
      );
    });
  });

  describe("matchesMBWStapelDUngueltig", () => {
    it.each([
      {
        text: "stimmzettelIsLeer",
        gueltigkeit: StimmzettelGueltigkeitEnum.Leer,
        beschlussfassung: null,
        expected: true,
      },
      {
        text: "stimmzettelIsLeer",
        gueltigkeit:
          StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag,
        beschlussfassung: null,
        expected: true,
      },
      {
        text: "invalidStimmzettelHasNoBeschlussfassung",
        gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
        beschlussfassung: null,
        expected: true,
      },
      {
        text: "invalidStimmzettelHasBeschlussfassung",
        gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
        beschlussfassung: preparePersistedStimmzettelBeschlussfassung().build(),
        expected: false,
      },
      {
        text: "stimmzettelIsValid",
        gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
        beschlussfassung: null,
        expected: false,
      },
    ])(
      "should_return'$expected'_when_$text",
      ({ gueltigkeit, beschlussfassung, expected }) => {
        const stimmzettel = preparePersistedStimmzettel()
          .gueltigkeit(gueltigkeit)
          .beschlussfassung(beschlussfassung)
          .build();

        expect(
          unitUnderTest.matchesMBWStapelDUngueltig(stimmzettel)
        ).toStrictEqual(expected);
      }
    );
  });

  describe("matchesMBWStapelEUngueltig", () => {
    it.each([
      {
        text: "stimmzettelIsLeer",
        gueltigkeit: StimmzettelGueltigkeitEnum.Leer,
        beschlussfassung: null,
        expected: false,
      },
      {
        text: "invalidStimmzettelHasBeschlussfassung",
        gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
        beschlussfassung: preparePersistedStimmzettelBeschlussfassung().build(),
        expected: true,
      },
      {
        text: "invalidStimmzettelHasNoBeschlussfassung",
        gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
        beschlussfassung: null,
        expected: false,
      },
      {
        text: "stimmzettelIsValid",
        gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
        beschlussfassung: preparePersistedStimmzettelBeschlussfassung().build(),
        expected: false,
      },
    ])(
      "should_return'$expected'_when_$text",
      ({ gueltigkeit, beschlussfassung, expected }) => {
        const stimmzettel = preparePersistedStimmzettel()
          .gueltigkeit(gueltigkeit)
          .beschlussfassung(beschlussfassung)
          .build();

        expect(
          unitUnderTest.matchesMBWStapelEUngueltig(stimmzettel)
        ).toStrictEqual(expected);
      }
    );
  });
});
