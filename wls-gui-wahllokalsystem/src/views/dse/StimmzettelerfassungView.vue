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
        <div
          v-if="!teamFinishedErfassung"
          class="d-flex w-100"
        >
          <base-text-button
            :active="startenBtnActive"
            :is-disabled="startenBtnIsDisabled"
            @click="onErfassungStartenClicked"
          >
            Starten
          </base-text-button>
          <base-text-button
            :is-disabled="unterbrechenBtnIsDisabled"
            @click="onErfassungUnterbrechenClicked"
          >
            Unterbrechen
          </base-text-button>
          <base-text-button
            class="ms-auto"
            :active="beendenBtnActive"
            :is-disabled="beendenBtnIsDisabled"
            @click="onErfassungBeendenClicked"
          >
            Beenden
          </base-text-button>
        </div>
        <div v-else>
          <base-feedback-card
            title="Sie haben die Erfassung bereits abgeschlossen"
            type="information"
          >
            Um weitere Stimmzettel zu erfassen oder zu korrigieren, lassen Sie
            sich bitte von der Schriftführung wieder freischalten und
            aktualisieren Sie dann mit dem Button die Seite.
            <base-button-refresh
              active
              class="ml-5"
              @click="onErfassungAktualisierenClicked"
            />
          </base-feedback-card>
        </div>
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
      :ref="STIMMZETTEL_BEENDEN_DIALOG_TEMPLATE_REF_NAME"
      :wahl-id="wahlID"
      :wahlbezirk-id="wahlbezirkID"
      :team-id="teamID"
    />
  </div>
</template>
<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import { computed, useTemplateRef } from "vue";
import { useRoute } from "vue-router";

import BaseButtonRefresh from "@/components/common/buttons/BaseButtonRefresh.vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import BaseStimmzettelUebersichtTable from "@/components/dse/BaseStimmzettelUebersichtTable.vue";
import TheStimmzettelerfassungBeendenDialog from "@/components/dse/TheStimmzettelerfassungBeendenDialog.vue";
import TheStimmzettelErfassungDialog from "@/components/dse/TheStimmzettelErfassungDialog.vue";
import TheStimmzettelkennungDialog from "@/components/dse/TheStimmzettelkennungDialog.vue";
import { useStimmzettelErfassungViewUtils } from "@/composables/dse/stimmzettelErfassungViewUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

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
  teamStatus,
  activeStimmzettel,
  beendenBtnActive,
  beendenBtnIsDisabled,
  isErfassungsDialogVisible,
  isKennungsDialogVisible,
  isStatusLoading,
  isStimmzettelLoading,
  savedStimmzettel,
  startenBtnIsDisabled,
  startenBtnActive,
  unterbrechenBtnIsDisabled,
  saveNewStimmzettel,
  sendStatusInBearbeitung,
  sendStatusUnterbrochen,
  startNewEmptyStimmzettelWithStimmzettelkennung,
  reloadTeamStatus,
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

function onErfassungBeendenClicked() {
  templateRefStimmzettelBeendenDialog.value?.showDialog();
}

async function onErfassungAktualisierenClicked() {
  // reload will update button disabled states
  await reloadTeamStatus();
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

const teamFinishedErfassung = computed(
  () =>
    teamStatus.value?.status == StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN
);
</script>
