import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";
import type { WbId_Wahlnummer } from "@/types/wlsTypes/WbId_Wahlnummer.ts";

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
  // WLS_Extended
  wahlbezirkID? = "";
  wahlbezirksArt?: WahlbezirksArtEnum;
  wahltagID? = "";
  wbid_wahlnummer?: WbId_Wahlnummer;
}

function UserLocalDevelopment(): User {
  const u = new User();
  u.username = "Local Development User";
  u.displayName = "Local Development User";
  u.wahlbezirksArt = "UWB";
  u.wbid_wahlnummer = {
    wbid_wahlnummer: [
      {
        wahlbezirkID: "",
        wahlnummer: "",
        wahlID: "",
      },
    ],
  };
  u.authorities = [
    // todo add authorities
  ];
  u.user_roles = [
    // todo add user roles
  ];
  return u;
}

export { User, UserLocalDevelopment };
