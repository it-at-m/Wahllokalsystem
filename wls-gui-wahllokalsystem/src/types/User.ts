import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";

import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

class User {
  username = "";
  email = "";
  userEnabled? = false;
  wahltagID? = "";
  wahltag? = "";
  wahlbezirkID? = "";
  wahlbezirkNummer? = "";
  wahlbezirksArt: WahlbezirksArtEnum = WahlbezirksArtEnum.UWB;
  pin = "";
  authorities = new Set<string>();
  wahlMetaData?: WahlMetaData[];
}

function createUserLocalDevelopment(): User {
  const u = new User();
  u.username = "Local Development User";
  u.wahlbezirksArt = "UWB";
  u.wahlbezirkNummer = "1234";
  u.wahlMetaData = [
    {
      wahlbezirkID: "",
      wahlnummer: "",
      wahlID: "",
    },
  ];

  return u;
}

export { User, createUserLocalDevelopment };
