import type { RoleMappingsDTO } from "@/api/wls-clients/generated-auth-api";
import type { RoleMapping } from "@/types/user/RoleMapping.ts";

export function useRolesMapper() {
  function toModel(dto: RoleMappingsDTO): RoleMapping {
    return {
      erfassungsteam: dto.erfassungsteam,
      schriftfuehrung: dto.schriftfuehrung,
    };
  }

  return {
    toModel,
  };
}
