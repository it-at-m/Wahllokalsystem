import type { RoleMapping } from "@/types/user/RoleMapping.ts";

import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useRolesMapper } from "@/composables/user/rolesMapper.ts";

const { createRoleMappingDTO } = useUserTestDataFactory();

describe("rolesMapper.ts", () => {
  let unitUnderTest: ReturnType<typeof useRolesMapper>;

  beforeEach(() => {
    unitUnderTest = useRolesMapper();
  });

  describe("toModel", () => {
    it("should_returnModel_when_dtoIsGiven", () => {
      const dtoToMap = createRoleMappingDTO();

      const result = unitUnderTest.toModel(dtoToMap);

      const expectedResult: RoleMapping = {
        schriftfuehrung: dtoToMap.schriftfuehrung,
        erfassungsteam: dtoToMap.erfassungsteam,
        admin: dtoToMap.admin,
      };
      expect(result).toEqual(expectedResult);
    });
  });
});
