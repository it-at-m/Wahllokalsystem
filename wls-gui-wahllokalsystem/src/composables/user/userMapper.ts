import type { UserDTO } from "@/api/wls-clients/generated-auth-api";
import type { User } from "@/types/User.ts";
import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";

export function useUserMapper() {
  function validateDtoAndMapToModel(userDto: UserDTO): User {
    const errors: Error[] = [];

    _assertIsNotBlank(userDto.username, "username", errors);
    _assertIsNotUndefinedOrNull(userDto.userEnabled, "userEnabled", errors);
    _assertIsNotBlank(userDto.wahltagID, "wahltagID", errors);
    _assertIsNotBlank(userDto.wahltag, "wahltag", errors);
    _assertIsNotBlank(userDto.wahlbezirkID, "wahlbezirkID", errors);
    _assertIsNotBlank(userDto.wahlbezirkNummer, "wahlbezirkNummer", errors);
    _assertIsNotBlank(userDto.wahlbezirksArt, "wahlbezirksArt", errors);
    _assertIsNotBlank(userDto.pin, "pin", errors);
    _assertIsNotEmpty(userDto.authorities, "authorities", errors);
    _assertIsNotBlank(userDto.wbid_wahlnummer, "wbid_wahlnummer", errors);

    if (errors.length > 0) {
      throw new Error(
        `Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: ${errors.join(", ")}`
      );
    }

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
      wahlMetaData: _parseWbIdWahlnummer(userDto.wbid_wahlnummer),
    } as User;
  }

  function _assertIsNotBlank(
    value: string | undefined | null,
    propName: string,
    errors?: Error[]
  ): asserts value is string {
    if (value === undefined || value == null || value.trim() === "") {
      if (errors) {
        errors.push(new Error(`${propName} is blank`));
      } else {
        throw new Error(`${propName} is blank`);
      }
    }
  }

  function _assertIsNotEmpty(
    value: Set<string> | null | undefined,
    propName: string,
    errors?: Error[]
  ): asserts value is Set<string> {
    if (value === undefined || value === null || value.size === 0) {
      if (errors) {
        errors.push(new Error(`${propName} is empty`));
      } else {
        throw new Error(`${propName} is empty`);
      }
    }
  }

  function _assertIsNotUndefinedOrNull(
    value: boolean | undefined | null,
    propName: string,
    errors?: Error[]
  ): asserts value is boolean {
    if (value === undefined || value == null) {
      if (errors) {
        errors.push(new Error(`${propName} is undefined`));
      } else {
        throw new Error(`${propName} is undefined`);
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
