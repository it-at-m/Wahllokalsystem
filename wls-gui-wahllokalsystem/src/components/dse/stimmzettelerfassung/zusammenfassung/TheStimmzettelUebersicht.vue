<template>
  <div>
    <base-stimmzettel-uebersicht-table
      :team-id="teamId"
      :stimmzettel-liste="stimmzettelListe"
      :stimmzettel-loading="isStimmzettelLoading"
      class="mt-3"
      :bearbeitung-disabled="isStatusLoading || hasTeamFinishedErfassung"
      @stimmzettel-bearbeiten="onStimmzettelBearbeitenClicked"
    />
    <v-card-actions v-if="!isStatusLoading">
      <div
        v-if="!hasTeamFinishedErfassung"
        class="d-flex w-100"
      >
        <base-text-button
          :active="startenBtnActive"
          class="mr-5"
          @click="onErfassungStartenClicked"
        >
          {{ startNewStimmzettelButtonText }}
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
          @click="onErfassungBeendenClicked"
        >
          Beenden
        </base-text-button>
      </div>
      <div
        v-else
        class="w-100"
      >
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
    <the-stimmzettelkennung-dialog
      :visible="isKennungsDialogVisible"
      :team-name="teamID"
      :existing-stimmzettel="stimmzettelListe"
      @confirm="onStimmzettelkennungConfirmed"
      @cancel="onStimmzettelkennungCanceled"
    />
    <the-stimmzettel-erfassung-dialog
      v-if="activeStimmzettel"
      :ref="STIMMZETTEL_ERFASSUNG_DIALOG_TEMPLATE_REF_NAME"
      v-model="isErfassungsDialogVisible"
      :stimmzettel="activeStimmzettel"
      :wahlvorschlaege="wahlvorschlaege"
      @cancel="onStimmzettelErfassungCanceled"
      @confirm-close="onStimmzettelErfassungConfirmed"
      @confirm-next="onStimmzettelErfassungConfirmedAndOpenNextStimmzettel"
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
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { computed, useTemplateRef } from "vue";
import { useRoute } from "vue-router";

import BaseButtonRefresh from "@/components/common/buttons/BaseButtonRefresh.vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import BaseStimmzettelUebersichtTable from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelUebersichtTable.vue";
import TheStimmzettelerfassungBeendenDialog from "@/components/dse/stimmzettelerfassung/dialogs/TheStimmzettelerfassungBeendenDialog.vue";
import TheStimmzettelErfassungDialog from "@/components/dse/stimmzettelerfassung/dialogs/TheStimmzettelErfassungDialog.vue";
import TheStimmzettelkennungDialog from "@/components/dse/stimmzettelerfassung/dialogs/TheStimmzettelkennungDialog.vue";
import { useStimmzettelErfassungViewUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelErfassungViewUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";

const props = defineProps<{
  teamId: string;
  stimmzettelListe: Stimmzettel[];
  isStimmzettelLoading: boolean;
  hasStimmzettel: boolean;
  wahlvorschlaege: Wahlvorschlag[];
  saveStimmzettel: (stimmzettel: Stimmzettel) => Promise<void>;
}>();

const STIMMZETTEL_BEENDEN_DIALOG_TEMPLATE_REF_NAME = "stimmzettelBeendenDialog";
const STIMMZETTEL_ERFASSUNG_DIALOG_TEMPLATE_REF_NAME =
  "stimmzettelErfassenDialog";

const route = useRoute();
const userStore = useUserStore();
const teamID = userStore.currentUserTeamName || "";
const wahlID = (route.params.wahlId as string) || "";
const wahlbezirkID = (route.params.wahlbezirkId as string) || "";

const templateRefStimmzettelBeendenDialog = useTemplateRef<
  InstanceType<typeof TheStimmzettelerfassungBeendenDialog>
>(STIMMZETTEL_BEENDEN_DIALOG_TEMPLATE_REF_NAME);

const templateRefStimmzettelErfassenDialog = useTemplateRef<
  InstanceType<typeof TheStimmzettelErfassungDialog>
>(STIMMZETTEL_ERFASSUNG_DIALOG_TEMPLATE_REF_NAME);

const {
  teamStatus,
  activeStimmzettel,
  beendenBtnActive,
  isErfassungsDialogVisible,
  isKennungsDialogVisible,
  isStatusLoading,
  startenBtnActive,
  unterbrechenBtnIsDisabled,
  sendStatusInBearbeitung,
  sendStatusUnterbrochen,
  startNewEmptyStimmzettelWithStimmzettelkennung,
  reloadTeamStatus,
} = useStimmzettelErfassungViewUtils(wahlID, wahlbezirkID, teamID);

const startNewStimmzettelButtonText = computed(() =>
  props.hasStimmzettel ||
  teamStatus.value?.status ===
    StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN ||
  teamStatus.value?.status === StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG
    ? "Fortsetzen"
    : "Starten"
);

function onErfassungStartenClicked() {
  isKennungsDialogVisible.value = true;
}

async function onStimmzettelkennungConfirmed(stimmzettelKennung: number) {
  await sendStatusInBearbeitung();
  isKennungsDialogVisible.value = false;
  startNewEmptyStimmzettelWithStimmzettelkennung(stimmzettelKennung);
  if (isErfassungsDialogVisible.value) {
    templateRefStimmzettelErfassenDialog.value?.focusCommandProcessingTextField();
  } else {
    isErfassungsDialogVisible.value = true;
  }
}

function onStimmzettelkennungCanceled() {
  isKennungsDialogVisible.value = false;
  isErfassungsDialogVisible.value = false;
}

async function onErfassungUnterbrechenClicked() {
  await sendStatusUnterbrochen();
}

function onErfassungBeendenClicked() {
  templateRefStimmzettelBeendenDialog.value?.showDialog();
}

async function onErfassungAktualisierenClicked() {
  await reloadTeamStatus();
}

async function onStimmzettelErfassungCanceled() {
  isErfassungsDialogVisible.value = false;
}
async function onStimmzettelErfassungConfirmed(
  confirmedStimmzettel: Stimmzettel
) {
  await props.saveStimmzettel(confirmedStimmzettel);
  isErfassungsDialogVisible.value = false;
}
async function onStimmzettelErfassungConfirmedAndOpenNextStimmzettel(
  confirmedStimmzettel: Stimmzettel
) {
  await props.saveStimmzettel(confirmedStimmzettel);
  isKennungsDialogVisible.value = true;
}
async function onStimmzettelBearbeitenClicked(stimmzettel: Stimmzettel) {
  if (
    teamStatus.value?.status === StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN
  )
    await sendStatusInBearbeitung();

  activeStimmzettel.value = stimmzettel;
  isErfassungsDialogVisible.value = true;
}

const hasTeamFinishedErfassung = computed(
  () =>
    teamStatus.value?.status == StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN
);
</script>
