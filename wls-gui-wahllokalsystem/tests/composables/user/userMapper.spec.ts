import type { UserDTO } from "@/api/wls-clients/generated-auth-api";

import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useUserMapper } from "@/composables/user/userMapper.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

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

    it.each([
      {
        userDto: {
          username: null,
          email: null,
          userEnabled: null,
          wahltagID: null,
          wahltag: null,
          wahlbezirkID: null,
          wahlbezirkNummer: null,
          wahlbezirksArt: WahlbezirksArtEnum.UWB,
          pin: null,
          authorities: null,
          wbid_wahlnummer: null,
        },
        when: "paramsAreNull",
      },
      {
        userDto: {
          username: undefined,
          email: undefined,
          userEnabled: undefined,
          wahltagID: undefined,
          wahltag: undefined,
          wahlbezirkID: undefined,
          wahlbezirkNummer: undefined,
          wahlbezirksArt: WahlbezirksArtEnum.UWB,
          pin: undefined,
          authorities: undefined,
          wbid_wahlnummer: undefined,
        },
        when: "paramsAreUndefined",
      },
    ])("should_returnUserWithDefaultValues_when_$when", ({ userDto }) => {
      const expectedUser = prepareUser().build();
      const result = toModel(userDto as unknown as UserDTO);

      expect(result).toEqual(expectedUser);
    });
  });
});
