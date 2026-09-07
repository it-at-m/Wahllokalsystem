/* eslint-disable  @typescript-eslint/no-non-null-assertion */
/* when we access a null field the test will fail */
import type {
  KandidatDTO,
  StimmzettelOfTeamDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Kandidat } from "@/types/dse/persistedStimmzettel/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/persistedStimmzettel/Wahlvorschlag.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { afterEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelMapper } from "@/composables/dse/stimmzettelerfassung/stimmzettelMapper.ts";

const mockDefinitions = vi.hoisted(() => ({
  hasAnyKennzeichen: vi.fn(),
}));

vi.mock(
  import("@/composables/dse/stimmzettelerfassung/KandidatTools.ts"),
  () => ({
    useKandidatTools: () => ({
      hasAnyKennzeichen: mockDefinitions.hasAnyKennzeichen,
    }),
  })
);

const {
  createStimmzettel,
  createStimmzettelWahlvorschlag,
  createStimmzettelOfTeamDTO,
  prepareStimmzettelOfTeamDTO,
  createStimmzettelKandidatDTO,
  createPersistedStimmzettel,
  preparePersistedStimmzettel,
  createPersistedStimmzettelKandidat,
  preparePersistedStimmzettelBeschlussfassung,
  prepareStimmzettel,
  prepareStimmzettelBeschlussfassungDTO,
  preparePersistedStimmzettelBeschlussgrund,
  prepareStimmzettelBeschlussgrundDTO,
  preparePersistedStimmzettelKandidat,
  prepareStimmzettelKandidat,
  prepareStimmzettelKandidatDTO,
  prepareStimmzettelKandidatIdDTO,
  preparePersistedStimmzettelWahlvorschlag,
  prepareStimmzettelWahlvorschlagDTO,
} = useStimmzettelTestDataFactory();
const { generateRandomNumber, generateRandomString } =
  useCommonTestDataFactory();

