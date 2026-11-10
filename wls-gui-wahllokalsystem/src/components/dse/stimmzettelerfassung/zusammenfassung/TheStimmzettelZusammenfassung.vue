<template>
  <div>
    <v-card-subtitle class="font-weight-bold mt-2 mb-10">
      aktueller Stand der erfassten Stimmzettel
    </v-card-subtitle>
    <div class="d-flex flex-column ga-5 mx-4">
      <the-m-b-w-wahlberechtigte-anzeigen-card
        v-if="hasRoleSchriftfuehrung"
        :wahlbezirk-id="wahlbezirkID"
        :wahl-id="wahlID"
      />
      <the-m-b-w-waehler-anzeigen-card
        v-if="hasRoleSchriftfuehrung"
        :wahlbezirk-id="wahlbezirkID"
        :wahl-id="wahlID"
      />
      <base-card-ungueltige-stimmen-anzeigen
        :ungueltige-stimmen="ungueltigeStimmen"
        :ungueltige-stimmzettel-nach-beschluss="0"
      />
      <v-card>
        <v-card-title> Gültige Stimmen </v-card-title>
        <v-card-text>
          <the-m-b-w-gueltige-stimmen-anzeigen-niederschrift-table
            :wahlvorschlaege-kandidaten-ergebnisse="
              wahlvorschlaegeWithKandidatenErgebnissen
            "
            :ergebnisse-and-wahlvorschlaege="
              wahlvorschlaegeErgebnisseStapelAAndB
            "
          />
        </v-card-text>
      </v-card>
      <base-card-wahlvorschlaege-kandidatenstimmen-anzeigen
        :kandidatenstimmen="wahlvorschlaegeWithKandidatenErgebnissen"
      />
    </div>
    <v-card-actions>
      <base-text-button
        :loading="isPrinting"
        @click="onPrintSummaryClicked"
        >Zusammenfassung ausdrucken</base-text-button
      >
    </v-card-actions>
  </div>
</template>

<script setup lang="ts">
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseCardUngueltigeStimmenAnzeigen from "@/components/ergebnismeldung/common/BaseCardUngueltigeStimmenAnzeigen.vue";
import BaseCardWahlvorschlaegeKandidatenstimmenAnzeigen from "@/components/ergebnismeldung/common/BaseCardWahlvorschlaegeKandidatenstimmenAnzeigen.vue";
import TheMBWGueltigeStimmenAnzeigenNiederschriftTable from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenNiederschriftTable.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { useCommonPrintService } from "@/composables/drucken/commonPrintService.ts";
import { useMbwStimmzettelFilterService } from "@/composables/dse/mbwStimmzettelFilterService.ts";
import { useStimmzettelZusammenfassungUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelZusammenfassungUtils.ts";
import { useStatusUtils } from "@/composables/ergebnismeldung/common/statusUtils.ts";
import { useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper } from "@/composables/ergebnismeldung/MBW/mbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper.ts";
import { useMbwNiederschriftDruckService } from "@/composables/ergebnismeldung/MBW/mbwNiederschriftDruckService.ts";
import { useNiederschriftDruckBWB } from "@/composables/ergebnismeldung/MBW/niederschriftDruckBWB.ts";
import { useNiederschriftDruckUWB } from "@/composables/ergebnismeldung/MBW/niederschriftDruckUWB.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { isStimmzettelUngueltig } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { hasRoleSchriftfuehrung, currentUserWahlbezirksArt } =
  storeToRefs(useUserStore());
const { wahlenActions } = useWahlenStore();
const route = useRoute();

const { printInWindow } = useCommonPrintService();
const { logError } = useLogging("TheStimmzettelZusammenfassung");
const { addNotification } = useUserNotificationService();
const { getInitialStatus } = useStatusUtils();

const wahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;

const {
  buildNiederschriftTemplateFromData: buildNiederschriftTemplateFromDataUWB,
} = useNiederschriftDruckUWB();
const {
  buildNiederschriftTemplateFromData: buildNiederschriftTemplateFromDataBWB,
} = useNiederschriftDruckBWB();

const status = getInitialStatus(wahlID, wahlbezirkID);

const props = defineProps<{
  stimmzettelListe: PersistedStimmzettel[];
  wahlvorschlaege: Wahlvorschlag[];
}>();

const {
  stapelASumGroupedByWahlvorschlag,
  stapelBSumGroupedByWahlvorschlag,
  stapelB,
  stapelC,
} = useMbwStimmzettelFilterService(computed(() => props.stimmzettelListe));

const { prepareDataForNiederschriftDruck } = useMbwNiederschriftDruckService(
  wahlID,
  wahlbezirkID,
  computed(() => props.stimmzettelListe)
);
const { wahlvorschlaegeErgebnisseStapelAAndB } =
  useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper(
    computed(() => props.wahlvorschlaege),
    stapelASumGroupedByWahlvorschlag,
    stapelBSumGroupedByWahlvorschlag
  );

const { wahlvorschlaegeWithKandidatenErgebnissen } =
  useStimmzettelZusammenfassungUtils(
    computed(() => [...stapelB.value, ...stapelC.value]),
    computed(() => props.wahlvorschlaege)
  );

const ungueltigeStimmen = computed(
  () =>
    props.stimmzettelListe.filter((stimmzettel) =>
      isStimmzettelUngueltig(stimmzettel.gueltigkeit)
    ).length
);
const isPrinting = ref(false);

async function onPrintSummaryClicked() {
  if (isPrinting.value) {
    return;
  }

  isPrinting.value = true;
  try {
    const pdfText = await buildNiederschriftTemplate();

    await printInWindow(pdfText);
  } catch (e) {
    logError("Fehler während des Druckvorgangs oder der Datenvorbereitung", e);
    addNotification(
      "Fehler beim Drucken der Zusammenfassung",
      UserNotificationCategoryEnum.ERROR
    );
  } finally {
    isPrinting.value = false;
  }
}

async function buildNiederschriftTemplate() {
  const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
  if (wahl) {
    const templateData = await prepareDataForNiederschriftDruck(status, wahl);
    if (currentUserWahlbezirksArt.value === WahlbezirksArtEnum.UWB) {
      // @ts-expect-error correct data is determined by the if check
      return buildNiederschriftTemplateFromDataUWB(templateData);
    } else {
      // @ts-expect-error correct data is determined by the if check
      return buildNiederschriftTemplateFromDataBWB(templateData);
    }
  }
  return " ";
}
</script>
