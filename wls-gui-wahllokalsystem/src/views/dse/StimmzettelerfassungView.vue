<template>
  <v-card>
    <v-card-title>Stimmzettelerfassung</v-card-title>
    <v-card-text>
      <v-skeleton-loader boilerplate type="card-avatar" class="mt-3"/>
    </v-card-text>
    <v-card-actions>
      <base-text-button :active="status.status !== ErfassungTeamStatusEnum.IN_BEARBEITUNG" @click="startErfassung">Starten</base-text-button>
      <base-text-button :active="status.status === ErfassungTeamStatusEnum.IN_BEARBEITUNG" @click="unterbrechen">Unterbrechen</base-text-button>
      <base-text-button class="ms-auto" :active="status.status === ErfassungTeamStatusEnum.ABGESCHLOSSEN" @click="beenden">Beenden</base-text-button>
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
  >
  </base-dialog>
  <base-dialog
      :visible="beendenDialog"
      icon="$information"
      dialogtext="🚧 TBD 🚧"
      dialogtitle="Stimmzettelerfassung beenden"
      cancel-disabled
      confirmtext="Ok"
      @confirm="beendenDialog = !beendenDialog"
  >
  </base-dialog>
</template>
<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import {
  useStimmzettelerfassungStatusTeamService
} from "@/composables/ergebnismeldung/common/erfassungTeamStatusService.ts";
import { ErfassungTeamStatusEnum } from "@/types/dse/ErfassungTeamStatusEnum";
import type { ErfassungTeamStatus } from "@/types/dse/ErfassungTeamStatus";

//TeamErfassungStatus: REGISTRIERT, IN_BEARBEITUNG,    UNTERBROCHEN,    ABGESCHLOSSEN
const status = ref<ErfassungTeamStatus>({ status: ErfassungTeamStatusEnum.REGISTRIERT });
const erfassungDialog = ref(false);
const beendenDialog = ref(false);
const erfassungTeamStatusService = useStimmzettelerfassungStatusTeamService();

const route = useRoute();
const teamID = (route.params.teamID as string) || "";
const wahlID = (route.params.wahlID as string) || "";
const wahlbezirkID = (route.params.wahlbezirkID as string) || "";

async function postStatus(sendNotification = true) {
  if (!teamID || !wahlID || !wahlbezirkID) {
    return;
  }

  await erfassungTeamStatusService.postErfassungTeamStatus(
    wahlID,
    wahlbezirkID,
    teamID,
    status.value,
    true
  );
}

onMounted(async () => {
  if (!teamID || !wahlID || !wahlbezirkID) {
    return;
  }
  try {
    const loaded = await erfassungTeamStatusService.loadErfassungTeamStatus(
      teamID,
      wahlID,
      wahlbezirkID
    );
    if (loaded) {
      status.value = loaded;
    }
  } catch (e) {
    // eslint-disable-next-line no-console
    console.error("Fehler beim Laden des ErfassungTeamStatus:", e);
  }
});

async function startErfassung() {
  status.value = { status: ErfassungTeamStatusEnum.IN_BEARBEITUNG };
  erfassungDialog.value = true;
  await postStatus();
}

async function unterbrechen() {
  status.value = { status: ErfassungTeamStatusEnum.UNTERBROCHEN };
  await postStatus();
}

async function beenden() {
  status.value = { status: ErfassungTeamStatusEnum.ABGESCHLOSSEN };
  beendenDialog.value = true;
  await postStatus();
}

</script>