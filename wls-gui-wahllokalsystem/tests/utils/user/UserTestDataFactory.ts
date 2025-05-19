import type { UserDTO } from "@/api/wls-clients/generated-auth-api";
import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { User } from "@/types/User.ts";

const {
  generateRandomString,
  generateRandomDateTimeAsString,
  generateRandomBoolean,
} = useCommonTestDataFactory();

export function useUserTestDataFactory() {
  function prepareUser(): Builder<User> {
    return proxyBuilder<User>(_createUserWithDefaultValues());
  }

  function prepareUserDTO(): Builder<UserDTO> {
    return proxyBuilder<UserDTO>(_createUserDtoWithRandomValues());
  }

  function mapUserDtoToUser(userDto: UserDTO): User {
    return {
      username: userDto.username,
      email: userDto.email,
      userEnabled: userDto.userEnabled,
      wahltagID: userDto.wahltagID,
      wahltag: userDto.wahltag,
      wahlbezirkID: userDto.wahlbezirkID,
      wahlbezirkNummer: userDto.wahlbezirkNummer,
      wahlbezirksArt: userDto.wahlbezirksArt,
      pin: userDto.pin,
      authorities: userDto.authorities,
      wahlMetaData: _mapDtoWbIdWahlnummerToModelWahlMetaData(
        userDto.wbid_wahlnummer
      ),
    };
  }

  function _createUserWithDefaultValues(): User {
    return new User();
  }

  function _createUserDtoWithRandomValues(): UserDTO {
    return {
      username: generateRandomString(10),
      email: generateRandomString(10),
      userEnabled: generateRandomBoolean(),
      wahltagID: generateRandomString(10),
      wahltag: generateRandomDateTimeAsString(),
      wahlbezirkID: generateRandomString(10),
      wahlbezirkNummer: generateRandomString(10),
      wahlbezirksArt: "BWB",
      pin: generateRandomString(10),
      authorities: new Set<string>(),
      wbid_wahlnummer: `{"wbid_wahlnummer":[{"wahlbezirkID":"${generateRandomString(10)}","wahlnummer":"${generateRandomString(1)}","wahlID":"${generateRandomString(10)}"}]}`,
    };
  }

  function _mapDtoWbIdWahlnummerToModelWahlMetaData(
    wbid_wahlnummer: string
  ): WahlMetaData[] {
    return JSON.parse(wbid_wahlnummer);
  }

  return {
    mapUserDtoToUser,
    prepareUser,
    prepareUserDTO,
  };
}
