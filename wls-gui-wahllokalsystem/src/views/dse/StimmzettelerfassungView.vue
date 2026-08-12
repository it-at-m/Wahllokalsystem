<template>
  <div>
    <v-card>
      <v-card-title>Stimmzettelerfassung</v-card-title>
      <v-card-text>
        <the-stimmzettel-uebersicht-table
          :team-id="teamID"
          :stimmzettel-liste="stimmzettelListe"
          :stimmzettel-loading="stimmzettelLoading"
        />
      </v-card-text>
      <v-card-actions>
        <base-text-button
          :active="startenBtnActive"
          @click="onErfassungStartenClicked"
          >Starten</base-text-button
        >
        <base-text-button @click="onErfassungUnterbrechenClicked"
          >Unterbrechen</base-text-button
        >
        <base-text-button
          class="ms-auto"
          :active="beendenBtnActive"
          @click="onErfassungBeendenClicked"
          >Beenden</base-text-button
        >
      </v-card-actions>
    </v-card>
    <the-stimmzettelkennung-dialog
      :visible="erfassungDialogVisible"
      :wahl-i-d="wahlID"
      @confirm="onStimmzettelkennungConfirmed"
      @cancel="erfassungDialogVisible = false"
    />
    <the-stimmzettelerfassung-beenden-dialog
      v-model="beendenDialogVisible"
      :wahl-id="wahlID"
      :wahlbezirk-id="wahlbezirkID"
      :team-id="teamID"
    />
  </div>
</template>
<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";

import { computed, onActivated, ref } from "vue";
import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import TheStimmzettelerfassungBeendenDialog from "@/components/dse/TheStimmzettelerfassungBeendenDialog.vue";
import TheStimmzettelkennungDialog from "@/components/dse/TheStimmzettelkennungDialog.vue";
import TheStimmzettelUebersichtTable from "@/components/dse/TheStimmzettelUebersichtTable.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatusService.ts";
import { useStimmzettelService } from "@/composables/dse/stimmzettelService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

const status = ref<StimmzettelerfassungTeamStatus | null>(null);
const erfassungDialogVisible = ref(false);
const beendenDialogVisible = ref(false);
const erfassungTeamStatusService = useStimmzettelerfassungTeamStatusService();

const stimmzettelListe = ref<Stimmzettel[]>([]);
const stimmzettelLoading = ref(false);
const { getStimmzettel } = useStimmzettelService();

const route = useRoute();
const userStore = useUserStore();
const { logError } = useLogging("stimmzettelerfassungView");
const teamID = userStore.currentUserTeamName || "";
const wahlID = (route.params.wahlId as string) || "";
const wahlbezirkID = (route.params.wahlbezirkId as string) || "";

const startenBtnActive = computed(
  () =>
    status.value?.status == StimmzettelerfassungTeamStatusEnum.REGISTRIERT ||
    status.value?.status == StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN
);
const beendenBtnActive = computed(
  () =>
    status.value?.status == StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG
);

async function loadTeamStatus() {
  const loaded = await erfassungTeamStatusService.loadErfassungTeamStatus(
    wahlID,
    wahlbezirkID,
    teamID,
    false
  );
  if (loaded) {
    status.value = loaded;
  }
}

onActivated(async () => {
  await loadTeamStatus();
  await loadStimmzettel();
});

async function loadStimmzettel() {
  stimmzettelLoading.value = true;
  try {
    const liste = await getStimmzettel(wahlID, wahlbezirkID, teamID, true);
    stimmzettelListe.value = liste ?? [];
  } catch (error) {
    logError("Fehler beim Laden der Stimmzettel", error);
  } finally {
    stimmzettelLoading.value = false;
  }
}

async function postTeamStatus(
  statusToChange: StimmzettelerfassungTeamStatus,
  sendNotification = false
) {
  try {
    await erfassungTeamStatusService.postErfassungTeamStatus(
      wahlID,
      wahlbezirkID,
      teamID,
      statusToChange,
      sendNotification
    );
    status.value = statusToChange;
  } catch (error) {
    logError("Fehler beim Speichern des Team-Status", error);
  }
}

function onErfassungStartenClicked() {
  erfassungDialogVisible.value = true;
}

async function onStimmzettelkennungConfirmed() {
  await postTeamStatus({
    status: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG,
  });
  await loadStimmzettel();
  erfassungDialogVisible.value = false;
}

async function onErfassungUnterbrechenClicked() {
  await postTeamStatus({
    status: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN,
  });
}

async function onErfassungBeendenClicked() {
  beendenDialogVisible.value = true;
}
</script>
