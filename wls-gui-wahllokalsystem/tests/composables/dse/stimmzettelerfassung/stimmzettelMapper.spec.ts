/* eslint-disable  @typescript-eslint/no-non-null-assertion */
/* when we access a null field the test will fail */
import type {
  KandidatDTO,
  StimmzettelOfTeamDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Kandidat } from "@/types/dse/stimmzettelerfassung/persistedStimmzettel/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/persistedStimmzettel/Stimmzettel.ts";

import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useStimmzettelMapper } from "@/composables/dse/stimmzettelerfassung/stimmzettelMapper.ts";

const {
  createStimmzettelOfTeamDTO,
  prepareStimmzettelOfTeamDTO,
  createStimmzettelKandidatDTO,
  createPersistedStimmzettel,
  preparePersistedStimmzettel,
  createPersistedStimmzettelKandidat,
  preparePersistedStimmzettelBeschlussfassung,
  prepareStimmzettelBeschlussfassungDTO,
  preparePersistedStimmzettelBeschlussgrund,
  prepareStimmzettelBeschlussgrundDTO,
  preparePersistedStimmzettelKandidat,
  prepareStimmzettelKandidatDTO,
  prepareStimmzettelKandidatIdDTO,
  preparePersistedStimmzettelWahlvorschlag,
  prepareStimmzettelWahlvorschlagDTO,
} = useStimmzettelTestDataFactory();

describe("stimmzettelMapper.ts", () => {
  const { toModel, toDTO } = useStimmzettelMapper();

  describe("toModel", () => {
    it("should_mapAllFields_when_dtoIsGiven", () => {
      const dtoToMap = createStimmzettelOfTeamDTO();

      const result: Stimmzettel = toModel(dtoToMap);

      const expectedResult: Stimmzettel = preparePersistedStimmzettel()
        .stimmzettelkennung(dtoToMap.stimmzettelkennung)
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

      const result = toModel(dtoWithoutWahlvorschlaege);

      expect(result.wahlvorschlaege).toStrictEqual([]);
    });

    it("should_returnEmptyBeschlussvorschlag_when_dtoBeschlussvorschlagIsUndefined", () => {
      const dtoWithoutBeschlussvorschlag = prepareStimmzettelOfTeamDTO()
        .wahlvorstandBeschlussvorschlag(undefined)
        .build();

      const result = toModel(dtoWithoutBeschlussvorschlag);

      expect(result.beschlussvorschlag).toStrictEqual([]);
    });

    it("should_returnEmptyBeschlussvorschlag_when_dtoBeschlussvorschlagIsEmpty", () => {
      const dtoWithoutBeschlussvorschlag = prepareStimmzettelOfTeamDTO()
        .wahlvorstandBeschlussvorschlag([])
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
});
