import type {
  RoleMappingsDTO,
  UserDTO,
} from "@/api/wls-clients/generated-auth-api";
import type { User } from "@/types/User.ts";
import type { RoleMapping } from "@/types/user/RoleMapping.ts";
import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const {
  generateRandomString,
  generateRandomDateTimeAsString,
  generateRandomBoolean,
} = useCommonTestDataFactory();

export function useUserTestDataFactory() {
  function createRoleMapping(): RoleMapping {
    return {
      erfassungsteam: generateRandomString(10),
      schriftfuehrung: generateRandomString(10),
    };
  }

  function createRoleMappingDTO(): RoleMappingsDTO {
    return {
      admin: generateRandomString(10),
      erfassungsteam: generateRandomString(10),
      schriftfuehrung: generateRandomString(10),
    };
  }

  function prepareRoleMapping(): Builder<RoleMapping> {
    return proxyBuilder<RoleMapping>(createRoleMapping());
  }

  function prepareUser(): Builder<User> {
    return proxyBuilder<User>(_createUserWithDefaultValues());
  }

  function prepareUserDTO(): Builder<UserDTO> {
    return proxyBuilder<UserDTO>(_createUserDtoWithRandomValues());
  }

  function mapValidUserDtoToUser(userDto: UserDTO): User {
    return {
      username: userDto.username,
      email: userDto.email,
      userEnabled: userDto.userEnabled,
      /* eslint-disable @typescript-eslint/no-non-null-assertion -- Temporarily disabling because only valid dto is given as input */
      teamName: userDto.teamID!,
      wahltagID: userDto.wahltagID!,
      wahltag: userDto.wahltag!,
      wahlbezirkID: userDto.wahlbezirkID!,
      wahlbezirkNummer: userDto.wahlbezirkNummer!,
      wahlbezirksArt: userDto.wahlbezirksArt!,
      pin: userDto.pin!,
      /* eslint-enable @typescript-eslint/no-non-null-assertion */
      authorities: userDto.authorities,
      wahlMetaData:
        _mapDtoWbIdWahlnummerToModelWahlMetaData(userDto.wbid_wahlnummer) || [],
    };
  }

  function _createUserWithDefaultValues(): User {
    return {
      username: "",
      email: "",
      userEnabled: false,
      teamName: "",
      wahltagID: "",
      wahltag: "",
      wahlbezirkID: "",
      wahlbezirkNummer: "",
      wahlbezirksArt: WahlbezirksArtEnum.UWB,
      pin: "",
      authorities: ["authority"],
      wahlMetaData: [{ wahlbezirkID: "", wahlID: "", wahlnummer: "" }],
    };
  }

  function _createUserDtoWithRandomValues(): UserDTO {
    return {
      username: generateRandomString(10),
      email: generateRandomString(10),
      userEnabled: generateRandomBoolean(),
      teamID: generateRandomString(10),
      wahltagID: generateRandomString(10),
      wahltag: generateRandomDateTimeAsString(),
      wahlbezirkID: generateRandomString(10),
      wahlbezirkNummer: generateRandomString(10),
      wahlbezirksArt: WahlbezirksArtEnum.BWB,
      pin: generateRandomString(10),
      authorities: ["authority"],
      wbid_wahlnummer: `{"wbid_wahlnummer":[{"wahlbezirkID":"${generateRandomString(10)}","wahlnummer":"${generateRandomString(1)}","wahlID":"${generateRandomString(10)}"}]}`,
    };
  }

  function _mapDtoWbIdWahlnummerToModelWahlMetaData(
    wbid_wahlnummer: string | undefined
  ): WahlMetaData[] | undefined {
    return wbid_wahlnummer
      ? JSON.parse(wbid_wahlnummer).wbid_wahlnummer
      : undefined;
  }

  return {
    createRoleMapping,
    createRoleMappingDTO,
    mapValidUserDtoToUser,
    prepareRoleMapping,
    prepareUser,
    prepareUserDTO,
  };
}
