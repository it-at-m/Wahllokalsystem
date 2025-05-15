import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";
import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";

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
  wahlMetaData?: WahlMetaData[];
}

function UserLocalDevelopment(): User {
  const u = new User();
  u.username = "Local Development User";
  u.wahlbezirksArt = "UWB";
  u.wahlMetaData = [
    {
      wahlbezirkID: "",
      wahlnummer: "",
      wahlID: "",
    },
  ];

  return u;
}

export { User, UserLocalDevelopment };
