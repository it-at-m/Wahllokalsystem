import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";

export interface User {
  username: string;
  email: string | undefined;
  userEnabled: boolean | undefined;
  wahltagID: string | undefined;
  wahltag: string | undefined;
  wahlbezirkID: string | undefined;
  wahlbezirkNummer: string | undefined;
  wahlbezirksArt: WahlbezirksArtEnum | undefined;
  pin: string | undefined;
  authorities: Set<string>;
  wahlMetaData: WahlMetaData[] | undefined;
}

export function createUserLocalDevelopment(): User {
  return {
    username: "Local Development User",
    email: undefined,
    userEnabled: undefined,
    wahltagID: undefined,
    wahltag: undefined,
    wahlbezirkID: undefined,
    wahlbezirkNummer: "1234",
    wahlbezirksArt: "UWB",
    pin: undefined,
    authorities: new Set<string>(),
    wahlMetaData: [{ wahlbezirkID: "", wahlnummer: "", wahlID: "" }],
  };
}
