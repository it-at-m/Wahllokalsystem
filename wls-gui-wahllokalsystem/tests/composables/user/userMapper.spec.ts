import type { UserDTO } from "@/api/wls-clients/generated-auth-api";

import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useUserMapper } from "@/composables/user/userMapper.ts";

describe("userMapper.ts", () => {
  const { toModel } = useUserMapper();
  const { prepareUser, prepareUserDTO, mapUserDtoToUser } =
    useUserTestDataFactory();

  describe("toModel", () => {
    it("should_returnModel_when_givenDto", () => {
      const dto: UserDTO = prepareUserDTO().build();
      const expectedUser = mapUserDtoToUser(dto);

      const result = toModel(dto);

      expect(result).toEqual(expectedUser);
    });
  });
});
