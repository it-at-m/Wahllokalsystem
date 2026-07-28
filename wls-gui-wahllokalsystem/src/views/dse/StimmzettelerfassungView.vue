<template>
  <div>
    <v-card>
      <v-card-title>Stimmzettelerfassung</v-card-title>
      <v-card-text>
        Status: {{ status ? status.status : "-" }}
        <v-skeleton-loader
          boilerplate
          type="card-avatar"
          class="mt-3"
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
      @confirm="erfassungDialogVisible = false"
      @cancel="erfassungDialogVisible = false"
    />
    <base-dialog
      :visible="beendenDialogVisible"
      icon="$information"
      dialogtitle="Stimmzettelerfassung beenden"
      cancel-disabled
      confirmtext="Ok"
      @confirm="beendenDialogVisible = !beendenDialogVisible"
      >🚧 TBD 🚧</base-dialog
    >
  </div>
</template>
<script setup lang="ts">
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";

import { computed, onActivated, ref } from "vue";
import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import TheStimmzettelkennungDialog from "@/components/dse/TheStimmzettelkennungDialog.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { useStimmzettelerfassungStatusTeamService } from "@/composables/dse/stimmzettelerfassungTeamStatusService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

const status = ref<StimmzettelerfassungTeamStatus | null>(null);
const erfassungDialogVisible = ref(false);
const beendenDialogVisible = ref(false);
const erfassungTeamStatusService = useStimmzettelerfassungStatusTeamService();

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
});

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

async function onErfassungStartenClicked() {
  await postTeamStatus({
    status: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG,
  });
  erfassungDialogVisible.value = true;
}

async function onErfassungUnterbrechenClicked() {
  await postTeamStatus({
    status: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN,
  });
}

async function onErfassungBeendenClicked() {
  beendenDialogVisible.value = true;
  await postTeamStatus({
    status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN,
  });
}
</script>
