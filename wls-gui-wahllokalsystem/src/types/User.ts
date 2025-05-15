import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";
import type { WbId_Wahlnummer } from "@/types/wlsTypes/WbId_Wahlnummer.ts";

class User {
  username = "";
  email = "";
  userEnabled? = false;
  wahltagID? = "";
  wahltag? = "";
  wahlbezirkID? = "";
  wahlbezirkNummer? = "";
  wahlbezirksArt?: WahlbezirksArtEnum;
  pin = "";
  authorities = new Set<string>();
  wbid_wahlnummer?: WbId_Wahlnummer[];
}

function UserLocalDevelopment(): User {
  const u = new User();
  u.username = "Local Development User";
  u.wahlbezirksArt = "UWB";
  u.wbid_wahlnummer = [
    {
      wahlbezirkID: "",
      wahlnummer: "",
      wahlID: "",
    },
  ];

  return u;
}

export { User, UserLocalDevelopment };
