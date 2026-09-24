import type { BeschlussentscheidungenDruckInput } from "@/types/dse/beschlussfassung/BeschlussentscheidungenDruckInput.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import { storeToRefs } from "pinia";

import { useLogging } from "@/composables/common/logging.ts";
import { useDruckTemplateTools } from "@/composables/drucken/druckTemplateTools.ts";
import { useAusdruckService } from "@/composables/ergebnismeldung/common/ausdruckService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";

const { logError } = useLogging("useBeschlussentscheidungenDruckenTools");

export function useBeschlussentscheidungenDruckenTools(
  wahlID: string,
  wahlbezirkID: string
) {
  const { currentUserWahlbezirkNummer, currentUserWahlbezirksArt } =
    storeToRefs(useUserStore());

  const { postAusdruck } = useAusdruckService();
  const { createFooter } = useDruckTemplateTools();

  async function sendAusdruckBeschlussentscheidungen(
    meldungsart: MeldungsartEnum,
    ausdruck: string
  ) {
    try {
      await postAusdruck(wahlbezirkID, wahlID, meldungsart, ausdruck);
    } catch {
      logError("Fehler beim Speichern des Ausdrucks");
    }
  }

  function prepareDataForBeschlussentscheidungenDruck(
    wahl: Wahl,
    stimmzettelWithBeschluss: PersistedStimmzettel[]
  ): BeschlussentscheidungenDruckInput {
    return {
      stimmzettelWithBeschluss: stimmzettelWithBeschluss,
      wahlbezirkNummer: currentUserWahlbezirkNummer.value,
      aktuelleWahl: wahl,
      wahlbezirksArt: currentUserWahlbezirksArt.value,
      footer: createFooter(
        MeldungValidierungsstatusEnum.Valide,
        currentUserWahlbezirkNummer.value
      ),
    };
  }

  return {
    sendAusdruckBeschlussentscheidungen,
    prepareDataForBeschlussentscheidungenDruck,
  };
}
