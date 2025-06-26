import type { UserDTO } from "@/api/wls-clients/generated-auth-api";

export function useUserValidator() {
  function validUserDtoOrThrow(userDto: UserDTO) {
    const requiredFields: (keyof UserDTO)[] = [
      "username",
      "userEnabled",
      "wahltagID",
      "wahltag",
      "wahlbezirkID",
      "wahlbezirkNummer",
      "wahlbezirksArt",
      "pin",
      "authorities",
      "wbid_wahlnummer",
    ];

    const undefinedOrEmptyFields = requiredFields.filter((key) => {
      const value = userDto[key as keyof typeof userDto];
      return (
        value === undefined ||
        value === null ||
        _isEmptyString(value) ||
        _isEmptySet(value)
      );
    });

    if (undefinedOrEmptyFields.length > 0) {
      throw new Error(
        `Laden des Users fehlgeschlagen. Folgende Pflichtfelder sind nicht befüllt: ${undefinedOrEmptyFields.join(", ")}`
      );
    } else {
      return userDto;
    }
  }

  function _isEmptyString(value: string | boolean | Set<string>) {
    return typeof value === "string" && value.trim() === "";
  }

  function _isEmptySet(value: string | boolean | Set<string>) {
    return value instanceof Set && value.size === 0;
  }

  return { validUserDtoOrThrow };
}
