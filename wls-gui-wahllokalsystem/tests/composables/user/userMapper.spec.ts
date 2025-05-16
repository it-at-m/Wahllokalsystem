import type { UserDTO } from "@/api/wls-clients/generated-auth-api";
import type { User } from "@/types/User.ts";

import { useUserTestDataFactory } from "@tests/utils/common/UserTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useUserMapper } from "@/composables/user/userMapper.ts";

describe("userMapper.ts", () => {
  const { toModel } = useUserMapper();
  const {
    createUserDtoWithRandomValues,
    mapDtoWbIdWahlnummerToModelWahlMetaData,
    createUserWithDefaultValues,
  } = useUserTestDataFactory();

  describe("toModel", () => {
    it("should_returnModel_when_givenDto", () => {
      const dto: UserDTO = createUserDtoWithRandomValues();
      const expectedUser: User = {
        username: dto.username,
        email: dto.email,
        userEnabled: dto.userEnabled,
        wahltagID: dto.wahltagID,
        wahltag: dto.wahltag,
        wahlbezirkID: dto.wahlbezirkID,
        wahlbezirkNummer: dto.wahlbezirkNummer,
        wahlbezirksArt: dto.wahlbezirksArt,
        pin: dto.pin,
        authorities: dto.authorities,
        wahlMetaData: mapDtoWbIdWahlnummerToModelWahlMetaData(
          dto.wbid_wahlnummer
        ),
      };

      const result = toModel(dto);

      expect(result).toEqual(expectedUser);
    });

    it("should_returnUserWithDefaultValues_when_valuesInDtoAreNull", () => {
      const dto = {
        username: null,
        email: null,
        userEnabled: null,
        wahltagID: null,
        wahltag: null,
        wahlbezirkID: null,
        wahlbezirkNummer: null,
        wahlbezirksArt: null,
        pin: null,
        authorities: null,
        wbid_wahlnummer: null,
      };

      const expectedUser = createUserWithDefaultValues();
      const result = toModel(dto as unknown as UserDTO);

      expect(result).toEqual(expectedUser);
    });
  });
});
