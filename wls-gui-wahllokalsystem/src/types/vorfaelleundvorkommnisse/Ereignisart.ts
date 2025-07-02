import { storeToRefs } from "pinia";

import { useUserStore } from "@/stores/userStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export const EreignisartEnum = {
  Vorfall: "VORFALL",
  Vorkommnis: "VORKOMMNIS",
} as const;

export type EreignisartEnum =
  (typeof EreignisartEnum)[keyof typeof EreignisartEnum];

export function getEreignisArtForDateRelatedToSchliessungsuhrzeit(
  ereignisDate: Date,
  schliessungsuhrzeit: Date | undefined
): EreignisartEnum {
  const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());
  switch (currentUserWahlbezirksArt.value) {
    case WahlbezirksArtEnum.BWB:
      return EreignisartEnum.Vorkommnis;
    case WahlbezirksArtEnum.UWB:
      if (!schliessungsuhrzeit) {
        return EreignisartEnum.Vorfall;
      } else {
        return ereignisDate.getTime() > schliessungsuhrzeit.getTime()
          ? EreignisartEnum.Vorkommnis
          : EreignisartEnum.Vorfall;
      }
  }
}
