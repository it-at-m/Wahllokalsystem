<template>
  <v-card>
    <v-card-title>Stimmzettelerfassung</v-card-title>
    <v-card-text>
      <v-skeleton-loader boilerplate type="card-avatar" class="mt-3"/>
    </v-card-text>
    <v-card-actions>
      <base-text-button :active="startenBtnActive" @click="startErfassung">Starten</base-text-button>
      <base-text-button @click="unterbrechen">Unterbrechen</base-text-button>
      <base-text-button class="ms-auto" :active="beendenBtnActive" @click="beenden">Beenden</base-text-button>
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
import {ref, onMounted, computed} from "vue";
import { useRoute } from "vue-router";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import {
  useStimmzettelerfassungStatusTeamService
} from "@/composables/ergebnismeldung/common/erfassungTeamStatusService.ts";
import { ErfassungTeamStatusEnum } from "@/types/dse/ErfassungTeamStatusEnum";
import type { ErfassungTeamStatus } from "@/types/dse/ErfassungTeamStatus";

const status = ref<ErfassungTeamStatus>({ status: ErfassungTeamStatusEnum.REGISTRIERT });
const erfassungDialog = ref(false);
const beendenDialog = ref(false);
const erfassungTeamStatusService = useStimmzettelerfassungStatusTeamService();
import { useLogging } from "@/composables/common/logging.ts";

const route = useRoute();
const { logError } = useLogging("stimmzettelerfassungView");
const teamID = (route.params.teamID as string) || "";
const wahlID = (route.params.wahlID as string) || "";
const wahlbezirkID = (route.params.wahlbezirkID as string) || "";

const startenBtnActive = computed(() => status.value.status == ErfassungTeamStatusEnum.REGISTRIERT || ErfassungTeamStatusEnum.UNTERBROCHEN);
const beendenBtnActive = computed(() => status.value.status == ErfassungTeamStatusEnum.IN_BEARBEITUNG);

async function postStatus(sendNotification = true) {
  if (!teamID || !wahlID || !wahlbezirkID) {
    return;
  }

  await erfassungTeamStatusService.postErfassungTeamStatus(
    wahlID,
    wahlbezirkID,
    teamID,
    status.value,
    sendNotification
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
  } catch (error) {
    logError("Fehler beim Laden der ErfassungTeamStatus: ", error);
    throw error;
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