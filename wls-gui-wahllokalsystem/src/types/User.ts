import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";
import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";

export interface User {
  username: string;
  email: string;
  userEnabled?: boolean | undefined;
  wahltagID?: string | undefined;
  wahltag?: string | undefined;
  wahlbezirkID?: string | undefined;
  wahlbezirkNummer?: string | undefined;
  wahlbezirksArt?: WahlbezirksArtEnum | undefined;
  pin: string;
  authorities: Set<string>;
  wahlMetaData?: WahlMetaData[] | undefined;
}

export function createUserLocalDevelopment(): User {
  return {
    username: "Local Development User",
    email: "",
    wahlbezirksArt: "UWB",
    wahlbezirkNummer: "1234",
    pin: "",
    authorities: new Set<string>(),
    wahlMetaData: [{ wahlbezirkID: "", wahlnummer: "", wahlID: "" }],
  };
}
