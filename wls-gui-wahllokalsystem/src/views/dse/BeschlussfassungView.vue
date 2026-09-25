<template>
  <div>
    <v-card>
      <v-card-title>Beschlussfassung</v-card-title>
      <v-card-text>
        <base-progress-linear
          class="text-center"
          titel="bereits gefasste Beschlüsse"
          :is-loading="false"
          :current="completedStimmzettelForBeschlussfassung.length"
          :total="stimmzettelForBeschlussfassung.length"
          color="success" />
        <base-beschlussfassung-uebersicht-table
          :stimmzettel-liste="stimmzettelForBeschlussfassung"
          :stimmzettel-loading="isStimmzettelForBeschlussLoading"
          @edit-beschluss-stimmzettel="onBeschlussBearbeitenClicked($event)"
      /></v-card-text>
      <v-card-actions>
        <v-spacer />
        <base-wls-button-save
          save-text="Beschlussentscheidungen drucken"
          prepend-icon="$printer"
          :loading="isBeschlussentscheidungenDruckenLoading"
          @click="onBeschlussentscheidungenDruckenClicked"
        />
        <base-text-button
          active
          :disabled="isBeschlussfassungBeendenButtonDisabled"
          @click="onBeschlussfassungBeendenClicked"
        >
          Beschlussfassung Beenden
        </base-text-button>
      </v-card-actions>
    </v-card>
    <the-beschlussfassung-bearbeiten-dialog
      v-model="isBearbeitenDialogVisible"
      v-model:stimmzettel="activeStimmzettelForBeschluss"
      @cancel="onBeschlussBearbeitenCanceled"
      @save="onBeschlussBearbeitenSaved"
    />
    <base-beschlussentscheidungen-drucken-info-dialog
      v-model="isBeschlussentscheidungenDruckenDialogVisble"
    />
  </div>
</template>

<script setup lang="ts">
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { ref } from "vue";
import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BaseProgressLinear from "@/components/common/progressLinear/BaseProgressLinear.vue";
import BaseBeschlussentscheidungenDruckenInfoDialog from "@/components/dse/beschlussfassung/BaseBeschlussentscheidungenDruckenInfoDialog.vue";
import BaseBeschlussfassungUebersichtTable from "@/components/dse/beschlussfassung/BaseBeschlussfassungUebersichtTable.vue";
import TheBeschlussfassungBearbeitenDialog from "@/components/dse/beschlussfassung/TheBeschlussfassungBearbeitenDialog.vue";
import { useBeschlussentscheidungenDruckenTools } from "@/composables/dse/beschlussfassung/beschlussentscheidungenDruckenTools.ts";
import { useBeschlussentscheidungenDruckTemplateTools } from "@/composables/dse/beschlussfassung/beschlussentscheidungenDruckTemplateTools.ts";
import { useBeschlussfassungViewUtils } from "@/composables/dse/beschlussfassung/beschlussfassungViewUtils.ts";
import { useDseWorkflowStatusService } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusService.ts";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import router from "@/plugins/router.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { addNotification } = useUserNotificationService();
const { wahlenActions } = useWahlenStore();
const { saveDseWorkflowStatus } = useDseWorkflowStatusService();
const { setStepDone } = useWorkflowStore();
const { getNextRoute } = useNavigationService();
const route = useRoute();

const wahlID = (route.params.wahlId as string) || "";
const wahlbezirkID = (route.params.wahlbezirkId as string) || "";

const isBearbeitenDialogVisible = ref(false);
const isBeschlussentscheidungenDruckenDialogVisble = ref(false);
const activeStimmzettelForBeschluss = ref<PersistedStimmzettel>();
const isBeschlussentscheidungenDruckenLoading = ref<boolean>(false);

const {
  isStimmzettelForBeschlussLoading,
  stimmzettelForBeschlussfassung,
  isBeschlussfassungBeendenButtonDisabled,
  completedStimmzettelForBeschlussfassung,
  saveBeschlussStimmzettel,
} = useBeschlussfassungViewUtils(wahlID, wahlbezirkID);
const {
  sendAusdruckBeschlussentscheidungen,
  prepareDataForBeschlussentscheidungenDruck,
} = useBeschlussentscheidungenDruckenTools(wahlID, wahlbezirkID);
const { buildTemplate } = useBeschlussentscheidungenDruckTemplateTools();

async function onBeschlussfassungBeendenClicked() {
  await saveDseWorkflowStatus(wahlID, wahlbezirkID, {
    status: StimmzettelerfassungStatusEnum.BeAbgeschlossen,
  });

  setStepDone(wahlID, wahlbezirkID, MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG);

  await router.push(getNextRoute());
}

function onBeschlussBearbeitenClicked(stimmzettelToEdit: PersistedStimmzettel) {
  activeStimmzettelForBeschluss.value = stimmzettelToEdit;
  isBearbeitenDialogVisible.value = true;
}

function onBeschlussBearbeitenCanceled() {
  isBearbeitenDialogVisible.value = false;
}

async function onBeschlussBearbeitenSaved(stimmzettel: PersistedStimmzettel) {
  await saveBeschlussStimmzettel(stimmzettel);
  isBearbeitenDialogVisible.value = false;
}

async function onBeschlussentscheidungenDruckenClicked() {
  isBeschlussentscheidungenDruckenLoading.value = true;
  try {
    const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
    if (wahl) {
      const pdfText = buildTemplate(
        prepareDataForBeschlussentscheidungenDruck(
          wahl,
          stimmzettelForBeschlussfassung.value
        )
      );
      const printWindow = window.open(
        "",
        "",
        "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
      );

      if (printWindow) {
        printWindow.document.writeln(pdfText);
        printWindow.document.close();
        printWindow.print();
        printWindow.close();
      }

      isBeschlussentscheidungenDruckenDialogVisble.value = true;

      await sendAusdruckBeschlussentscheidungen(
        MeldungsArtEnum.Beschlussentscheidungen,
        pdfText
      );
    }
  } catch {
    addNotification(
      "Fehler beim Drucken der Beschlussentscheidungen.",
      UserNotificationCategoryEnum.WARNING
    );
  } finally {
    isBeschlussentscheidungenDruckenLoading.value = false;
  }
}
</script>
