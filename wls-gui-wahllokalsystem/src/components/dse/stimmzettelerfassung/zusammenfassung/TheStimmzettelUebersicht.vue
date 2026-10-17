<template>
  <div>
    <base-stimmzettel-uebersicht-table
      :team-id="teamId"
      :stimmzettel-liste="stimmzettelListe"
      :stimmzettel-loading="isStimmzettelLoading"
      class="mt-3"
      :bearbeitung-disabled="
        isStatusLoading ||
        isWahlvorschlaegeLoading ||
        hasTeamFinishedErfassung ||
        isElectionFinished
      "
      @stimmzettel-bearbeiten="onStimmzettelBearbeitenClicked"
      @stimmzettel-ansehen="onStimmzettelAnsehenClicked"
    />
    <v-card-actions v-if="!isStatusLoading">
      <div
        v-if="!hasTeamFinishedErfassung && !isElectionFinished"
        class="d-flex w-100"
      >
        <base-text-button
          :active="startenBtnActive"
          :is-disabled="isWahlvorschlaegeLoading"
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
        v-else-if="!isElectionFinished"
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
      v-if="isErfassungsDialogPreloaded"
      :ref="STIMMZETTEL_ERFASSUNG_DIALOG_TEMPLATE_REF_NAME"
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
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { computed, nextTick, ref, useTemplateRef, watch } from "vue";
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
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";

const props = defineProps<{
  teamId: string;
  stimmzettelListe: PersistedStimmzettel[];
  isStimmzettelLoading: boolean;
  isWahlvorschlaegeLoading: boolean;
  hasStimmzettel: boolean;
  wahlvorschlaege: Wahlvorschlag[];
  saveStimmzettel: (stimmzettel: PersistedStimmzettel) => Promise<void>;
}>();

const STIMMZETTEL_BEENDEN_DIALOG_TEMPLATE_REF_NAME = "stimmzettelBeendenDialog";
const STIMMZETTEL_ERFASSUNG_DIALOG_TEMPLATE_REF_NAME =
  "stimmzettelErfassenDialog";

const route = useRoute();
const userStore = useUserStore();
const teamID = userStore.currentUserTeamName || "";
const wahlID = (route.params.wahlId as string) || "";
const wahlbezirkID = (route.params.wahlbezirkId as string) || "";
const isErfassungsDialogPreloaded = ref(false);

const templateRefStimmzettelBeendenDialog = useTemplateRef<
  InstanceType<typeof TheStimmzettelerfassungBeendenDialog>
>(STIMMZETTEL_BEENDEN_DIALOG_TEMPLATE_REF_NAME);

const templateRefStimmzettelErfassenDialog = useTemplateRef<
  InstanceType<typeof TheStimmzettelErfassungDialog>
>(STIMMZETTEL_ERFASSUNG_DIALOG_TEMPLATE_REF_NAME);

const {
  teamStatus,
  beendenBtnActive,
  isKennungsDialogVisible,
  isStatusLoading,
  startenBtnActive,
  unterbrechenBtnIsDisabled,
  ensureStatusInBearbeitung,
  sendStatusUnterbrochen,
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

watch(
  () => [props.isWahlvorschlaegeLoading, props.wahlvorschlaege.length] as const,
  async ([isLoading, numberOfWahlvorschlaege]) => {
    if (!isLoading && numberOfWahlvorschlaege > 0) {
      await enableErfassungsDialog();
    }
  },
  { immediate: true }
);

function onErfassungStartenClicked() {
  isKennungsDialogVisible.value = true;
}

async function onStimmzettelkennungConfirmed(stimmzettelKennung: number) {
  await ensureStatusInBearbeitung();
  isKennungsDialogVisible.value = false;
  await enableErfassungsDialog();
  templateRefStimmzettelErfassenDialog.value?.showDialog(stimmzettelKennung);
}

function onStimmzettelkennungCanceled() {
  isKennungsDialogVisible.value = false;
  templateRefStimmzettelErfassenDialog.value?.closeDialog();
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
  templateRefStimmzettelErfassenDialog.value?.closeDialog();
}
async function onStimmzettelErfassungConfirmed(
  confirmedStimmzettel: PersistedStimmzettel
) {
  await props.saveStimmzettel(confirmedStimmzettel);
  templateRefStimmzettelErfassenDialog.value?.closeDialog();
}
async function onStimmzettelErfassungConfirmedAndOpenNextStimmzettel(
  confirmedStimmzettel: PersistedStimmzettel
) {
  await props.saveStimmzettel(confirmedStimmzettel);
  isKennungsDialogVisible.value = true;
}
async function onStimmzettelBearbeitenClicked(
  stimmzettel: PersistedStimmzettel
) {
  await ensureStatusInBearbeitung();
  await enableErfassungsDialog();
  templateRefStimmzettelErfassenDialog.value?.showDialog(stimmzettel);
}
async function onStimmzettelAnsehenClicked(stimmzettel: PersistedStimmzettel) {
  await enableErfassungsDialog();
  templateRefStimmzettelErfassenDialog.value?.showDialog(stimmzettel, {
    readonly: true,
  });
}

async function enableErfassungsDialog() {
  if (!isErfassungsDialogPreloaded.value) {
    isErfassungsDialogPreloaded.value = true;
  }
  // update dialog DOM
  await nextTick();
}

const hasTeamFinishedErfassung = computed(
  () =>
    teamStatus.value?.status == StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN
);

const isElectionFinished = computed(() =>
  useWorkflowStore().isElectionFinished(wahlID, wahlbezirkID)
);
</script>
