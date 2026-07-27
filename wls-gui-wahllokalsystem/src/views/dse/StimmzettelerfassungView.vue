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
          @click="startErfassung"
          >Starten</base-text-button
        >
        <base-text-button @click="unterbrechen">Unterbrechen</base-text-button>
        <base-text-button
          class="ms-auto"
          :active="beendenBtnActive"
          @click="beenden"
          >Beenden</base-text-button
        >
      </v-card-actions>
    </v-card>
    <base-dialog
      :visible="erfassungDialog"
      icon="$information"
      dialogtext="🚧 TBD 🚧"
      dialogtitle="Stimmzettel erfassen"
      cancel-disabled
      confirmtext="Ok"
      @confirm="erfassungDialog = !erfassungDialog"
    />
    <base-dialog
      :visible="beendenDialog"
      icon="$information"
      dialogtext="🚧 TBD 🚧"
      dialogtitle="Stimmzettelerfassung beenden"
      cancel-disabled
      confirmtext="Ok"
      @confirm="beendenDialog = !beendenDialog"
    />
  </div>
</template>
<script setup lang="ts">
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";

import { computed, onMounted, ref } from "vue";
import { useRoute } from "vue-router";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { useStimmzettelerfassungStatusTeamService } from "@/composables/dse/stimmzettelerfassungTeamStatusService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

const status = ref<StimmzettelerfassungTeamStatus | null>(null);
const erfassungDialog = ref(false);
const beendenDialog = ref(false);
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
    teamID,
    wahlID,
    wahlbezirkID,
    false
  );
  if (loaded) {
    status.value = loaded;
  }
}

onMounted(async () => {
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

async function startErfassung() {
  await postTeamStatus({
    status: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG,
  });
  erfassungDialog.value = true;
}

async function unterbrechen() {
  await postTeamStatus({
    status: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN,
  });
}

async function beenden() {
  beendenDialog.value = true;
  await postTeamStatus({
    status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN,
  });
}
</script>
