import type { UserDTO } from "@/api/wls-clients/generated-auth-api";

import { User } from "@/types/User.ts";

export function useUserMapper() {
  function toModel(userDto: UserDTO): User {
    const user: User = new User();

    user.username = userDto.username || "";
    user.email = userDto.email || "";
    user.userEnabled = userDto.userEnabled || false;
    user.wahltagID = userDto.wahltagID || "";
    user.wahltag = userDto.wahltag || "";
    user.wahlbezirkID = userDto.wahlbezirkID || "";
    user.wahlbezirkNummer = userDto.wahlbezirkNummer || "";
    user.wahlbezirksArt = userDto.wahlbezirksArt || undefined;
    user.pin = userDto.pin || "";
    user.authorities = userDto.authorities || new Set<string>();
    user.wahlMetaData = JSON.parse(userDto.wbid_wahlnummer) || undefined;

    return user;
  }

  return { toModel };
}
