import type { UserDTO } from "@/api/wls-clients/generated-auth-api";
import type { User } from "@/types/User.ts";

export function useUserMapper() {
  function toModel(userDto: UserDTO): User {
    const user: User = {
      username: userDto.username,
      email: userDto.email ?? undefined,
      userEnabled: userDto.userEnabled,
      wahltagID: userDto.wahltagID,
      wahltag: userDto.wahltag,
      wahlbezirkID: userDto.wahlbezirkID,
      wahlbezirkNummer: userDto.wahlbezirkNummer,
      wahlbezirksArt: userDto.wahlbezirksArt,
      pin: userDto.pin,
      authorities: userDto.authorities,
      wahlMetaData: [],
    };

    try {
      const parsed_wbid_wahlnummer = userDto.wbid_wahlnummer
        ? JSON.parse(userDto.wbid_wahlnummer)
        : undefined;

      user.wahlMetaData = parsed_wbid_wahlnummer?.wbid_wahlnummer
        ? parsed_wbid_wahlnummer.wbid_wahlnummer
        : [];
    } catch {
      console.debug("failed to parse JSON wbid_wahlnummer");
      user.wahlMetaData = [];
    }

    return user;
  }

  return { toModel };
}
