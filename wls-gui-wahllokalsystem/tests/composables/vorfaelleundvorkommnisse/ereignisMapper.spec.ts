import type {
  EreignisDTO,
  EreignisseWriteDTO,
  WahlbezirkEreignisseDTO,
} from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse";

import { useVorfaelleundvorkommnisseTestDataFactory } from "@tests/utils/vorfaelleundvorkommnisse/VorfaelleundvorkommnisseTestDataFactory";
import { describe, expect, it } from "vitest";

import { useEreignisMapper } from "@/composables/vorfaelleundvorkommnisse/ereignisMapper.ts";

const {
  prepareWahlbezirkEreignisseDTO,
  prepareEreignisDTO,
  prepareEreignis,
  prepareWahlbezirkEreignisse,
} = useVorfaelleundvorkommnisseTestDataFactory();

describe("ereignisMapper.ts", () => {
  const { toModel, toDto } = useEreignisMapper();

  describe("toModel", () => {
    it("should_returnWahlbezirkEreignisse_when_dtoIsGiven", () => {
      const dtoToMap = getWahlbezirkEreignisseDTO(
        getEreignisseDTO("2025-04-28T08:15:00", "2025-04-28T11:40:00")
      );

      const result = toModel(dtoToMap);

      const expectedResult = getWahlbezirkEreignisse(getEreignisse());
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnWahlbezirkEreignisseWithEmptyArray_when_dtoHasUndefinedEreigniseintraege", () => {
      const dtoWithUndefinedEreigniseintraege =
        getWahlbezirkEreignisseDTO(undefined);

      const result = toModel(dtoWithUndefinedEreigniseintraege);

      const expectedResult = getWahlbezirkEreignisse([]);
      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("toDto", () => {
    it("should_returnEreignisseWriteDto_when_modelIsGiven", () => {
      const modelToMap = getWahlbezirkEreignisse(getEreignisse());

      const result = toDto(modelToMap);

      const expectedResult: EreignisseWriteDTO = {
        ereigniseintraege: getEreignisseDTO(
          "2025-04-28T08:15:00.000",
          "2025-04-28T11:40:00.000"
        ),
        keineVorfaelle: false,
        keineVorkommnisse: false,
      };
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnEreignisseWriteDto_when_modelIsGivenWithTrueValues", () => {
      const modelToMap = getWahlbezirkEreignisse(getEreignisse());
      modelToMap.keineVorfaelle = true;
      modelToMap.keineVorkommnisse = true;

      const result = toDto(modelToMap);

      const expectedResult: EreignisseWriteDTO = {
        ereigniseintraege: getEreignisseDTO(
          "2025-04-28T08:15:00.000",
          "2025-04-28T11:40:00.000"
        ),
        keineVorfaelle: true,
        keineVorkommnisse: true,
      };
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnDtoWithoutEreignisse_when_modelIsGivenWithoutBeschreibungen", () => {
      const modelToMap = getWahlbezirkEreignisse([
        prepareEreignis()
          .beschreibung(undefined)
          .uhrzeit(new Date("2025-04-28T08:15:00"))
          .ereignisart("VORFALL")
          .build(),
        prepareEreignis()
          .beschreibung(undefined)
          .uhrzeit(new Date("2025-04-28T11:40:00"))
          .ereignisart("VORKOMMNIS")
          .build(),
      ]);

      const result = toDto(modelToMap);

      const expectedResult: EreignisseWriteDTO = {
        ereigniseintraege: [],
        keineVorfaelle: false,
        keineVorkommnisse: false,
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });

  it("should_haveEqualUhrzeitString_when_readingDTOAndProducingDTOWithCreatedModel", () => {
    const uhrzeit = "2025-08-13T16:01:23.123";
    const dtoToMap = prepareWahlbezirkEreignisseDTO()
      .ereigniseintraege([prepareEreignisDTO().uhrzeit(uhrzeit).build()])
      .build();

    const model = toModel(dtoToMap);
    const dto = toDto(model);

    // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
    expect(dto.ereigniseintraege![0]?.uhrzeit).toStrictEqual(uhrzeit);
  });
});

function getWahlbezirkEreignisseDTO(
  ereigniseintraege: EreignisDTO[] | undefined
): WahlbezirkEreignisseDTO {
  return prepareWahlbezirkEreignisseDTO()
    .wahlbezirkID("1234")
    .keineVorkommnisse(false)
    .keineVorfaelle(false)
    .ereigniseintraege(ereigniseintraege)
    .build();
}

function getEreignisseDTO(uhrzeit1: string, uhrzeit2: string): EreignisDTO[] {
  return [
    prepareEreignisDTO()
      .beschreibung("Ereignis 1")
      .uhrzeit(uhrzeit1)
      .ereignisart("VORFALL")
      .build(),
    prepareEreignisDTO()
      .beschreibung("Ereignis 2")
      .uhrzeit(uhrzeit2)
      .ereignisart("VORKOMMNIS")
      .build(),
  ];
}

function getWahlbezirkEreignisse(
  ereigniseintraege: Ereignis[]
): WahlbezirkEreignisse {
  return prepareWahlbezirkEreignisse()
    .wahlbezirkID("1234")
    .keineVorkommnisse(false)
    .keineVorfaelle(false)
    .ereigniseintraege(ereigniseintraege)
    .build();
}

function getEreignisse(): Ereignis[] {
  return [
    prepareEreignis()
      .beschreibung("Ereignis 1")
      .uhrzeit(new Date("2025-04-28T08:15:00"))
      .ereignisart("VORFALL")
      .build(),
    prepareEreignis()
      .beschreibung("Ereignis 2")
      .uhrzeit(new Date("2025-04-28T11:40:00"))
      .ereignisart("VORKOMMNIS")
      .build(),
  ];
}
