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
      const dtoToMap = getWahlbezirkEreignisseDTO();

      const result = toModel(dtoToMap);

      const expectedResult = getWahlbezirkEreignisse();
      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("toDto", () => {
    it("should_returnEreignisseWriteDto_when_modelIsGiven", () => {
      const modelToMap = getWahlbezirkEreignisse();

      const result = toDto(modelToMap);

      const expectedResult: EreignisseWriteDTO = {
        ereigniseintraege: getEreignisseDTO(
          "2025-04-28T08:15:00.000Z",
          "2025-04-28T11:40:00.000Z"
        ),
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });
});

function getWahlbezirkEreignisseDTO(): WahlbezirkEreignisseDTO {
  return prepareWahlbezirkEreignisseDTO()
    .wahlbezirkID("1234")
    .keineVorkommnisse(false)
    .keineVorfaelle(false)
    .ereigniseintraege(
      getEreignisseDTO("2025-04-28T08:15:00", "2025-04-28T11:40:00")
    )
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

function getWahlbezirkEreignisse(): WahlbezirkEreignisse {
  return prepareWahlbezirkEreignisse()
    .wahlbezirkID("1234")
    .keineVorkommnisse(false)
    .keineVorfaelle(false)
    .ereigniseintraege(getEreignisse())
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
