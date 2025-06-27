import type { UserDTO } from "@/api/wls-clients/generated-auth-api";
import type { User } from "@/types/User.ts";
import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";

import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useUserMapper() {
  function validateDtoAndMapToModel(userDto: UserDTO): User {
    const undefinedOrEmptyFields = [
      ...(userDto.username == null || !_isValidString(userDto.username)
        ? ["username"]
        : []),
      ...(userDto.userEnabled == null ? ["userEnabled"] : []),
      ...(userDto.wahltagID == null || !_isValidString(userDto.wahltagID)
        ? ["wahltagID"]
        : []),
      ...(userDto.wahltag == null || !_isValidString(userDto.wahltag)
        ? ["wahltag"]
        : []),
      ...(userDto.wahlbezirkID == null || !_isValidString(userDto.wahlbezirkID)
        ? ["wahlbezirkID"]
        : []),
      ...(userDto.wahlbezirkNummer == null ||
      !_isValidString(userDto.wahlbezirkNummer)
        ? ["wahlbezirkNummer"]
        : []),
      ...(userDto.wahlbezirksArt == null ||
      !(userDto.wahlbezirksArt in WahlbezirksArtEnum)
        ? ["wahlbezirksArt"]
        : []),
      ...(userDto.pin == null || !_isValidString(userDto.pin) ? ["pin"] : []),
      ...(userDto.authorities == null || !_isValidSet(userDto.authorities)
        ? ["authorities"]
        : []),
      ...(userDto.wbid_wahlnummer == null ||
      !_isValidString(userDto.wbid_wahlnummer)
        ? ["wbid_wahlnummer"]
        : []),
    ];

    if (undefinedOrEmptyFields.length > 0) {
      throw new Error(
        `Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: ${undefinedOrEmptyFields.join(", ")}`
      );
    } else {
      return {
        username: userDto.username,
        email: userDto.email,
        userEnabled: userDto.userEnabled,
        wahltagID: userDto.wahltagID!,
        wahltag: userDto.wahltag!,
        wahlbezirkID: userDto.wahlbezirkID!,
        wahlbezirkNummer: userDto.wahlbezirkNummer!,
        wahlbezirksArt: userDto.wahlbezirksArt!!,
        pin: userDto.pin!,
        authorities: userDto.authorities,
        wahlMetaData: _parseWbIdWahlnummer(userDto.wbid_wahlnummer!),
      } as User;
    }
  }

  function _isValidString(value: string): boolean {
    return value.trim() !== "";
  }

  function _isValidSet(value: Set<string>): boolean {
    return value.size !== 0;
  }

  function _parseWbIdWahlnummer(value: string): WahlMetaData[] {
    try {
      const parsed_wbid_wahlnummer = value ? JSON.parse(value) : undefined;
      return parsed_wbid_wahlnummer?.wbid_wahlnummer
        ? parsed_wbid_wahlnummer.wbid_wahlnummer
        : undefined;
    } catch {
      console.debug("failed to parse JSON wbid_wahlnummer");
      return [];
    }
  }

  return { validateDtoAndMapToModel };
}
