import type { StimmzettelOfTeamDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Kandidat } from "@/types/dse/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";

import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useStimmzettelMapper } from "@/composables/dse/stimmzettelMapper.ts";

const {
  createStimmzettelOfTeamDTO,
  prepareStimmzettelOfTeamDTO,
  createStimmzettelKandidatDTO,
  prepareStimmzettelKandidat,
  createStimmzettel,
  prepareStimmzettel,
  createStimmzettelKandidat,
  prepareStimmzettelKandidatDTO,
} = useStimmzettelTestDataFactory();

describe("stimmzettelMapper.ts", () => {
  const { toModel, toDTO } = useStimmzettelMapper();

  describe("toModel", () => {
    it("should_mapAllFields_when_dtoIsGiven", () => {
      const dtoToMap = createStimmzettelOfTeamDTO();

      const result: Stimmzettel = toModel(dtoToMap);

      expect(result.stimmzettelkennung).toBe(dtoToMap.stimmzettelkennung);
      expect(result.selectedWahlvorschlaegeOrdnungszahlen).toStrictEqual(
        dtoToMap.selectedWahlvorschlaegeOrdnungszahlen
      );

      expect(result.kandidaten.length).toBe(dtoToMap.kandidaten?.length ?? 0);

      result.kandidaten.forEach((mappedKandidat: Kandidat, index: number) => {
        // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
        const dtoKandidat = dtoToMap.kandidaten![index];
        const expectedKandidat = prepareStimmzettelKandidat()
          .kandidatId(dtoKandidat.kandidatId)
          .votesByVoter(dtoKandidat.votesByVoter)
          .isDiscarded(dtoKandidat.isDiscarded)
          .build();
        expect(mappedKandidat).toStrictEqual(expectedKandidat);
      });
    });

    it("should_returnEmptyKandidaten_when_dtoKandidatenIsUndefined", () => {
      const dtoWithoutKandidaten = prepareStimmzettelOfTeamDTO()
        .kandidaten(undefined)
        .build();

      const result = toModel(dtoWithoutKandidaten);

      expect(result.kandidaten).toStrictEqual([]);
    });

    it("should_returnEmptySelectedWahlvorschlaegeOrdnungszahlen_when_dtoFieldIsUndefined", () => {
      const dtoWithoutSelection = prepareStimmzettelOfTeamDTO()
        .selectedWahlvorschlaegeOrdnungszahlen(undefined)
        .build();

      const result = toModel(dtoWithoutSelection);

      expect(result.selectedWahlvorschlaegeOrdnungszahlen).toStrictEqual([]);
    });

    it("should_handleEmptyKandidatenArray_when_dtoKandidatenIsEmpty", () => {
      const dtoWithEmptyKandidaten = prepareStimmzettelOfTeamDTO()
        .kandidaten([])
        .build();

      const result = toModel(dtoWithEmptyKandidaten);

      expect(result.kandidaten).toStrictEqual([]);
    });

    it("should_handleEmptySelectedWahlvorschlaegeOrdnungszahlen_when_dtoArrayIsEmpty", () => {
      const dtoWithEmptySelection = prepareStimmzettelOfTeamDTO()
        .selectedWahlvorschlaegeOrdnungszahlen([])
        .build();

      const result = toModel(dtoWithEmptySelection);

      expect(result.selectedWahlvorschlaegeOrdnungszahlen).toStrictEqual([]);
    });

    it("should_returnEmptyArrays_when_kandidatenAndSelectionAreUndefined", () => {
      const dtoWithUndefinedArrays = prepareStimmzettelOfTeamDTO()
        .kandidaten(undefined)
        .selectedWahlvorschlaegeOrdnungszahlen(undefined)
        .build();

      const result = toModel(dtoWithUndefinedArrays);

      expect(result.kandidaten).toStrictEqual([]);
      expect(result.selectedWahlvorschlaegeOrdnungszahlen).toStrictEqual([]);
    });

    it("should_mapSingleKandidatCorrectly_when_oneKandidatIsGiven", () => {
      const singleKandidat = createStimmzettelKandidatDTO();
      const dtoWithSingleKandidat = prepareStimmzettelOfTeamDTO()
        .kandidaten([singleKandidat])
        .build();

      const result = toModel(dtoWithSingleKandidat);

      expect(result.kandidaten.length).toBe(1);

      const expectedKandidat = prepareStimmzettelKandidat()
        .kandidatId(singleKandidat.kandidatId)
        .isDiscarded(singleKandidat.isDiscarded)
        .votesByVoter(singleKandidat.votesByVoter)
        .build();
      const mapped = result.kandidaten[0];
      expect(mapped).toStrictEqual(expectedKandidat);
    });
  });

  describe("toDTO", () => {
    it("should_mapAllFields_when_modelIsGiven", () => {
      const modelToMap = createStimmzettel();

      const result: StimmzettelOfTeamDTO = toDTO(modelToMap);

      expect(result.stimmzettelkennung).toBe(modelToMap.stimmzettelkennung);
      expect(result.selectedWahlvorschlaegeOrdnungszahlen).toStrictEqual(
        modelToMap.selectedWahlvorschlaegeOrdnungszahlen
      );

      expect(result.kandidaten?.length).toBe(
        modelToMap.kandidaten?.length ?? 0
      );

      result.kandidaten?.forEach((mappedKandidat: Kandidat, index: number) => {
        // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
        const modelKandidat = modelToMap.kandidaten![index];
        const expectedKandidat = prepareStimmzettelKandidatDTO()
          .kandidatId(modelKandidat.kandidatId)
          .votesByVoter(modelKandidat.votesByVoter)
          .isDiscarded(modelKandidat.isDiscarded)
          .build();
        expect(mappedKandidat).toStrictEqual(expectedKandidat);
      });
    });

    it("should_handleEmptyKandidatenArray_when_modelKandidatenIsEmpty", () => {
      const modelWithEmptyKandidaten = prepareStimmzettel()
        .kandidaten([])
        .build();

      const result = toDTO(modelWithEmptyKandidaten);

      expect(result.kandidaten).toStrictEqual([]);
    });

    it("should_handleEmptySelectedWahlvorschlaegeOrdnungszahlen_when_modelArrayIsEmpty", () => {
      const modelWithEmptySelection = prepareStimmzettel()
        .selectedWahlvorschlaegeOrdnungszahlen([])
        .build();

      const result = toDTO(modelWithEmptySelection);

      expect(result.selectedWahlvorschlaegeOrdnungszahlen).toStrictEqual([]);
    });

    it("should_mapSingleKandidatCorrectly_when_oneKandidatIsGiven", () => {
      const singleKandidat = createStimmzettelKandidat();
      const modelWithSingleKandidat = prepareStimmzettel()
        .kandidaten([singleKandidat])
        .build();

      const result = toDTO(modelWithSingleKandidat);

      expect(result.kandidaten?.length).toBe(1);

      const expectedKandidat = prepareStimmzettelKandidat()
        .kandidatId(singleKandidat.kandidatId)
        .isDiscarded(singleKandidat.isDiscarded)
        .votesByVoter(singleKandidat.votesByVoter)
        .build();
      // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
      const mapped = result.kandidaten![0];
      expect(mapped).toStrictEqual(expectedKandidat);
    });
  });
});
