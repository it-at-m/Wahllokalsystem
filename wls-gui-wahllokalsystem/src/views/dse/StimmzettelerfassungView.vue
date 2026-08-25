<template>
  <div>
    <v-card>
      <v-card-title>Stimmzettelerfassung</v-card-title>
      <v-card-text>
        <base-stimmzettel-uebersicht-table
          :team-id="teamID"
          :stimmzettel-liste="savedStimmzettel"
          :stimmzettel-loading="isStimmzettelLoading"
          class="mt-3"
        />
      </v-card-text>
      <v-card-actions v-if="!isStatusLoading">
        <base-text-button
          :active="startenBtnActive"
          :is-disabled="startenBtnIsDisabled"
          @click="onErfassungStartenClicked"
          >{{ startNewStimmzettelButtonText }}</base-text-button
        >
        <base-text-button
          :is-disabled="unterbrechenBtnIsDisabled"
          @click="onErfassungUnterbrechenClicked"
          >Unterbrechen</base-text-button
        >
        <base-text-button
          class="ms-auto"
          :active="beendenBtnActive"
          :is-disabled="beendenBtnIsDisabled"
          @click="onErfassungBeendenClicked"
          >Beenden</base-text-button
        >
      </v-card-actions>
    </v-card>
    <the-stimmzettelkennung-dialog
      :visible="isKennungsDialogVisible"
      :team-name="teamID"
      :existing-stimmzettel="savedStimmzettel"
      @confirm="onStimmzettelkennungConfirmed"
      @cancel="isKennungsDialogVisible = false"
    />
    <the-stimmzettel-erfassung-dialog
      v-if="activeStimmzettel"
      v-model="isErfassungsDialogVisible"
      :stimmzettel="activeStimmzettel"
      :wahlvorschlaege="wahlvorschlaege"
      @cancel="onStimmzettelErfassungCanceled"
      @confirm="onStimmzettelErfassungConfirmed"
    />
    <the-stimmzettelerfassung-beenden-dialog
      :ref="STIMMZETTEL_BEENDEN_DIALOG_TEMPLATE_REF_NAME"
      :wahl-id="wahlID"
      :wahlbezirk-id="wahlbezirkID"
      :team-id="teamID"
    />
  </div>
</template>
<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/persistedStimmzettel/Stimmzettel.ts";

import { computed, useTemplateRef } from "vue";
import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseStimmzettelUebersichtTable from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelUebersichtTable.vue";
import TheStimmzettelerfassungBeendenDialog from "@/components/dse/stimmzettelerfassung/dialogs/TheStimmzettelerfassungBeendenDialog.vue";
import TheStimmzettelErfassungDialog from "@/components/dse/stimmzettelerfassung/dialogs/TheStimmzettelErfassungDialog.vue";
import TheStimmzettelkennungDialog from "@/components/dse/stimmzettelerfassung/dialogs/TheStimmzettelkennungDialog.vue";
import { useStimmzettelErfassungViewUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelErfassungViewUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const STIMMZETTEL_BEENDEN_DIALOG_TEMPLATE_REF_NAME = "stimmzettelBeendenDialog";

const route = useRoute();
const userStore = useUserStore();
const teamID = userStore.currentUserTeamName || "";
const wahlID = (route.params.wahlId as string) || "";
const wahlbezirkID = (route.params.wahlbezirkId as string) || "";

const templateRefStimmzettelBeendenDialog = useTemplateRef<
  InstanceType<typeof TheStimmzettelerfassungBeendenDialog>
>(STIMMZETTEL_BEENDEN_DIALOG_TEMPLATE_REF_NAME);

const {
  activeStimmzettel,
  beendenBtnActive,
  beendenBtnIsDisabled,
  hasStimmzettel,
  isErfassungsDialogVisible,
  isKennungsDialogVisible,
  isStatusLoading,
  isStimmzettelLoading,
  savedStimmzettel,
  startenBtnIsDisabled,
  startenBtnActive,
  unterbrechenBtnIsDisabled,
  wahlvorschlaege,
  saveNewStimmzettel,
  sendStatusInBearbeitung,
  sendStatusUnterbrochen,
  startNewEmptyStimmzettelWithStimmzettelkennung,
} = useStimmzettelErfassungViewUtils(wahlID, wahlbezirkID, teamID);

const startNewStimmzettelButtonText = computed(() =>
  hasStimmzettel.value ? "Fortsetzen" : "Starten"
);

function onErfassungStartenClicked() {
  isKennungsDialogVisible.value = true;
}

async function onStimmzettelkennungConfirmed(stimmzettelKennung: number) {
  await sendStatusInBearbeitung();
  isKennungsDialogVisible.value = false;
  startNewEmptyStimmzettelWithStimmzettelkennung(stimmzettelKennung);
  isErfassungsDialogVisible.value = true;
}

async function onErfassungUnterbrechenClicked() {
  await sendStatusUnterbrochen();
}

function onErfassungBeendenClicked() {
  templateRefStimmzettelBeendenDialog.value?.showDialog();
}

async function onStimmzettelErfassungCanceled() {
  isErfassungsDialogVisible.value = false;
}
async function onStimmzettelErfassungConfirmed(
  confirmedStimmzettel: Stimmzettel
) {
  await saveNewStimmzettel(confirmedStimmzettel);
  isErfassungsDialogVisible.value = false;
}
</script>
