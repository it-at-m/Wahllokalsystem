import type { UserDTO } from "@/api/wls-clients/generated-auth-api";
import type { User } from "@/types/User.ts";
import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";

export function useUserMapper() {
  function validateDtoAndMapToModel(userDto: UserDTO): User {
    const invalidProps: string[] = [];

    _assertIsNotBlank(userDto.username, "username");
    _assertIsNotUndefinedOrNull(userDto.userEnabled, "userEnabled");
    _assertIsNotBlank(userDto.wahltagID, "teamID");
    _assertIsNotBlank(userDto.teamID, "wahltagID");
    _assertIsNotBlank(userDto.wahltag, "wahltag");
    _assertIsNotBlank(userDto.wahlbezirkID, "wahlbezirkID");
    _assertIsNotBlank(userDto.wahlbezirkNummer, "wahlbezirkNummer");
    _assertIsNotBlank(userDto.wahlbezirksArt, "wahlbezirksArt");
    _assertIsNotBlank(userDto.pin, "pin");
    _assertIsNotEmpty(userDto.authorities, "authorities");
    _assertIsNotBlank(userDto.wbid_wahlnummer, "wbid_wahlnummer");

    if (invalidProps.length > 0) {
      throw new Error(
        `Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: ${invalidProps.join(", ")}`
      );
    }

    return {
      username: userDto.username,
      email: userDto.email,
      teamName: userDto.teamID,
      userEnabled: userDto.userEnabled,
      wahltagID: userDto.wahltagID,
      wahltag: userDto.wahltag,
      wahlbezirkID: userDto.wahlbezirkID,
      wahlbezirkNummer: userDto.wahlbezirkNummer,
      wahlbezirksArt: userDto.wahlbezirksArt,
      pin: userDto.pin,
      authorities: userDto.authorities,
      wahlMetaData: _parseWbIdWahlnummer(userDto.wbid_wahlnummer),
    };

    function _assertIsNotBlank(
      value: string | undefined | null,
      propName: string
    ): asserts value is string {
      if (value === undefined || value == null || value.trim() === "") {
        invalidProps.push(propName);
      }
    }

    function _assertIsNotEmpty(
      value: string[] | null | undefined,
      propName: string
    ): asserts value is string[] {
      if (value === undefined || value === null || value.length === 0) {
        invalidProps.push(propName);
      }
    }

    function _assertIsNotUndefinedOrNull(
      value: boolean | undefined | null,
      propName: string
    ): asserts value is boolean {
      if (value === undefined || value == null) {
        invalidProps.push(propName);
      }
    }
  }

  function _parseWbIdWahlnummer(value: string): WahlMetaData[] {
    const parsedObject = JSON.parse(value);
    if (!_isWbidWahlnummer(parsedObject)) {
      throw new Error();
    }

    return parsedObject.wbid_wahlnummer;
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  function _isWbidWahlnummer(value: any): value is {
    wbid_wahlnummer: WahlMetaData[];
  } {
    return (
      value.wbid_wahlnummer !== undefined &&
      Array.isArray(value.wbid_wahlnummer) &&
      _isArrayOfWahlMetaData(value.wbid_wahlnummer)
    );
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  function _isArrayOfWahlMetaData(values: any[]): values is WahlMetaData[] {
    return values.every(
      (value) =>
        value.wahlbezirkID !== undefined &&
        value.wahlnummer !== undefined &&
        value.wahlID !== undefined
    );
  }

  return { validateDtoAndMapToModel };
}
