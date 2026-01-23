import type { NavigationDefinition } from "@/types/navigation/NavigationDefinition.ts";
import type { ComputedRef } from "vue";

import { computed } from "vue";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

export function useMbwNavigationService(wahlID: string, wahlbezirkID: string) {
  const { getStimmzettelTermForWahlID } = useTextFormatter();

  const { getStatus } = useStatusStore();
  const status = getStatus(wahlID, wahlbezirkID);

  const navigation: ComputedRef<NavigationDefinition<MbwRoutesEnum>[]> =
    computed(() => [
      {
        title: `Zählen der ${getStimmzettelTermForWahlID(wahlID)}`,
        targetRoute: mbwRoutesRecord.createRoute(
          MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Ungültige Stimmzettel`,
        targetRoute: mbwRoutesRecord.createRoute(
          MbwRoutesEnum.MBW_STAPEL_D,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Gültige Stimmzettel`,
        targetRoute: mbwRoutesRecord.createRoute(
          MbwRoutesEnum.MBW_STAPEL_A_AND_B,
          wahlID,
          wahlbezirkID
        ),
        disabled: status
          ? !status.stepsDone[StapelArtEnum.MbwDUngueltig]
          : false,
      },
      {
        title: `Schnellmeldung`,
        targetRoute: mbwRoutesRecord.createRoute(
          MbwRoutesEnum.MBW_SCHNELLMELDUNG,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Kandidatinnen- und Kandidatenstimmen`,
        targetRoute: mbwRoutesRecord.createRoute(
          MbwRoutesEnum.MBW_STAPEL_BC,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Niederschrift`,
        targetRoute: mbwRoutesRecord.createRoute(
          MbwRoutesEnum.MBW_NIEDERSCHRIFT,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
    ]);

  return {
    navigation,
  };
}
