import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";
import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";

export interface User {
  username: string;
  email: string | undefined;
  userEnabled: boolean;
  wahltagID: string;
  wahltag: string;
  wahlbezirkID: string;
  wahlbezirkNummer: string;
  wahlbezirksArt: WahlbezirksArtEnum;
  pin: string;
  authorities: Set<string>;
  wahlMetaData: WahlMetaData[];
}

export function createUserLocalDevelopment(): User {
  return {
    username: "Local Development User",
    email: undefined,
    userEnabled: true,
    wahltagID: "wahltagID",
    wahltag: "2026-01-01",
    wahlbezirkID: "wahlbezirkID",
    wahlbezirkNummer: "1234",
    wahlbezirksArt: "UWB",
    pin: "pin",
    authorities: new Set<string>(),
    wahlMetaData: [
      {
        wahlbezirkID: "wahlbezirkID",
        wahlnummer: "wahlnummer",
        wahlID: "wahlID",
      },
    ],
  };
}
