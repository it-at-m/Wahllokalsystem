import type {
  KandidatDTO,
  StimmzettelOfTeamDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Kandidat } from "@/types/dse/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";

import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useStimmzettelMapper } from "@/composables/dse/stimmzettelMapper.ts";

const {
  createStimmzettelOfTeamDTO,
  prepareStimmzettelOfTeamDTO,
  createStimmzettelKandidatDTO,
  createStimmzettel,
  prepareStimmzettel,
  createStimmzettelKandidat,
  prepareStimmzettelKandidat,
  prepareStimmzettelKandidatDTO,
  prepareStimmzettelWahlvorschlag,
  prepareStimmzettelWahlvorschlagDTO,
} = useStimmzettelTestDataFactory();

describe("stimmzettelMapper.ts", () => {
  const { toModel, toDTO } = useStimmzettelMapper();

  describe("toModel", () => {
    it("should_mapAllFields_when_dtoIsGiven", () => {
      const dtoToMap = createStimmzettelOfTeamDTO();

      const result: Stimmzettel = toModel(dtoToMap);

      const expectedResult: Stimmzettel = {
        stimmzettelkennung: dtoToMap.stimmzettelkennung,
        invalideVotes: dtoToMap.invalideVotes,
        gueltigkeit: dtoToMap.gueltigkeit,
        beschlussfassung: {
          // @ts-expect-error source possible undefined
          contra: dtoToMap.beschlussfassung?.contra,
          // @ts-expect-error source possible undefined
          pro: dtoToMap.beschlussfassung?.pro,
          // @ts-expect-error source possible undefined
          text: dtoToMap.beschlussfassung?.text,
        },
        // @ts-expect-error source has possible undefined values
        beschlussvorschlag: dtoToMap.beschlussvorschlag?.map(
          (beschlussgrund) => ({
            text: beschlussgrund.text,
          })
        ),
        // @ts-expect-error source has possible undefined values
        wahlvorschlaege: dtoToMap.wahlvorschlaege?.map((wahlvorschlag) => ({
          wahlvorschlagID: wahlvorschlag.wahlvorschlagID,
          selected: wahlvorschlag.selected,
          kandidaten: wahlvorschlag.kandidaten?.map((kandidat) => ({
            kandidatId: kandidat.id.kandidatID,
            nennung: kandidat.id.nennungsNummer,
            isDiscarded: kandidat.discarded,
            votesByVoter: kandidat.votesByVoter,
            invalidVotes: kandidat.invalidVotes,
            votesByWahlvorschlag: kandidat.votesByWahlvorschlag,
          })),
        })),
      };

      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnEmptyWahlvorschlaege_when_dtoWahlvorschlaegeIsUndefined", () => {
      const dtoWithoutWahlvorschlaege = prepareStimmzettelOfTeamDTO()
        .wahlvorschlaege(undefined)
        .build();

      const result = toModel(dtoWithoutWahlvorschlaege);

      expect(result.wahlvorschlaege).toStrictEqual([]);
    });

    it("should_returnEmptyBeschlussvorschlag_when_dtoBeschlussvorschlagIsUndefined", () => {
      const dtoWithoutBeschlussvorschlag = prepareStimmzettelOfTeamDTO()
        .beschlussvorschlag(undefined)
        .build();

      const result = toModel(dtoWithoutBeschlussvorschlag);

      expect(result.beschlussvorschlag).toStrictEqual([]);
    });

    it("should_returnEmptyBeschlussvorschlag_when_dtoBeschlussvorschlagIsEmpty", () => {
      const dtoWithoutBeschlussvorschlag = prepareStimmzettelOfTeamDTO()
        .beschlussvorschlag([])
        .build();

      const result = toModel(dtoWithoutBeschlussvorschlag);

      expect(result.beschlussvorschlag).toStrictEqual([]);
    });

    it("should_returnNullBeschlussfassung_when_dtoBeschlussfassungIsUndefined", () => {
      const dtoWithoutBeschlussfassung = prepareStimmzettelOfTeamDTO()
        .beschlussfassung(undefined)
        .build();

      const result = toModel(dtoWithoutBeschlussfassung);

      expect(result.beschlussfassung).toBeNull();
    });

    it("should_handleEmptyWahlvorschlaegeArray_when_dtoWahlvorschlaegeIsEmpty", () => {
      const dtoWithEmptyWahlvorschlaege = prepareStimmzettelOfTeamDTO()
        .wahlvorschlaege([])
        .build();

      const result = toModel(dtoWithEmptyWahlvorschlaege);

      expect(result.wahlvorschlaege).toStrictEqual([]);
    });

    it("should_handleEmptyKandidatenArray_when_dtoKandidatenIsEmpty", () => {
      const dtoWithEmptyKandidaten = prepareStimmzettelOfTeamDTO()
        .wahlvorschlaege([
          prepareStimmzettelWahlvorschlagDTO().kandidaten([]).build(),
        ])
        .build();

      const result = toModel(dtoWithEmptyKandidaten);

      expect(result.wahlvorschlaege[0].kandidaten).toStrictEqual([]);
    });

    it("should_returnEmptyKandidaten_when_dtoKandidatenIsUndefined", () => {
      const dtoWithUndefinedKandidaten = prepareStimmzettelOfTeamDTO()
        .wahlvorschlaege([
          prepareStimmzettelWahlvorschlagDTO().kandidaten(undefined).build(),
        ])
        .build();

      const result = toModel(dtoWithUndefinedKandidaten);

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

      const result = toModel(dtoWithSingleKandidat);

      expect(result.wahlvorschlaege[0].kandidaten).toHaveLength(1);
      expect(result.wahlvorschlaege[0].kandidaten[0]).toStrictEqual({
        kandidatId: singleKandidat.id.kandidatID,
        nennung: singleKandidat.id.nennungsNummer,
        isDiscarded: singleKandidat.discarded,
        votesByVoter: singleKandidat.votesByVoter,
        invalidVotes: singleKandidat.invalidVotes,
        votesByWahlvorschlag: singleKandidat.votesByWahlvorschlag,
      });
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

      const result = toModel(dtoWithKandidatWithoutVotes);

      expect(result.wahlvorschlaege[0].kandidaten[0].votesByVoter).toBeNull();
      expect(result.wahlvorschlaege[0].kandidaten[0].invalidVotes).toBeNull();
      expect(
        result.wahlvorschlaege[0].kandidaten[0].votesByWahlvorschlag
      ).toBeNull();
    });
  });

  describe("toDTO", () => {
    it("should_mapAllFields_when_modelIsGiven", () => {
      const modelToMap = createStimmzettel();

      const result: StimmzettelOfTeamDTO = toDTO(modelToMap);

      const expectedResult: StimmzettelOfTeamDTO = {
        stimmzettelkennung: modelToMap.stimmzettelkennung,
        invalideVotes: modelToMap.invalideVotes,
        gueltigkeit: modelToMap.gueltigkeit,
        beschlussfassung: {
          // @ts-expect-error source possible undefined
          contra: modelToMap.beschlussfassung?.contra,
          // @ts-expect-error source possible undefined
          pro: modelToMap.beschlussfassung?.pro,
          // @ts-expect-error source possible undefined
          text: modelToMap.beschlussfassung?.text,
        },
        beschlussvorschlag: modelToMap.beschlussvorschlag?.map(
          (beschlussgrund) => ({
            text: beschlussgrund.text,
          })
        ),
        // @ts-expect-error source has possible undefined values
        wahlvorschlaege: modelToMap.wahlvorschlaege.map((wahlvorschlag) => ({
          wahlvorschlagID: wahlvorschlag.wahlvorschlagID,
          selected: wahlvorschlag.selected,
          kandidaten: wahlvorschlag.kandidaten?.map((kandidat) => ({
            id: {
              kandidatID: kandidat.kandidatId,
              nennungsNummer: 0,
            },
            discarded: kandidat.isDiscarded,
            votesByVoter: kandidat.votesByVoter,
            votesByWahlvorschlag: kandidat.votesByWahlvorschlag,
          })),
        })),
      };

      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnUndefinedWahlvorschlaege_when_modelWahlvorschlaegeIsEmpty", () => {
      const modelWithEmptyWahlvorschlaege = prepareStimmzettel()
        .wahlvorschlaege([])
        .build();

      const result = toDTO(modelWithEmptyWahlvorschlaege);

      expect(result.wahlvorschlaege).toBeUndefined();
    });

    it("should_returnUndefinedBeschlussvorschlag_when_modelBeschlussvorschlagIsEmpty", () => {
      const modelWithoutBeschlussvorschlag = prepareStimmzettel()
        .beschlussvorschlag([])
        .build();

      const result = toDTO(modelWithoutBeschlussvorschlag);

      expect(result.beschlussvorschlag).toBeUndefined();
    });

    it("should_returnUndefinedBeschlussfassung_when_modelBeschlussfassungIsNull", () => {
      const modelWithoutBeschlussfassung = prepareStimmzettel()
        .beschlussfassung(null)
        .build();

      const result = toDTO(modelWithoutBeschlussfassung);

      expect(result.beschlussfassung).toBeUndefined();
    });

    it("should_handleEmptyKandidatenArray_when_modelKandidatenIsEmpty", () => {
      const modelWithEmptyKandidaten = prepareStimmzettel()
        .wahlvorschlaege([
          prepareStimmzettelWahlvorschlag().kandidaten([]).build(),
        ])
        .build();

      const result = toDTO(modelWithEmptyKandidaten);

      expect(result.wahlvorschlaege?.[0].kandidaten).toStrictEqual([]);
    });

    it("should_mapSingleKandidatCorrectly_when_oneKandidatIsGiven", () => {
      const singleKandidat = createStimmzettelKandidat();
      const modelWithSingleKandidat = prepareStimmzettel()
        .wahlvorschlaege([
          prepareStimmzettelWahlvorschlag()
            .kandidaten([singleKandidat])
            .build(),
        ])
        .build();

      const result = toDTO(modelWithSingleKandidat);

      expect(result.wahlvorschlaege?.[0].kandidaten).toHaveLength(1);
      expect(result.wahlvorschlaege?.[0].kandidaten?.[0]).toStrictEqual({
        id: {
          kandidatID: singleKandidat.kandidatId,
          nennungsNummer: 0,
        },
        discarded: singleKandidat.isDiscarded,
        votesByVoter: singleKandidat.votesByVoter,
        votesByWahlvorschlag: singleKandidat.votesByWahlvorschlag,
      });
    });

    it("should_mapNullVoteFieldsToUndefined_when_kandidatVotesAreNull", () => {
      const kandidatWithoutVotes: Kandidat = prepareStimmzettelKandidat()
        .votesByVoter(null)
        .invalidVotes(null)
        .votesByWahlvorschlag(null)
        .build();
      const modelWithKandidatWithoutVotes = prepareStimmzettel()
        .wahlvorschlaege([
          prepareStimmzettelWahlvorschlag()
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
});