describe("stimmzettelMapper.ts", () => {
  const { toModel, toPersistedStimmzettel, toDTO } = useStimmzettelMapper();
  const teamID = "teamID";

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("toModel", () => {
    it("should_mapAllFields_when_dtoIsGiven", () => {
      const dtoToMap = createStimmzettelOfTeamDTO();

      const result: Stimmzettel = toModel(dtoToMap, teamID);

      const expectedResult: Stimmzettel = preparePersistedStimmzettel()
        .stimmzettelkennung(dtoToMap.stimmzettelkennung)
        .teamID(teamID)
        .invalideVotes(dtoToMap.invalideVotes)
        .gueltigkeit(dtoToMap.gueltigkeit)
        .beschlussfassung(
          preparePersistedStimmzettelBeschlussfassung()
            .contra(dtoToMap.beschlussfassung!.contra!)
            .pro(dtoToMap.beschlussfassung!.pro!)
            .text(dtoToMap.beschlussfassung!.text!)
            .build()
        )
        .beschlussvorschlag(
          dtoToMap.wahlvorstandBeschlussvorschlag!.map(
            (dtoWahlvorstandBeschlussgrund) =>
              preparePersistedStimmzettelBeschlussgrund()
                .text(dtoWahlvorstandBeschlussgrund.text)
                .build()
          )
        )
        .wahlvorschlaege(
          dtoToMap.wahlvorschlaege!.map((dtoWahlvorschlag) =>
            preparePersistedStimmzettelWahlvorschlag()
              .wahlvorschlagID(dtoWahlvorschlag.wahlvorschlagID)
              .selected(dtoWahlvorschlag.selected)
              .kandidaten(
                dtoWahlvorschlag.kandidaten!.map((dtoKandidat) =>
                  preparePersistedStimmzettelKandidat()
                    .isDiscarded(dtoKandidat.discarded)
                    .invalidVotes(dtoKandidat.invalidVotes!)
                    .votesByVoter(dtoKandidat.votesByVoter!)
                    .kandidatId(dtoKandidat.id.kandidatID)
                    .votesByWahlvorschlag(dtoKandidat.votesByWahlvorschlag!)
                    .nennung(dtoKandidat.id.nennungsNummer)
                    .build()
                )
              )
              .build()
          )
        )
        .build();
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnEmptyWahlvorschlaege_when_dtoWahlvorschlaegeIsUndefined", () => {
      const dtoWithoutWahlvorschlaege = prepareStimmzettelOfTeamDTO()
        .wahlvorschlaege(undefined)
        .build();

      const result = toModel(dtoWithoutWahlvorschlaege, teamID);

      expect(result.wahlvorschlaege).toStrictEqual([]);
    });

    it("should_returnEmptyBeschlussvorschlag_when_dtoBeschlussvorschlagIsUndefined", () => {
      const dtoWithoutBeschlussvorschlag = prepareStimmzettelOfTeamDTO()
        .wahlvorstandBeschlussvorschlag(undefined)
        .build();

      const result = toModel(dtoWithoutBeschlussvorschlag, teamID);

      expect(result.beschlussvorschlag).toStrictEqual([]);
    });

    it("should_returnEmptyBeschlussvorschlag_when_dtoBeschlussvorschlagIsEmpty", () => {
      const dtoWithoutBeschlussvorschlag = prepareStimmzettelOfTeamDTO()
        .wahlvorstandBeschlussvorschlag([])
        .build();

      const result = toModel(dtoWithoutBeschlussvorschlag, teamID);

      expect(result.beschlussvorschlag).toStrictEqual([]);
    });

    it("should_returnNullBeschlussfassung_when_dtoBeschlussfassungIsUndefined", () => {
      const dtoWithoutBeschlussfassung = prepareStimmzettelOfTeamDTO()
        .beschlussfassung(undefined)
        .build();

      const result = toModel(dtoWithoutBeschlussfassung, teamID);

      expect(result.beschlussfassung).toBeNull();
    });

    it("should_handleEmptyWahlvorschlaegeArray_when_dtoWahlvorschlaegeIsEmpty", () => {
      const dtoWithEmptyWahlvorschlaege = prepareStimmzettelOfTeamDTO()
        .wahlvorschlaege([])
        .build();

      const result = toModel(dtoWithEmptyWahlvorschlaege, teamID);

      expect(result.wahlvorschlaege).toStrictEqual([]);
    });

    it("should_handleEmptyKandidatenArray_when_dtoKandidatenIsEmpty", () => {
      const dtoWithEmptyKandidaten = prepareStimmzettelOfTeamDTO()
        .wahlvorschlaege([
          prepareStimmzettelWahlvorschlagDTO().kandidaten([]).build(),
        ])
        .build();

      const result = toModel(dtoWithEmptyKandidaten, teamID);

      expect(result.wahlvorschlaege[0].kandidaten).toStrictEqual([]);
    });

    it("should_returnEmptyKandidaten_when_dtoKandidatenIsUndefined", () => {
      const dtoWithUndefinedKandidaten = prepareStimmzettelOfTeamDTO()
        .wahlvorschlaege([
          prepareStimmzettelWahlvorschlagDTO().kandidaten(undefined).build(),
        ])
        .build();

      const result = toModel(dtoWithUndefinedKandidaten, teamID);

      expect(result.wahlvorschlaege[0].kandidaten).toStrictEqual([]);
    });

    it("should_mapSingleKandidatCorrectly_when_oneKandidatIsGiven", () => {
      const singleKandidat = createStimmzettelKandidatDTO();
      const dtoWithSingleKandidat = prepareStimmzettelOfTeamDTO()
        .wahlvorschlaege([
          prepareStimmzettelWahlvorschlagDTO()
            .kandidaten([singleKandidat])
            .build(),
        ])
        .build();

      const result = toModel(dtoWithSingleKandidat, teamID);

      const expectedKandidat = preparePersistedStimmzettelKandidat()
        .kandidatId(singleKandidat.id.kandidatID)
        .nennung(singleKandidat.id.nennungsNummer)
        .isDiscarded(singleKandidat.discarded)
        .invalidVotes(singleKandidat.invalidVotes!)
        .votesByVoter(singleKandidat.votesByVoter!)
        .votesByWahlvorschlag(singleKandidat.votesByWahlvorschlag!)
        .build();
      expect(result.wahlvorschlaege[0].kandidaten).toStrictEqual([
        expectedKandidat,
      ]);
    });

    it("should_mapUndefinedVoteFieldsToNull_when_kandidatVotesAreUndefined", () => {
      const kandidatWithoutVotes: KandidatDTO = prepareStimmzettelKandidatDTO()
        .votesByVoter(undefined)
        .invalidVotes(undefined)
        .votesByWahlvorschlag(undefined)
        .build();
      const dtoWithKandidatWithoutVotes = prepareStimmzettelOfTeamDTO()
        .wahlvorschlaege([
          prepareStimmzettelWahlvorschlagDTO()
            .kandidaten([kandidatWithoutVotes])
            .build(),
        ])
        .build();

      const result = toModel(dtoWithKandidatWithoutVotes, teamID);

      expect(result.wahlvorschlaege[0].kandidaten[0].votesByVoter).toBeNull();
      expect(result.wahlvorschlaege[0].kandidaten[0].invalidVotes).toBeNull();
      expect(
        result.wahlvorschlaege[0].kandidaten[0].votesByWahlvorschlag
      ).toBeNull();
    });
  });

  describe("toDTO", () => {
    it("should_mapAllFields_when_modelIsGiven", () => {
      const modelToMap = createPersistedStimmzettel();

      const result: StimmzettelOfTeamDTO = toDTO(modelToMap);

      const expectedResult: StimmzettelOfTeamDTO = prepareStimmzettelOfTeamDTO()
        .stimmzettelkennung(modelToMap.stimmzettelkennung)
        .invalideVotes(modelToMap.invalideVotes)
        .gueltigkeit(modelToMap.gueltigkeit)
        .beschlussfassung(
          prepareStimmzettelBeschlussfassungDTO()
            .pro(modelToMap.beschlussfassung!.pro!)
            .contra(modelToMap.beschlussfassung!.contra!)
            .text(modelToMap.beschlussfassung!.text!)
            .build()
        )
        .wahlvorstandBeschlussvorschlag(
          modelToMap.beschlussvorschlag.map((modelBeschlussgrund) =>
            prepareStimmzettelBeschlussgrundDTO()
              .text(modelBeschlussgrund.text)
              .build()
          )
        )
        .wahlvorschlaege(
          modelToMap.wahlvorschlaege.map((modelWahlvorschlag) =>
            prepareStimmzettelWahlvorschlagDTO()
              .selected(modelWahlvorschlag.selected)
              .wahlvorschlagID(modelWahlvorschlag.wahlvorschlagID)
              .kandidaten(
                modelWahlvorschlag.kandidaten.map((modelKandidat) =>
                  prepareStimmzettelKandidatDTO()
                    .id(
                      prepareStimmzettelKandidatIdDTO()
                        .kandidatID(modelKandidat.kandidatId)
                        .nennungsNummer(modelKandidat.nennung)
                        .build()
                    )
                    .discarded(modelKandidat.isDiscarded)
                    .invalidVotes(modelKandidat.invalidVotes!)
                    .votesByVoter(modelKandidat.votesByVoter!)
                    .votesByWahlvorschlag(modelKandidat.votesByWahlvorschlag!)
                    .build()
                )
              )
              .build()
          )
        )
        .build();
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnUndefinedWahlvorschlaege_when_modelWahlvorschlaegeIsEmpty", () => {
      const modelWithEmptyWahlvorschlaege = preparePersistedStimmzettel()
        .wahlvorschlaege([])
        .build();

      const result = toDTO(modelWithEmptyWahlvorschlaege);

      expect(result.wahlvorschlaege).toBeUndefined();
    });

    it("should_returnUndefinedBeschlussvorschlag_when_modelBeschlussvorschlagIsEmpty", () => {
      const modelWithoutBeschlussvorschlag = preparePersistedStimmzettel()
        .beschlussvorschlag([])
        .build();

      const result = toDTO(modelWithoutBeschlussvorschlag);

      expect(result.wahlvorstandBeschlussvorschlag).toBeUndefined();
    });

    it("should_returnUndefinedBeschlussfassung_when_modelBeschlussfassungIsNull", () => {
      const modelWithoutBeschlussfassung = preparePersistedStimmzettel()
        .beschlussfassung(null)
        .build();

      const result = toDTO(modelWithoutBeschlussfassung);

      expect(result.beschlussfassung).toBeUndefined();
    });

    it("should_handleEmptyKandidatenArray_when_modelKandidatenIsEmpty", () => {
      const modelWithEmptyKandidaten = preparePersistedStimmzettel()
        .wahlvorschlaege([
          preparePersistedStimmzettelWahlvorschlag().kandidaten([]).build(),
        ])
        .build();

      const result = toDTO(modelWithEmptyKandidaten);

      expect(result.wahlvorschlaege?.[0].kandidaten).toStrictEqual([]);
    });

    it("should_mapSingleKandidatCorrectly_when_oneKandidatIsGiven", () => {
      const singleKandidat = createPersistedStimmzettelKandidat();
      const modelWithSingleKandidat = preparePersistedStimmzettel()
        .wahlvorschlaege([
          preparePersistedStimmzettelWahlvorschlag()
            .kandidaten([singleKandidat])
            .build(),
        ])
        .build();

      const result = toDTO(modelWithSingleKandidat);

      const expectedKandidatDTO = prepareStimmzettelKandidatDTO()
        .id(
          prepareStimmzettelKandidatIdDTO()
            .kandidatID(singleKandidat.kandidatId)
            .nennungsNummer(singleKandidat.nennung)
            .build()
        )
        .discarded(singleKandidat.isDiscarded)
        .invalidVotes(singleKandidat.invalidVotes!)
        .votesByVoter(singleKandidat.votesByVoter!)
        .votesByWahlvorschlag(singleKandidat.votesByWahlvorschlag!)
        .build();
      expect(result.wahlvorschlaege?.[0].kandidaten).toStrictEqual([
        expectedKandidatDTO,
      ]);
    });

    it("should_mapNullVoteFieldsToUndefined_when_kandidatVotesAreNull", () => {
      const kandidatWithoutVotes: Kandidat =
        preparePersistedStimmzettelKandidat()
          .votesByVoter(null)
          .invalidVotes(null)
          .votesByWahlvorschlag(null)
          .build();
      const modelWithKandidatWithoutVotes = preparePersistedStimmzettel()
        .wahlvorschlaege([
          preparePersistedStimmzettelWahlvorschlag()
            .kandidaten([kandidatWithoutVotes])
            .build(),
        ])
        .build();

      const result = toDTO(modelWithKandidatWithoutVotes);

      expect(
        result.wahlvorschlaege?.[0].kandidaten?.[0].votesByVoter
      ).toBeUndefined();
      expect(
        result.wahlvorschlaege?.[0].kandidaten?.[0].votesByWahlvorschlag
      ).toBeUndefined();
      expect(
        result.wahlvorschlaege?.[0].kandidaten?.[0].invalidVotes
      ).toBeUndefined();
    });
  });

  describe("toPersistedStimmzettel", () => {
    it("should_returnPersistedStimmzettel_when_dseStimmzettelIsGiven", () => {
      const stimmzettelkennung = generateRandomNumber(2);
      const teamID = generateRandomString(10);
      const dseStimmzettel = createStimmzettel();

      mockDefinitions.hasAnyKennzeichen.mockReturnValue(true);

      const result = toPersistedStimmzettel(
        dseStimmzettel,
        stimmzettelkennung,
        teamID
      );

      const expectedWahlvorschlaege: Wahlvorschlag[] =
        dseStimmzettel.wahlvorschlaege.map((wahlvorschlag) => {
          const kandidaten: Kandidat[] = wahlvorschlag.kandidaten.map(
            (kandidat) => ({
              votesByWahlvorschlag: kandidat.reststimmen,
              invalidVotes: kandidat.ungueltigeStimmen,
              votesByVoter: kandidat.einzelstimmen,
              isDiscarded: kandidat.durchgestrichen,
              nennung: kandidat.nennung,
              kandidatId: kandidat.kandidatId,
            })
          );
          return {
            kandidaten,
            selected: wahlvorschlag.selected,
            wahlvorschlagID: wahlvorschlag.wahlvorschlagID,
          };
        });
      const expectedResult: Stimmzettel = {
        stimmzettelkennung,
        teamID,
        beschlussfassung: dseStimmzettel.beschlussfassung,
        beschlussvorschlag: [],
        invalideVotes: dseStimmzettel.invalideVotes,
        wahlvorschlaege: expectedWahlvorschlaege,
        gueltigkeit: dseStimmzettel.gueltigkeit,
      };
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnPersistedStimmzettelWithReducedData_when_stimmzettelHasWahlvorschlagWithOnlyKandidatenWithoutAnyKennzeichen", () => {
      const stimmzettelkennung = generateRandomNumber(2);
      const teamID = generateRandomString(10);

      const dseStimmzettel = prepareStimmzettel()
        .wahlvorschlaege([createStimmzettelWahlvorschlag()])
        .build();

      mockDefinitions.hasAnyKennzeichen.mockReturnValue(false);

      expect(
        dseStimmzettel.wahlvorschlaege[0].kandidaten.length > 0
      ).toStrictEqual(true);

      const result = toPersistedStimmzettel(
        dseStimmzettel,
        stimmzettelkennung,
        teamID
      );

      expect(result.wahlvorschlaege).toStrictEqual([]);
    });

    it("should_returnPersistedStimmzettelWithReducedData_when_stimmzettelHasWahlvorschlaegWithKandidatenWithAndWithoutAnyKennzeichen", () => {
      const stimmzettelkennung = generateRandomNumber(2);
      const teamID = generateRandomString(10);

      const wahlvorschlag = createStimmzettelWahlvorschlag();
      const kandidatWithKennzeichen = prepareStimmzettelKandidat(wahlvorschlag)
        .kandidatId("k1")
        .build();
      const kandidatWithoutKennzeichen = prepareStimmzettelKandidat(
        wahlvorschlag
      )
        .kandidatId("k2")
        .build();
      wahlvorschlag.kandidaten = [
        kandidatWithKennzeichen,
        kandidatWithoutKennzeichen,
      ];

      const dseStimmzettel = prepareStimmzettel()
        .wahlvorschlaege([wahlvorschlag])
        .build();

      mockDefinitions.hasAnyKennzeichen.mockImplementation(
        (kandidat: Kandidat) =>
          kandidat.kandidatId === kandidatWithKennzeichen.kandidatId
      );

      expect(
        dseStimmzettel.wahlvorschlaege[0].kandidaten.length > 0
      ).toStrictEqual(true);

      const result = toPersistedStimmzettel(
        dseStimmzettel,
        stimmzettelkennung,
        teamID
      );

      const expectedKandidat: Kandidat = {
        kandidatId: kandidatWithKennzeichen.kandidatId,
        nennung: kandidatWithKennzeichen.nennung,
        votesByWahlvorschlag: kandidatWithKennzeichen.reststimmen,
        invalidVotes: kandidatWithKennzeichen.ungueltigeStimmen,
        votesByVoter: kandidatWithKennzeichen.einzelstimmen,
        isDiscarded: kandidatWithKennzeichen.durchgestrichen,
      };

      expect(result.wahlvorschlaege.length).toStrictEqual(1);
      expect(result.wahlvorschlaege[0].kandidaten).toStrictEqual([
        expectedKandidat,
      ]);
    });
  });
});
