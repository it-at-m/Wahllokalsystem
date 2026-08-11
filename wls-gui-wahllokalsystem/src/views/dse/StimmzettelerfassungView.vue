<template>
  <div>
    <v-card>
      <v-card-title>Stimmzettelerfassung</v-card-title>
      <v-card-text>
        Status: {{ teamStatus ? teamStatus.status : "-" }}
        <v-skeleton-loader
          :boilerplate="!isStatusLoading"
          type="card-avatar"
          class="mt-3"
        />
      </v-card-text>
      <v-card-actions v-if="!isStatusLoading">
        <base-text-button
          :active="startenBtnActive"
          :is-disabled="startenBtnIsDisabled"
          @click="onErfassungStartenClicked"
          >Starten</base-text-button
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
      @cancel="onStimmzettelErfassungCanceled"
      @confirm="onStimmzettelErfassungConfirmed"
    />
    <the-stimmzettelerfassung-beenden-dialog
      v-model="isBeendenDialogVisible"
      :wahl-id="wahlID"
      :wahlbezirk-id="wahlbezirkID"
      :team-id="teamID"
    />
  </div>
</template>
<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";

import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import TheStimmzettelerfassungBeendenDialog from "@/components/dse/TheStimmzettelerfassungBeendenDialog.vue";
import TheStimmzettelErfassungDialog from "@/components/dse/TheStimmzettelErfassungDialog.vue";
import TheStimmzettelkennungDialog from "@/components/dse/TheStimmzettelkennungDialog.vue";
import { useStimmzettelErfassungViewUtils } from "@/composables/dse/stimmzettelErfassungViewUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const route = useRoute();
const userStore = useUserStore();
const teamID = userStore.currentUserTeamName || "";
const wahlID = (route.params.wahlId as string) || "";
const wahlbezirkID = (route.params.wahlbezirkId as string) || "";

const {
  activeStimmzettel,
  beendenBtnActive,
  beendenBtnIsDisabled,
  isBeendenDialogVisible,
  isErfassungsDialogVisible,
  isKennungsDialogVisible,
  isStatusLoading,
  savedStimmzettel,
  startenBtnIsDisabled,
  startenBtnActive,
  teamStatus,
  unterbrechenBtnIsDisabled,
  saveNewStimmzettel,
  sendStatusInBearbeitung,
  sendStatusUnterbrochen,
  startNewEmptyStimmzettelWithStimmzettelkennung,
} = useStimmzettelErfassungViewUtils(wahlID, wahlbezirkID, teamID);

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

async function onErfassungBeendenClicked() {
  isBeendenDialogVisible.value = true;
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
