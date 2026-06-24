import type {
  KonfigurationDTO,
  KonfigurationSetDTO,
} from "@/api/wls-clients/generated-infomanagement-api";
import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";

export function useKonfigurationMapper() {
  function mapKonfigurationDtoToConfigParameter(
    dto: KonfigurationDTO
  ): InfomanagementConfigParameter {
    return {
      name: dto.schluessel,
      beschreibung: dto.beschreibung,
      wert: dto.wert,
      defaultValue: dto.standardwert,
    };
  }

  function mapKonfigurationDtosToConfigParameters(
    dtos: KonfigurationDTO[]
  ): InfomanagementConfigParameter[] {
    return dtos.map(mapKonfigurationDtoToConfigParameter);
  }

  function mapConfigParameterToKonfigurationSetDto(
    configParameter: InfomanagementConfigParameter
  ): KonfigurationSetDTO {
    return {
      wert: configParameter.wert,
      beschreibung: configParameter.beschreibung,
      standardwert: configParameter.defaultValue,
    };
  }

  return {
    mapKonfigurationDtoToConfigParameter,
    mapKonfigurationDtosToConfigParameters,
    mapConfigParameterToKonfigurationSetDto,
  };
}
