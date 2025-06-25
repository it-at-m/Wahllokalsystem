import type { UserDTO } from "@/api/wls-clients/generated-auth-api";

export function useUserValidator() {
  function validUserDtoOrThrow(userDto: UserDTO) {
    const requiredFields = [
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
        (typeof value === "string" && value.trim() === "") || // check for empty or blank strings
        (value instanceof Set && value.size === 0) // check for empty authorities set
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

  return { validUserDtoOrThrow };
}
