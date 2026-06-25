import { useKonfigurationTestDataFactory } from "@tests/types/config/KonfigurationTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useKonfigurationMapper } from "@/composables/konfiguration/konfigurationMapper.ts";

const {
  mapKonfigurationDtoToConfigParameter,
  mapKonfigurationDtosToConfigParameters,
  mapConfigParameterToKonfigurationSetDto,
} = useKonfigurationMapper();

const { prepareKonfigurationDto, prepareConfigParameter } =
  useKonfigurationTestDataFactory();

describe("konfigurationMapper.ts", () => {
  describe("mapKonfigurationDtoToConfigParameter", () => {
    it("should_mapAllFields_when_called", () => {
      const dto = prepareKonfigurationDto()
        .schluessel("WILLKOMMENSTEXT")
        .wert("aWert")
        .beschreibung("eineBeschreibung")
        .standardwert("einStandardwert")
        .build();

      const result = mapKonfigurationDtoToConfigParameter(dto);

      expect(result).toStrictEqual({
        name: "WILLKOMMENSTEXT",
        wert: "aWert",
        beschreibung: "eineBeschreibung",
        defaultValue: "einStandardwert",
      });
    });
  });

  describe("mapKonfigurationDtosToConfigParameters", () => {
    it("should_mapEveryDto_when_called", () => {
      const dtos = [
        prepareKonfigurationDto().build(),
        prepareKonfigurationDto().build(),
      ];

      const result = mapKonfigurationDtosToConfigParameters(dtos);

      expect(result).toStrictEqual(
        dtos.map((dto) => mapKonfigurationDtoToConfigParameter(dto))
      );
      expect(result).toHaveLength(2);
    });
  });

  describe("mapConfigParameterToKonfigurationSetDto", () => {
    it("should_mapValueDescriptionAndDefaultValue_when_called", () => {
      const configParameter = prepareConfigParameter()
        .name("WILLKOMMENSTEXT")
        .wert("aWert")
        .beschreibung("eineBeschreibung")
        .defaultValue("einStandardwert")
        .build();

      const result = mapConfigParameterToKonfigurationSetDto(configParameter);

      expect(result).toStrictEqual({
        wert: "aWert",
        beschreibung: "eineBeschreibung",
        standardwert: "einStandardwert",
      });
    });
  });
});
