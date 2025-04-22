import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";





class User {
  sub = "";

  // LHM
  displayName = "";
  surname = "";
  telephoneNumber = "";
  email = "";
  username = "";
  givenname = "";
  department = "";
  lhmObjectID = "";
  // LHM_Extended
  preferred_username = "";
  memberof: string[] = [];
  user_roles: string[] = [];
  authorities: string[] = [];

  wahlbezirkID? = "";
  wahlbezirksArt?: WahlbezirksArtEnum;
}

function UserLocalDevelopment(): User {
  const u = new User();
  u.username = "Local Development User";
  u.displayName = "Local Development User";
  u.wahlbezirksArt = "UWB";
  u.authorities = [
    // todo add authorities
  ];
  u.user_roles = [
    // todo add user roles
  ];
  return u;
}

export { User, UserLocalDevelopment };
