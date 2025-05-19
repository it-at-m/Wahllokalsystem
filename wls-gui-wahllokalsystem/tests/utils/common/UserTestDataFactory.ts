import type { UserDTO } from "@/api/wls-clients/generated-auth-api";
import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { User } from "@/types/User.ts";

const {
  generateRandomString,
  generateRandomDateTimeAsString,
  generateRandomBoolean,
} = useCommonTestDataFactory();

export function useUserTestDataFactory() {
  function createUserWithRandomWahlbezirkID(): User {
    const user = new User();
    user.wahlbezirkID = generateRandomString(10);
    return user;
  }

  function createUserWithUndefinedWahlbezirkID(): User {
    const user = new User();
    user.wahlbezirkID = undefined;
    return user;
  }

  function createUserWithRandomWahltagID(): User {
    const user = new User();
    user.wahltagID = generateRandomString(10);
    return user;
  }

  function createUserWithUndefinedWahltagID(): User {
    const user = new User();
    user.wahltagID = undefined;
    return user;
  }

  function createUserWithUwbWahlbezirksArt(): User {
    const user = new User();
    user.wahlbezirksArt = "UWB";
    return user;
  }

  function createUserWithBwbWahlbezirksArt(): User {
    const user = new User();
    user.wahlbezirksArt = "BWB";
    return user;
  }

  function createUserWithUndefinedWahlbezirksArt(): User {
    const user = new User();
    user.wahlbezirksArt = undefined;
    return user;
  }

  function createUserWithDefaultValues(): User {
    return new User();
  }

  function createUserDtoWithRandomValues(): UserDTO {
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

  function mapDtoWbIdWahlnummerToModelWahlMetaData(
    wbid_wahlnummer: string
  ): WahlMetaData[] {
    return JSON.parse(wbid_wahlnummer);
  }

  return {
    createUserWithRandomWahlbezirkID,
    createUserWithUndefinedWahlbezirkID,
    createUserWithRandomWahltagID,
    createUserWithUndefinedWahltagID,
    createUserWithUwbWahlbezirksArt,
    createUserWithBwbWahlbezirksArt,
    createUserWithUndefinedWahlbezirksArt,
    createUserWithDefaultValues,
    createUserDtoWithRandomValues,
    mapDtoWbIdWahlnummerToModelWahlMetaData,
  };
}
